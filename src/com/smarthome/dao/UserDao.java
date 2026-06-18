package com.smarthome.dao;

import com.smarthome.config.AppConfig;
import com.smarthome.entity.User;
import com.smarthome.util.FileUtil;
import com.smarthome.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 * 负责用户数据的JSON文件读写
 */
public class UserDao {

    private String filePath;

    public UserDao() {
        this.filePath = AppConfig.getInstance().getDataDirectory() + "/users.json";
    }

    public List<User> findAll() {
        String json = FileUtil.readFile(filePath);
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<User> users = JsonUtil.fromJsonList(json, User.class);
        return users != null ? users : new ArrayList<>();
    }

    public void saveAll(List<User> users) {
        String json = JsonUtil.toJsonList(users);
        FileUtil.writeFile(filePath, json);
    }

    public User findByUsername(String username) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User findByUsernamePassword(String username, String password) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.login(username, password)) {
                return user;
            }
        }
        return null;
    }

    public void add(User user) {
        List<User> users = findAll();
        users.add(user);
        saveAll(users);
    }

    public void update(User user) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        saveAll(users);
    }
}
