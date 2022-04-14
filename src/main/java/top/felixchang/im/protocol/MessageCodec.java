package top.felixchang.im.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import top.felixchang.im.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 不可以在channel间共享！因为eventLoopGroup中的工人可能会收到半包，然后改变了Encoder的状态
 * 还要传递给下一次调用！这时候多线程同时调用就会导致混乱，解决办法是前面加一个LengthFieldDecoder，
 * 但是netty不知道我们加没加，所以要使用@Sharable注解标记，MessageCodec继承了ByteToMessageCodec，
 * ByteToMessageCodec不允许加@Sharable注解，所以我们写MessageCodecSharable类的时候就要继承一个
 * MessageToMessageCodec类了
 */
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //System.out.println("into encode");
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
    
    }
    
    /**
     *
     * @param ctx
     * @param in
     * @param out 可能会解析出多条！
     * @throws Exception
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //System.out.println("into decode");
        
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
        
        System.out.println(message.toString());
        
        out.add(message);
    }
}
