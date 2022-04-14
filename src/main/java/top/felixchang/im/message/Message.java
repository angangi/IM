package top.felixchang.im.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public abstract class Message implements Serializable {
    private int sequenceId;
    private int messageType;
    
    public abstract int getMessageType();
    
    public int getSequenceId() {
        return sequenceId;
    }
    
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }
    
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    
    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    
    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();
    static {
        messageClasses.put(0, top.felixchang.im.message.impl.LoginRequestMessage.class);
    }
    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }
}
