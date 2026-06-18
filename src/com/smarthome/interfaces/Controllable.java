package com.smarthome.interfaces;

/**
 * 可控制接口
 * 定义设备的统一控制行为
 */
public interface Controllable {
    void turnOn();
    void turnOff();
    void control();
}
