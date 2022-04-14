package top.felixchang.im.nio;

import static top.felixchang.im.nio.ByteBufferUtil.*;
import org.junit.jupiter.api.Test;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestByteBuffer {
    @Test
    public void testScatteringRead() throws Exception {
        ByteBuffer buffer1 = ByteBuffer.allocate(3);
        ByteBuffer buffer2 = ByteBuffer.allocate(3);
        ByteBuffer buffer3 = ByteBuffer.allocate(3);
        //java的相对路径根目录就是项目目录而非src中的main、test，classpath本身和项目目录是无关的，idea自动会把target中的package路径加入到classpath中
        RandomAccessFile rw = new RandomAccessFile("data.txt", "rw");
        rw.getChannel().read(new ByteBuffer[]{buffer1, buffer2, buffer3});
        debugAll(buffer1);
        debugAll(buffer2);
        debugAll(buffer3);
        RandomAccessFile rw2 = new RandomAccessFile("data2.txt", "rw");
        rw2.getChannel().write(new ByteBuffer[]{buffer1, buffer2, buffer3});
    }
    
    @Test
    public void testString() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("你好".getBytes());
        debugAll(buffer);
        ByteBuffer hello = StandardCharsets.UTF_8.encode("hello");
        debugAll(hello);
        System.out.println(StandardCharsets.UTF_8.decode(hello));
    }
    
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{0x61, 0x62});
        buffer.flip();
        System.out.println((char) buffer.get());
        buffer.rewind();
        System.out.println((char) buffer.get());
        
        
    }
}
