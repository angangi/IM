package top.felixchang.im.message.impl;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import top.felixchang.im.message.Message;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestMessage extends Message {
    private String username;
    private String password;
    private String nickname;
    
    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
