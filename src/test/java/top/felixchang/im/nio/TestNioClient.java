package top.felixchang.im.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestNioClient {
    public static void main(String[] args) throws Exception{
        SocketChannel sc = SocketChannel.open(new InetSocketAddress(7788));
        ByteBuffer buffer = ByteBuffer.allocate(256);
        System.out.println("hello");
    }
}

//sc.write(StandardCharsets.ISO_8859_1.encode("123"))