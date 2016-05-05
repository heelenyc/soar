package heelenyc.soar.provider.remote.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import heelenyc.commonlib.NamedThreadFactory;
import heelenyc.soar.provider.remote.api.IRemoteServer;
import heelenyc.soar.provider.remote.tcp.coder.TcpCommandDecoder;
import heelenyc.soar.provider.remote.tcp.coder.TcpResponseEncoder;
import heelenyc.soar.provider.remote.tcp.handler.AbstractTcpCommandHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author yicheng
 * @since 2016年5月3日
 * 
 */
public class SimpleTcpServer implements IRemoteServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // private channle
    private EventLoopGroup bossGroup;
    private EventLoopGroup wokerGroup;
    private ServerBootstrap bootstrap;

    private AbstractTcpCommandHandler tcpHandler;

    /**
     * 
     */
    public SimpleTcpServer(AbstractTcpCommandHandler tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    @Override
    public void start(String hostPortstrString) throws Exception {
        try {

            String hostPort[] = hostPortstrString.split(":");
            String address = hostPort[0];
            int port = Integer.valueOf(hostPort[1]);

            bossGroup = new NioEventLoopGroup(10, new NamedThreadFactory("tcp-boss-evenloopgroup"));
            wokerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1, new NamedThreadFactory("tcp-worker-evenloopgroup"));
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class).localAddress(address, port).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(0, 0, 60)).addLast(new TcpCommandDecoder()).addLast(new TcpResponseEncoder()).addLast(tcpHandler);
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 200);
            bootstrap.option(ChannelOption.SO_RCVBUF, 256 * 1024);
            bootstrap.option(ChannelOption.SO_SNDBUF, 256 * 1024);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,
            // AdaptiveRecvByteBufAllocator.DEFAULT); // 动态缓冲区
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT); // 动态缓冲区
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

            // ChannelFuture f = b.bind(port).sync();
            bootstrap.bind(address, port).sync();
            logger.info("tcp server start at " + address + ":" + port);


        } catch (Exception e) {
            logger.error("tcp server start error at " + hostPortstrString, e);
            throw e;
        }
    }

    @Override
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (wokerGroup != null) {
            wokerGroup.shutdownGracefully();
        }
    }

}
