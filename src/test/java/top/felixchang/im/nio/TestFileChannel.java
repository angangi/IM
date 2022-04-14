package top.felixchang.im.nio;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static top.felixchang.im.nio.ByteBufferUtil.*;

public class TestFileChannel {
    @Test
    public void testTransfer() {
        try (
                FileChannel f1 = new FileInputStream("data.txt").getChannel();
                FileChannel f2 = new FileOutputStream("data2.txt").getChannel()
        ) {
//            ByteBuffer buffer = ByteBuffer.allocate(256);
//            f1.read(buffer);
//            buffer.flip();
//            while (buffer.hasRemaining()) {
//                f2.write(buffer);
//            }
            
            //最大2G
            //f1.transferTo(0, f1.size(), f2);
            //改进：支持超过2G
            long size = f1.size();
            for (long left = size; left > 0; ) {
                left -= f1.transferTo(size-left, left, f2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
