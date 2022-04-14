package top.felixchang.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.felixchang.im.message.impl.LoginRequestMessage;
import top.felixchang.im.protocol.MessageCodecSharable;
import top.felixchang.im.protocol.ProcotolFrameDecoder;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class ImClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            
            MessageCodecSharable CODEC = new MessageCodecSharable();
            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
            
            //The class NioSocketChannel implemented the interface SocketChannel
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(CODEC);
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
    
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("登录结果：" + msg);
                        }
    
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //连接建立后触发
                            new Thread(() -> {
                                Scanner sc = new Scanner(System.in);
                                System.out.println("输入用户名和密码");
                                String username = sc.next();
                                String password = sc.next();
                                
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                System.out.println(loginRequestMessage);
                                
                                ctx.writeAndFlush(loginRequestMessage);
    
                                System.out.println("等待");
                                try {
                                    System.in.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
    
                            }, "system in").start();
                            //super.channelActive(ctx);
                        }
    
                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            
                        }
    
                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8087).sync().channel();
            channel.closeFuture().sync();
    
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
