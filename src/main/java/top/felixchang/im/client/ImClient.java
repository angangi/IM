package top.felixchang.im.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
            AtomicBoolean EXIT = new AtomicBoolean(false);
            
            //The class NioSocketChannel implemented the interface SocketChannel
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(CODEC);
    
                    //空闲处理器
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用户触发的事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (IdleState.WRITER_IDLE == event.state()) {
                                //log.debug("已经3s没有写数据了，发送一个心跳包");
                                //直接关闭不太好
                                //ctx.channel().close();
                                //发送心跳包
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("{}", msg);
                            
                            //登录成功后，服务器会返回一个LoginResponseMessage消息，读取后允许channelActive中启用一个菜单线程处理用户输入
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
                            Thread thread = new Thread(() -> {
                                if(EXIT.get()) {
                                    return;
                                }
                                Scanner sc = new Scanner(System.in);
                                System.out.println("输入用户名和密码");
                                String username = sc.nextLine();
                                String password = sc.nextLine();
        
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                //System.out.println(loginRequestMessage);
        
                                ctx.writeAndFlush(loginRequestMessage);
        
                                System.out.println("登录消息发出去了，等待后序操作...");
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
    
                                if(EXIT.get()) {
                                    return;
                                }
        
                                while (true) {
                                    if(EXIT.get()) {
                                        return;
                                    }
                                    
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
                                        case "send":
                                            ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(chatRequestMessage);
                                            break;
                                        case "gsend":
                                            GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username, s[1], s[2]);
                                            ctx.writeAndFlush(groupChatRequestMessage);
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            //bugfix: 群聊中加入了自身用户名，但是这里有歧义：其实gcreate的时候可以加上自己的用户名
                                            set.add(username);
                                            GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1], set);
                                            ctx.writeAndFlush(groupCreateRequestMessage);
                                            break;
                                        case "gmembers":
                                            GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                            ctx.writeAndFlush(groupMembersRequestMessage);
                                            break;
                                        case "gjoin":
                                            GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupJoinRequestMessage);
                                            break;
                                        case "gquit":
                                            GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username, s[1]);
                                            ctx.writeAndFlush(groupQuitRequestMessage);
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                        default:
                                            System.out.println("you have input " + s[0]);
                                            break;
                                    }
                                }
        
                            }, "login");
                            //todo 全都是守护线程的时候jvm会退出
                            //thread.setDaemon(true);
                            thread.start();
                        }
    
                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            //这里需要把处理用户输入的线程结束掉，否则jvm不会退出
                            EXIT.set(true);
                        }
    
                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            //log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            log.debug("连接已经断开，按任意键退出..");
                            EXIT.set(true);
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
