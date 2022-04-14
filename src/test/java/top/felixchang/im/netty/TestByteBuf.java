package top.felixchang.im.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(4);
        System.out.println(buf);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            builder.append("a");
        }
        buf.writeBytes(builder.toString().getBytes(Charset.defaultCharset()));
        System.out.println(buf);
    
        System.out.println(buf.readByte());
    }
}
