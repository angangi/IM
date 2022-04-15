package top.felixchang.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import top.felixchang.im.protocol.MessageCodecSharable;
import top.felixchang.im.protocol.ProcotolFrameDecoder;
import top.felixchang.im.server.handler.*;

@Slf4j
public class ImServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        
        //Handler
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        LoginRequestMessageInboundHandler LOGIN_HANDLER = new LoginRequestMessageInboundHandler();
        ChatRequestMessageInboundHandler CHAT_HANDLER = new ChatRequestMessageInboundHandler();
        GroupCreateRequestMessageInboundHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageInboundHandler();
        GroupJoinRequestMessageInboundHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageInboundHandler();
        GroupMembersMessageInboundHandler GROUP_MEMBERS_HANDLER = new GroupMembersMessageInboundHandler();
        GroupQuitRequestMessageInboundHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageInboundHandler();
        GroupChatRequestMessageInboundHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageInboundHandler();
    
        QuitHandler QUIT_HANDLER = new QuitHandler();
        //编解码器
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
                    
                    //空闲处理器
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用户触发的事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (IdleState.READER_IDLE == event.state()) {
                                //log.debug("已经5s没有读到数据了");
                                //直接关闭不太好，Client端配合了心跳机制
                                ctx.channel().close();
                            }
                        }
                    });
                    
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    ch.pipeline().addLast(QUIT_HANDLER);
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
