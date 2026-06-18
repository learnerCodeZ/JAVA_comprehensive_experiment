package com.smarthome.interfaces;

/**
 * 可监控接口
 * 定义设备的状态监控行为
 */
public interface Monitorable {
    boolean isOnline();
    String getStatusReport();
}
