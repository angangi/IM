package top.felixchang.im.server.session;

import java.nio.channels.Channel;
import java.util.List;
import java.util.Set;

/**
 * 聊天群会话的管理接口
 */
public interface GroupSession {
    /**
     * 新建聊天组
     * @param name 组名
     * @param members 成员名集合
     * @return 组名不存在则新建Group对象，存在则返回null
     */
    Group createGroup(String name, Set<String> members);
    
    /**
     * 加入聊天组
     * @param name 组名
     * @param member 成员名
     * @return 组名不存在则返回null，存在则返回Group对象
     */
    Group joinGroup(String name, String member);
    
    /**
     * 移除群成员
     * @param name 组名
     * @param member 成员名
     * @return 组名不存在返回null，存在返回Group对象（成员存在与否无所谓，会尝试进行成员删除）
     */
    Group removeMember(String name, String member);
    
    /**
     * 移除群组
     * @param name 群组名
     * @return 组名不存在返回null，存在返回Group对象
     */
    Group removeGroup(String name);
    
    /**
     * 获取群组成员
     * @param name 组名
     * @return 群组成员集合
     */
    Set<String> getMembers(String name);
    
    /**
     * 获取群组成员的channel（也就是往群组内发消息的时候调用）
     * @param name 群组名
     * @return 群成员对应的channel集合
     */
    List<Channel> getMemberChannel(String name);
}
