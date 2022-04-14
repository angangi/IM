package top.felixchang.im.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static top.felixchang.im.nio.ByteBufferUtil.debugAll;

public class TestMultithreadSelector {
    public static void main(String[] args) throws Exception {
        Selector boss = Selector.open();
        Worker worker = new Worker();
        
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(7788));
        ssc.configureBlocking(false);
        ssc.register(boss, SelectionKey.OP_ACCEPT, null);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                SocketChannel sc = (SocketChannel) key.channel();
                sc.configureBlocking(false);
                worker.register(sc);
                iter.remove();
            }
        }
    }
    
    static class Worker implements Runnable {
        Thread thread;
        Selector selector;
        boolean flag = true;
        ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        
        public void register(SocketChannel sc) throws IOException {
            if (flag) {
                this.thread = new Thread(this);
                this.selector = Selector.open();
                this.thread.start();
                flag = false;
            }
            //可能卡在select上，到不了注册这里
            //sc.register(selector, SelectionKey.OP_READ, null);
            //唤醒下面的selector，使用线程间通信可以解决顺序问题
            queue.add(()->{
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //上面的队列也可以不用，直接wakeup相当于发一张门票，什么时候用都可以，每次调用这个register函数的时候run中select会被唤醒，然后
            //就可以继续执行channel的注册操作了
            selector.wakeup();
        }
        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Runnable runnable = queue.poll();
                if (runnable != null) runnable.run();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    try {
                        sc.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    buffer.flip();
                    debugAll(buffer);
                }
            }
        }
    }
}
