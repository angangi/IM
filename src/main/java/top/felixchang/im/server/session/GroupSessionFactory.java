package top.felixchang.im.server.session;

import top.felixchang.im.server.session.GroupSession;
import top.felixchang.im.server.session.impl.GroupSessionMemoryImpl;

public abstract class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}
