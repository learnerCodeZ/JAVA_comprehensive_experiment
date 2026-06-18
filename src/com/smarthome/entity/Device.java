package com.smarthome.entity;

import com.smarthome.interfaces.Controllable;
import com.smarthome.interfaces.Monitorable;

/**
 * 设备抽象基类
 * 定义所有智能设备的共性属性和方法
 * 实现 Controllable 和 Monitorable 接口
 */
public abstract class Device implements Controllable, Monitorable {
    private String id;
    private String name;
    private boolean status;
    private String roomId;
    private String type;

    public Device() {
    }

    public Device(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 开启设备
     */
    public void turnOn() {
        this.status = true;
    }

    /**
     * 关闭设备
     */
    public void turnOff() {
        this.status = false;
    }

    /**
     * 获取设备状态描述
     */
    public String getStatusText() {
        return status ? "开启" : "关闭";
    }

    /**
     * 抽象方法：控制设备
     * 由子类实现具体的控制逻辑
     */
    public abstract void control();

    /**
     * 获取设备详细信息
     */
    public abstract String getInfo();

    @Override
    public boolean isOnline() {
        return this.status;
    }

    @Override
    public String getStatusReport() {
        return name + " [" + getType() + "] - " + getStatusText();
    }

    @Override
    public String toString() {
        return name + " [" + getType() + "] - " + getStatusText();
    }
}
