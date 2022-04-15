package top.felixchang.im.server.service.impl;

import top.felixchang.im.server.service.UserService;

import java.util.HashMap;
import java.util.Map;


public class UserServiceMemImpl implements UserService {
    private static Map<String, String> users = new HashMap<>();
    static {
        users.put("u1", "123");
        users.put("u2", "123");
        users.put("u3", "123");
        users.put("u4", "123");
        users.put("u5", "123");
    }
    
    @Override
    public boolean login(String username, String password) {
        return password.equals(users.get(username));
    }
}
