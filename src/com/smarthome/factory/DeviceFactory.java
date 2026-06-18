package com.smarthome.factory;

import com.smarthome.entity.*;

/**
 * 设备工厂类
 * 使用工厂模式创建设备对象
 */
public class DeviceFactory {

    /**
     * 创建设备
     * @param type 设备类型: light, ac, curtain, speaker
     * @param id 设备ID
     * @param name 设备名称
     * @return 设备对象
     */
    public static Device createDevice(String type, String id, String name) {
        switch (type.toLowerCase()) {
            case "light":
                return new Light(id, name);
            case "ac":
                return new AirConditioner(id, name);
            case "curtain":
                return new Curtain(id, name);
            case "speaker":
                return new Speaker(id, name);
            default:
                throw new IllegalArgumentException("未知设备类型: " + type);
        }
    }

    /**
     * 获取设备类型名称
     */
    public static String getDeviceTypeName(String type) {
        switch (type.toLowerCase()) {
            case "light":
                return "智能灯";
            case "ac":
                return "空调";
            case "curtain":
                return "智能窗帘";
            case "speaker":
                return "智能音箱";
            default:
                return "未知设备";
        }
    }
}
