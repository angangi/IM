package top.felixchang.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.felixchang.im.message.impl.LoginRequestMessage;
import top.felixchang.im.message.impl.LoginResponseMessage;
import top.felixchang.im.server.service.UserServiceFactory;
import top.felixchang.im.server.session.SessionFactory;

/**
 * 登录处理器，这个前面有ProcotolFrameDecoder的时候是可以共享的
 */
@ChannelHandler.Sharable
public class LoginRequestMessageInboundHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message = null;
        if (login) {
            message = new LoginResponseMessage(true, "成功");
            //登录后绑定channel和用户名
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            message = new LoginResponseMessage(false, "失败");
        }
        ctx.writeAndFlush(message);
    }
}
