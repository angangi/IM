package top.felixchang.im.server.service.impl;

import top.felixchang.im.server.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class UserServiceMemImpl implements UserService {
    private static Map<String, String> users = new HashMap<>();
    static {
        users.put("felix", "123");
        users.put("chang", "123");
    }
    
    @Override
    public boolean login(String username, String password) {
        return password.equals(users.get(username));
    }
}
