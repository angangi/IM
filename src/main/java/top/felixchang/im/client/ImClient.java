package top.felixchang.im.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.felixchang.im.message.impl.*;
import top.felixchang.im.protocol.MessageCodecSharable;
import top.felixchang.im.protocol.ProcotolFrameDecoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ImClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            
            //handler
            MessageCodecSharable CODEC = new MessageCodecSharable();
            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    
            //信号
            CountDownLatch WAIT_LOGIN = new CountDownLatch(1);
            AtomicBoolean LOGIN = new AtomicBoolean();
            
            //The class NioSocketChannel implemented the interface SocketChannel
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(CODEC);
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("{}", msg);
                            
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                                boolean success = responseMessage.isSuccess();
                                if (success) {
                                    LOGIN.set(true);
                                } else {
                                    LOGIN.set(false);
                                }
                                WAIT_LOGIN.countDown();
                            }
                        }
    
                        //连接建立后触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                Scanner sc = new Scanner(System.in);
                                System.out.println("输入用户名和密码");
                                String username = sc.nextLine();
                                String password = sc.nextLine();
                                
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                //System.out.println(loginRequestMessage);
                                
                                ctx.writeAndFlush(loginRequestMessage);
    
                                System.out.println("等待...");
                                //try {
                                //    System.in.read();
                                //} catch (IOException e) {
                                //    e.printStackTrace();
                                //}
                                try {
                                    WAIT_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                } else {
                                    System.out.println("登录成功");
                                }
    
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
    
                                    String command = sc.nextLine();
                                    String[] s = command.split(" ");
                                    
                                    System.out.println(Arrays.toString(s));
                                    
                                    switch (s[0]) {
                                        case "send" :
                                            ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(chatRequestMessage);
                                            break;
                                        case "gsend" :
                                            GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(groupChatRequestMessage);
                                            break;
                                        case "gcreate" :
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1], set);
                                            ctx.writeAndFlush(groupCreateRequestMessage);
                                            break;
                                        case "gmembers" :
                                            GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                            ctx.writeAndFlush(groupMembersRequestMessage);
                                            break;
                                        case "gjoin" :
                                            GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupJoinRequestMessage);
                                            break;
                                        case "gquit" :
                                            GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupQuitRequestMessage);
                                            break;
                                        case "quit" :
                                            ctx.channel().close();
                                            return;
                                        default:
                                            System.out.println("you have input " + s[0]);
                                            break;
                                    }
                                }
    
                            }, "login").start();
                        }
    
                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                        }
    
                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            //log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            log.debug("连接已经断开，按任意键退出..");
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
