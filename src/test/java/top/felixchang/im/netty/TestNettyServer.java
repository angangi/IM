package top.felixchang.im.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class TestNettyServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                //类比包含selector和thread的Boss、Worker类，指定group组
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                //指定ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                //指定子处理器，ChannelInitializer是一个特殊的handler，用来添加其他handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //channel注册后调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder()); //ByteBuf -> 字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(7788);
    }
}
