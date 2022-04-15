package top.felixchang.im.server.session.impl;

import io.netty.channel.Channel;
import top.felixchang.im.server.session.Group;
import top.felixchang.im.server.session.GroupSession;
import top.felixchang.im.server.session.SessionFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupSessionMemoryImpl implements GroupSession {
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();
    
    @Override
    public Group createGroup(String name, Set<String> members) {
        //这里的逻辑不对，接口中应该是存在的时候直接返回null
        //不过考虑到ConcurrentHashMap的特点：为了线程安全只能忍一忍了，用这个putIfAbsent
        Group group = new Group(name, members);
        return groupMap.putIfAbsent(name, group);
    }
    
    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().add(member);
            return value;
        });
    }
    
    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key, value) -> {
            value.getMembers().remove(member);
            return value;
        });
    }
    
    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }
    
    @Override
    public Set<String> getMembers(String name) {
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }
    
    
    @Override
    public List<Channel> getMembersChannel(String name) {
        return getMembers(name).stream()
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
