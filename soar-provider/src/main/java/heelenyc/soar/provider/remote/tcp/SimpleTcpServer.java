package heelenyc.soar.provider.remote.tcp;

import heelenyc.commonlib.NamedThreadFactory;
import heelenyc.soar.provider.remote.tcp.api.ITcpServer;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heelenyc.simpleredis.coder.RedisCommandDecoder;
import com.heelenyc.simpleredis.coder.RedisReplyEncoder;
import com.heelenyc.simpleredis.handler.AbstractRedisCommandHandler;

/**
 * @author yicheng
 * @since 2016年5月3日
 *
 */
public class SimpleTcpServer implements ITcpServer {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // private channle
    private EventLoopGroup bossGroup;
    private EventLoopGroup wokerGroup;
    private ServerBootstrap bootstrap;

    private AbstractTcpCommandHandler redisHandler;

    /**
     * 
     */
    public SimpleTcpServer(AbstractTcpCommandHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    @Override
    public void start(String hostPortstrString) throws Exception {
        try {

            String hostPort[] = hostPortstrString.split(":");
            String address = hostPort[0];
            int port = Integer.valueOf(hostPort[1]);

            bossGroup = new NioEventLoopGroup(10, new NamedThreadFactory("redis-boss-evenloopgroup"));
            wokerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1, new NamedThreadFactory("redis-worker-evenloopgroup"));
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class).localAddress(address, port).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(0, 0, 60)).addLast(new RedisCommandDecoder()).addLast(new RedisReplyEncoder()).addLast(redisHandler);
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
            logger.info("redis server start at " + address + ":" + port);

            // will block this thead
            // f.channel().closeFuture().sync();
            // logger.info("redis server shutdown!");

        } catch (Exception e) {
            logger.error("redis server start error at " + hostPortstrString, e);
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
