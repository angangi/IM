package top.felixchang.im.server.service;

import top.felixchang.im.server.service.impl.UserServiceMemImpl;

public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemImpl();

    public static UserService getUserService() {
        return userService;
    }
}
