package com.smarthome.service;

import com.smarthome.dao.UserDao;
import com.smarthome.entity.User;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务类
 * 通过UserDao持久化用户数据
 */
public class UserService {
    private List<User> userList;
    private User currentUser;
    private UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
        this.userList = userDao.findAll();
        // 如果JSON文件为空，初始化默认用户
        if (this.userList == null || this.userList.isEmpty()) {
            this.userList = new ArrayList<>();
            initDefaultUsers();
        }
    }

    /**
     * 初始化默认用户
     */
    private void initDefaultUsers() {
        userList.add(new User("1", "admin", "admin123", "管理员"));
        userList.add(new User("2", "user", "user123", "普通用户"));
        saveData();
    }

    /**
     * 保存数据到文件
     */
    private void saveData() {
        userDao.saveAll(userList);
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        for (User user : userList) {
            if (user.login(username, password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    /**
     * 用户登出
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 添加用户
     */
    public void addUser(User user) {
        if (user != null && !userList.contains(user)) {
            userList.add(user);
            saveData();
        }
    }

    /**
     * 获取所有用户
     */
    public List<User> findAll() {
        return new ArrayList<>(userList);
    }
}
