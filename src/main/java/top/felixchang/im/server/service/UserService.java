package top.felixchang.im.server.service;

public interface UserService {
    /**
     * 登录
     * @param username
     * @param password
     * @return 返回是否登录成功
     */
    boolean login(String username, String password);
}
