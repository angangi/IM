package top.felixchang.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.GroupChatRequestMessage;
import top.felixchang.im.message.impl.GroupMembersRequestMessage;

/**
 * 群成员获取处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 */
@ChannelHandler.Sharable
public class GroupMembersMessageInboundHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        
    }
}
