package com.smarthome.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间类
 * 包含多个设备的组合关系
 */
public class Room {
    private String id;
    private String name;
    private List<Device> devices;

    public Room() {
        this.devices = new ArrayList<>();
    }

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.devices = new ArrayList<>();
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

    public List<Device> getDevices() {
        return devices;
    }

    /**
     * 添加设备到房间
     */
    public void addDevice(Device device) {
        if (device != null && !devices.contains(device)) {
            devices.add(device);
            device.setRoomId(this.id);
        }
    }

    /**
     * 从房间移除设备
     */
    public void removeDevice(Device device) {
        devices.remove(device);
        if (device != null) {
            device.setRoomId(null);
        }
    }

    /**
     * 根据ID查找设备
     */
    public Device findDevice(String deviceId) {
        for (Device device : devices) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    /**
     * 获取设备数量
     */
    public int getDeviceCount() {
        return devices.size();
    }

    @Override
    public String toString() {
        return name + " (" + devices.size() + "个设备)";
    }
}
