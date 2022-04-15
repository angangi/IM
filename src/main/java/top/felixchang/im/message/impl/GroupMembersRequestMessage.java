package top.felixchang.im.message.impl;

import lombok.Data;
import lombok.ToString;
import top.felixchang.im.message.Message;

@Data
@ToString(callSuper = true)
public class GroupMembersRequestMessage extends Message {
    private String groupName;

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GroupMembersRequestMessage;
    }
}
