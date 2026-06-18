package com.smarthome.entity;

/**
 * 操作日志类
 */
public class OperationLog {
    private String id;
    private String deviceName;
    private String action;
    private long timestamp;

    public OperationLog() {
    }

    public OperationLog(String id, String deviceName, String action) {
        this.id = id;
        this.deviceName = deviceName;
        this.action = action;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new java.util.Date(timestamp));
        return time + " - " + action + " " + deviceName;
    }
}
