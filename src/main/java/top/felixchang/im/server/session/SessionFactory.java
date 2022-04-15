package top.felixchang.im.server.session;

import top.felixchang.im.server.session.impl.SessionMemImpl;

/**
 * Session工厂类，用于获取Session
 */
public abstract class SessionFactory {

    private static final Session session = new SessionMemImpl();
    public static Session getSession() {
        return session;
    }
}