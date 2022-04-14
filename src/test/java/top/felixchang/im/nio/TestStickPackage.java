package top.felixchang.im.nio;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static top.felixchang.im.nio.ByteBufferUtil.*;
public class TestStickPackage {
    @Test
    public void testFlip2times() {
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.put("hello\nsdff\nfgd".getBytes());
        buffer.flip();
        buffer.flip(); //确实2次flip会清空，和clear差不多
    }
    
    @Test
    public void testSplit() {
        ByteBuffer encode = StandardCharsets.UTF_8.encode("abcsdfa\nsadfafg\nsdafsafgsaash\n");
        
        ByteBuffer buffer = ByteBuffer.allocate(30);
        buffer.put("hello\nsdff\nfgd".getBytes());
        buffer.put("dd\n".getBytes());
        split(buffer);
    }
    
    public static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if(buffer.get(i) == '\n') {
                int len = i - buffer.position() + 1;
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int i1 = 0; i1 < len; i1++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }
        buffer.compact();
    }
}
