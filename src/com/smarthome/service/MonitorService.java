package com.smarthome.service;

import com.smarthome.entity.Device;
import java.util.List;

/**
 * 监控服务类
 * 使用多线程监控设备状态
 */
public class MonitorService implements Runnable {
    private DeviceService deviceService;
    private LogService logService;
    private volatile boolean running;
    private Thread monitorThread;

    public MonitorService(DeviceService deviceService, LogService logService) {
        this.deviceService = deviceService;
        this.logService = logService;
        this.running = false;
    }

    /**
     * 开始监控
     */
    public void startMonitor() {
        if (!running) {
            running = true;
            monitorThread = new Thread(this);
            monitorThread.setDaemon(true);
            monitorThread.start();
            System.out.println("监控服务已启动");
        }
    }

    /**
     * 停止监控
     */
    public void stopMonitor() {
        running = false;
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        System.out.println("监控服务已停止");
    }

    /**
     * 获取监控状态
     */
    public String getStatus() {
        return running ? "监控中" : "已停止";
    }

    @Override
    public void run() {
        while (running) {
            try {
                checkDevices();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 检查设备状态
     */
    private void checkDevices() {
        List<Device> devices = deviceService.findAll();
        int online = 0;
        int offline = 0;

        for (Device device : devices) {
            if (device.isStatus()) {
                online++;
            } else {
                offline++;
            }
        }

        System.out.println("[监控] 在线设备: " + online + ", 离线设备: " + offline);
    }
}
