package top.felixchang.im.message.impl;

import lombok.Data;
import lombok.ToString;
import top.felixchang.im.message.AbstractResponseMessage;

@Data
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupCreateResponseMessage;
    }
}
