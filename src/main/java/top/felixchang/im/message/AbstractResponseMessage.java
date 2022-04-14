package top.felixchang.im.message;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractResponseMessage extends Message {
    private boolean success;
    private String reason;
}
