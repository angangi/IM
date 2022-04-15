package top.felixchang.im.message.impl;

import top.felixchang.im.message.Message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
