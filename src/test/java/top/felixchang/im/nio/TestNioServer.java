package top.felixchang.im.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static top.felixchang.im.nio.ByteBufferUtil.*;
import static top.felixchang.im.nio.TestStickPackage.split;

public class TestNioServer {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
    
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(7788));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        
        
        while(true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()) {
                SelectionKey key = iter.next();
                //处理完就要删掉，即使读不完，还会触发一个read事件
                iter.remove();
                if(key.isAcceptable()) {
                    System.out.println("accept");
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    //挂载一个附件
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    SelectionKey register = accept.register(selector, SelectionKey.OP_READ, buffer);
                    //key.cancel();
                }
                else if(key.isReadable()) {
                    System.out.println("read");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    try {
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                            System.out.println("正常断开，返回-1");
                            //continue;
                        } else {
//                            buffer.flip();
//                            debugRead(buffer);
                            split(buffer);
                            //没压缩成功，说明要扩容
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer tbuf = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                tbuf.put(buffer);
                                key.attach(tbuf);
                            }
                        }
                        
                    } catch (Exception e) {
                        key.cancel();
                        e.printStackTrace();
                    }
                }
            }
        }
        
        
//        ssc.configureBlocking(false);
//        List<SocketChannel> channels = new ArrayList<>();
//        while (true) {
//            SocketChannel channel = ssc.accept();
//            if(channel != null) {
//                System.out.println("accepted");
//                channel.configureBlocking(false);
//                channels.add(channel);
//            }

//            for (SocketChannel sc : channels) {
//                ByteBuffer buffer = ByteBuffer.allocate(256);
//                int read = sc.read(buffer);
//                if (read > 0) {
//                    buffer.flip();
//                    debugRead(buffer);
//                }
//            }
//        }
    }
}
