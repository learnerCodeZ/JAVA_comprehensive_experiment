package com.smarthome.entity;

/**
 * 用户类
 *
 * 角色说明：
 * - 管理员(admin)：拥有全部权限，可进行设备增删改查、用户管理、系统配置等操作
 * - 普通用户(user)：拥有基本权限，可查看设备状态、控制设备开关、查看日志等
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String role;

    public User() {
    }

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 验证登录
     */
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * 获取用户信息
     */
    public String getInfo() {
        return String.format("用户: %s | 角色: %s", username, role);
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
