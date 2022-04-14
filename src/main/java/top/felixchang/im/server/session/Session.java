package top.felixchang.im.server.session;

import io.netty.channel.Channel;

public interface Session {
    /**
     * 绑定channel和username
     * @param channel
     * @param username
     */
    void bind(Channel channel, String username);
    
    /**
     * 解绑channel
     * @param channel
     */
    void unbind(Channel channel);
    
    /**
     * 根据用户名获取channel
     * @param username
     * @return
     */
    Channel getChannel(String username);
}
