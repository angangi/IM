package top.felixchang.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import top.felixchang.im.protocol.MessageCodecSharable;
import top.felixchang.im.protocol.ProcotolFrameDecoder;
import top.felixchang.im.server.handler.ChatRequestMessageInboundHandler;
import top.felixchang.im.server.handler.LoginRequestMessageInboundHandler;

public class ImServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        LoginRequestMessageInboundHandler LOGIN_HANDLER = new LoginRequestMessageInboundHandler();
        ChatRequestMessageInboundHandler CHAT_HANDLER = new ChatRequestMessageInboundHandler();
        MessageCodecSharable CODEC = new MessageCodecSharable();
        
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            
            //The class NioSocketChannel implemented the interface SocketChannel
            //The class NioServerSocketChannel implemented the interface ServerSocketChannel
    
            /**
             * bugfix: 这里serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {})
             * 泛型类型是SocketChannel，NioServerSocketChannel只在serverBootstrap.channel(NioServerSocketChannel.class)
             * 中用到了
             */
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(CODEC);
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(CHAT_HANDLER);
                }
            });
            
            //这句话是什么意思？多个连接可以执行吗？
            Channel channel = serverBootstrap.bind(8087).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
    
}
