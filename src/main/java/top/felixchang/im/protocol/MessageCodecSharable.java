package top.felixchang.im.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import top.felixchang.im.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 必须配合粘包半包处理器
 */
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        /*
         * 16字节
         * */
        //魔数 4
        out.writeBytes(new byte[]{1,2,3,4});
        //版本 1
        out.writeByte(1);
        //序列化方式 jdk=0 1
        out.writeByte(0);
        //指令类型 1
        out.writeByte(msg.getMessageType());
        //指令序号 4
        out.writeInt(msg.getSequenceId());
    
        //补齐16字节的头
        out.writeByte(0xff);
    
        //正文
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        byte[] bytes = baos.toByteArray();
    
        // 正文长度 4
        out.writeInt(bytes.length);
        // 正文
        out.writeBytes(bytes);
        outList.add(out);
    }
    
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
    
        in.readByte();//补齐填充
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) objectInputStream.readObject();
    
        //System.out.println(message.toString());
    
        out.add(message);
    }
}
