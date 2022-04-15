package top.felixchang.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.ChatRequestMessage;
import top.felixchang.im.message.impl.ChatResponseMessage;
import top.felixchang.im.message.impl.GroupChatRequestMessage;
import top.felixchang.im.message.impl.GroupChatResponseMessage;
import top.felixchang.im.server.session.GroupSession;
import top.felixchang.im.server.session.GroupSessionFactory;
import top.felixchang.im.server.session.SessionFactory;

import java.util.List;

/**
 * 群消息处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageInboundHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String from = msg.getFrom();
        String content = msg.getContent();
        
        //获取群聊的所有成员
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> membersChannels = groupSession.getMembersChannel(groupName);
    
        //往每个群聊的成员发送消息
        for (Channel channel : membersChannels) {
            GroupChatResponseMessage responseMessage = new GroupChatResponseMessage(from, content);
            channel.writeAndFlush(responseMessage);
        }
    }
}
