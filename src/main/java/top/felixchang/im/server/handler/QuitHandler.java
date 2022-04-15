package top.felixchang.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.felixchang.im.server.session.Session;
import top.felixchang.im.server.session.SessionFactory;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * 连接正常断开时（Client调用ctx.channel().close()）触发，从session管理器中删除这个channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = SessionFactory.getSession();
        session.unbind(ctx.channel());
        log.debug("{} 已经断开", ctx.channel());
    }
    
    /**
     * 连接异常断开
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session session = SessionFactory.getSession();
        session.unbind(ctx.channel());
        log.debug("{} 出现异常，已经断开，原因是 {}", ctx.channel(), cause.getMessage());
    }
}
