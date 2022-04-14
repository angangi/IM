package top.felixchang.im.nettyadvance;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

import org.junit.jupiter.api.Test;
import top.felixchang.im.message.impl.LoginRequestMessage;
import top.felixchang.im.protocol.MessageCodec;


public class TestMessageCodec {
    EmbeddedChannel channel = new EmbeddedChannel(
            new LoggingHandler(),
            new LengthFieldBasedFrameDecoder(
                    1024,
                    12,
                    4,
                    0,
                    0
            ),
            new MessageCodec()
    );
    
    @Test
    void testEncode() {
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123", "zhang");
        channel.writeOutbound(message);
    }
    
    @Test
    void testDecode() throws Exception {
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123", "zhang");
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        ByteBuf slice1 = buf.slice(0, 100);
        ByteBuf slice2 = buf.slice(100, buf.readableBytes() - 100);
        slice1.retain();
        channel.writeInbound(slice1);
        channel.writeInbound(slice2);
        
        /*
        * 明显可以看到，即使是半包，log会正常打印，但是其后的LengthFieldBasedFrameDecoder会凑成
        * 一个完整的包传给后面
        * */
    }
}
