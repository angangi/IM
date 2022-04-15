package top.felixchang.im.server.session.impl;

import io.netty.channel.Channel;
import top.felixchang.im.server.session.Session;

import java.util.HashMap;
import java.util.Map;

public class SessionMemImpl implements Session {
    private static final Map<String, Channel> usernameToChannelMap = new HashMap<>();
    private static final Map<Channel, String> channelToUsernameMap = new HashMap<>();
    
    @Override
    public void bind(Channel channel, String username) {
        usernameToChannelMap.put(username, channel);
        channelToUsernameMap.put(channel, username);
    }
    
    @Override
    public void unbind(Channel channel) {
        String username = channelToUsernameMap.remove(channel);
        usernameToChannelMap.remove(username);
        //todo channelToUsernameMap也需要删除
    }
    
    @Override
    public Channel getChannel(String username) {
        return usernameToChannelMap.get(username);
    }
}
