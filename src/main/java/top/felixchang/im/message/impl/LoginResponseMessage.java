package top.felixchang.im.message.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import top.felixchang.im.message.AbstractResponseMessage;

@NoArgsConstructor
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {
    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }
    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
