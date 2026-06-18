package com.smarthome.exception;

/**
 * 设备异常类
 * 用于处理设备相关异常
 */
public class DeviceException extends Exception {
    private int errorCode;

    public DeviceException(String message) {
        super(message);
        this.errorCode = 1000;
    }

    public DeviceException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DeviceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 1000;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "DeviceException [错误码=" + errorCode + ", 消息=" + getMessage() + "]";
    }
}
