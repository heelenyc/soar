package heelenyc.soar.consumer.remote.tcp;

import heelenyc.soar.consumer.remote.tcp.coder.TcpRequestEncoder;
import heelenyc.soar.consumer.remote.tcp.coder.TcpResponseDecoder;
import heelenyc.soar.consumer.remote.tcp.handler.TcpChannelHandler;
import heelenyc.soar.core.api.bean.ProtocolToken;
import heelenyc.soar.core.api.bean.Request;
import heelenyc.soar.core.api.bean.RequestBytePacket;
import heelenyc.soar.core.api.bean.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yicheng
 * @since 2016年5月4日
 * 
 */
public class TcpCallClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup();

    private Bootstrap bootstrap;
    private volatile Channel channel;

    private volatile boolean isConnected = false;
    private volatile boolean isWorking = false;
    private Map<Long, Object> syncLock = new ConcurrentHashMap<Long, Object>();
    private Map<Long, Response> syncResponse = new ConcurrentHashMap<Long, Response>();

    public TcpCallClient(final int port, final String host) throws Exception {

        // 配置客户端NIO线程组

        try {
            final TcpCallClient parent = this;
            bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TcpRequestEncoder());
                    ch.pipeline().addLast(new TcpResponseDecoder());
                    ch.pipeline().addLast(new TcpChannelHandler(parent));
                }
            });
            // 发起异步连接操作
            connect(port, host);

            executor.scheduleWithFixedDelay(new Runnable() {
                
                @Override
                public void run() {
                    // 监控重连
                    if (isWorking() && !isConnected()) {
                        // 还在工作，不是主动close的时候 就需要考虑重连
                        try {
                            reconnect(port, host);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }, 5, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            channel = null;
        } finally {
        }
    }

    private void connect(int port, String host) throws InterruptedException {
        
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
        channel = future.channel();
        if (channel.isActive()) {
            setConnected(true);
            setWorking(true);
        }
    }
    
    private void reconnect(int port, String host) throws InterruptedException {
        if (channel != null && channel.isActive()) {
            setConnected(true);
            setWorking(true);
            return;
        }
        logger.warn("attempt to reconnect  " + host + ":" + port);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        channel = future.channel();
        if (channel.isActive()) {
            setConnected(true);
            setWorking(true);
        }
    }

    /**
     * 关键是如何同步拿到结果
     * @param req
     * @return
     * @throws InterruptedException 
     * @throws Exception
     */
    public Response sendRequest(Request req) throws InterruptedException, Exception  {
        if (channel != null) {
            Object lock = new Object();
            syncLock.put(req.getId(),lock );
            channel.writeAndFlush(new RequestBytePacket(req)).sync();
            try {
                synchronized (lock) {
                    lock.wait(ProtocolToken.TIME_OUT);
                }
                Response response = syncResponse.remove(req.getId());
                syncLock.remove(req.getId());
                //lock = null;
                
                return response;
            } catch (InterruptedException e) {
                // TODO: 超时
                return Response.TIMEOUT_RESP;
            }
            
        } else {
            return Response.ERROR_RESP;
        }
    }

    /**
     * @throws InterruptedException
     * 
     */
    public void close() throws InterruptedException {
        setWorking(false);
        if (channel != null) {
            channel.close().sync();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Map<Long, Object> getSyncLock() {
        return syncLock;
    }

    public Map<Long, Response> getSyncResponse() {
        return syncResponse;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

}
