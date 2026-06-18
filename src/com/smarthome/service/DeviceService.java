package com.smarthome.service;

import com.smarthome.dao.DeviceDao;
import com.smarthome.entity.Device;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备服务类
 * 管理设备的增删改查操作，通过DeviceDao持久化数据
 */
public class DeviceService {
    private List<Device> deviceList;
    private DeviceDao deviceDao;

    public DeviceService() {
        this.deviceDao = new DeviceDao();
        this.deviceList = deviceDao.findAll();
        // 如果JSON文件为空，使用空列表
        if (this.deviceList == null) {
            this.deviceList = new ArrayList<>();
        }
    }

    /**
     * 保存数据到文件
     */
    private void saveData() {
        deviceDao.saveAll(deviceList);
    }

    /**
     * 添加设备
     */
    public void addDevice(Device device) {
        if (device != null && !deviceList.contains(device)) {
            deviceList.add(device);
            saveData();
        }
    }

    /**
     * 删除设备
     */
    public boolean removeDevice(String deviceId) {
        Device device = findById(deviceId);
        if (device != null) {
            deviceList.remove(device);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * 根据ID查找设备
     */
    public Device findById(String deviceId) {
        for (Device device : deviceList) {
            if (device.getId().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    /**
     * 获取所有设备
     */
    public List<Device> findAll() {
        return new ArrayList<>(deviceList);
    }

    /**
     * 根据房间ID获取设备列表
     */
    public List<Device> findByRoomId(String roomId) {
        List<Device> result = new ArrayList<>();
        for (Device device : deviceList) {
            if (roomId.equals(device.getRoomId())) {
                result.add(device);
            }
        }
        return result;
    }

    /**
     * 开启设备
     */
    public boolean turnOn(String deviceId) {
        Device device = findById(deviceId);
        if (device != null) {
            device.turnOn();
            saveData();
            return true;
        }
        return false;
    }

    /**
     * 关闭设备
     */
    public boolean turnOff(String deviceId) {
        Device device = findById(deviceId);
        if (device != null) {
            device.turnOff();
            saveData();
            return true;
        }
        return false;
    }

    /**
     * 获取设备总数
     */
    public int getDeviceCount() {
        return deviceList.size();
    }

    /**
     * 获取在线设备数量
     */
    public int getOnlineCount() {
        int count = 0;
        for (Device device : deviceList) {
            if (device.isStatus()) {
                count++;
            }
        }
        return count;
    }
}
