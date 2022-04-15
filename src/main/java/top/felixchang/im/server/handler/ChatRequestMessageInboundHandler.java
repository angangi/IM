package top.felixchang.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.ChatRequestMessage;
import top.felixchang.im.message.impl.ChatResponseMessage;
import top.felixchang.im.message.impl.LoginRequestMessage;
import top.felixchang.im.message.impl.LoginResponseMessage;
import top.felixchang.im.server.service.UserServiceFactory;
import top.felixchang.im.server.session.SessionFactory;

/**
 * 单聊处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 */
@ChannelHandler.Sharable
public class ChatRequestMessageInboundHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String to = msg.getTo();
        String content = msg.getContent();
    
        Channel toChannel = SessionFactory.getSession().getChannel(to);
        
        if(toChannel != null) {
            //给接收方发送一个单聊回应
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage(from, content);
            toChannel.writeAndFlush(chatResponseMessage);
        } else {
            //没法发送，给发送方发送一个单聊回应
            ctx.channel().writeAndFlush(new ChatResponseMessage(false, "找不到对方channel"));
        }
        
    
    }
}
