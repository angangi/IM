package top.felixchang.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.GroupChatRequestMessage;
import top.felixchang.im.message.impl.GroupJoinRequestMessage;

/**
 * 群加入处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 * TODO
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageInboundHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        
    }
}
