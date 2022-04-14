package top.felixchang.im.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.FutureTask;

public class TestEventLoopGroup {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(5);
        group.next().submit(()->{
            try {
                Thread.sleep(1000);
                System.out.println("inner");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //group.next().scheduleAtFixedRate()
        System.out.println("outer");
    }
}
