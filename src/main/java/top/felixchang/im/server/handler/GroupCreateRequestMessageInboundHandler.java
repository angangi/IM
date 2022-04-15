package top.felixchang.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.GroupChatRequestMessage;
import top.felixchang.im.message.impl.GroupCreateRequestMessage;
import top.felixchang.im.message.impl.GroupCreateResponseMessage;
import top.felixchang.im.server.session.Group;
import top.felixchang.im.server.session.GroupSession;
import top.felixchang.im.server.session.GroupSessionFactory;

import java.util.List;
import java.util.Set;

/**
 * 群创建处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageInboundHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
    
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
    
        GroupCreateResponseMessage groupCreateResponseMessage = null;
        if (group == null) {
            //ctx的writeAndFlush是从当前handler直接发出这个消息，而channel的writeAndFlush是从整个pipline最后一个outhandler发出
            groupCreateResponseMessage = new GroupCreateResponseMessage(true, groupName + "创建完成");
            //通知到每一个群成员
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            GroupCreateResponseMessage responseMessage = new GroupCreateResponseMessage(true, "您已被拉入群聊" + groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(responseMessage);
            }
            
        } else {
            groupCreateResponseMessage = new GroupCreateResponseMessage(false, groupName + "创建失败");
        }
        ctx.writeAndFlush(groupCreateResponseMessage);
    }
}
