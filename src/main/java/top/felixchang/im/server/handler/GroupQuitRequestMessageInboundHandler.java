package top.felixchang.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.GroupChatRequestMessage;
import top.felixchang.im.message.impl.GroupQuitRequestMessage;

/**
 * 群退出处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 * TODO
 */
@ChannelHandler.Sharable
public class GroupQuitRequestMessageInboundHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        
    }
}
