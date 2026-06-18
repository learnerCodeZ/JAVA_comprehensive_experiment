package com.smarthome.service;

import com.smarthome.entity.OperationLog;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志服务类
 */
public class LogService {
    private List<OperationLog> logList;

    public LogService() {
        this.logList = new ArrayList<>();
    }

    /**
     * 添加日志
     */
    public void addLog(OperationLog log) {
        if (log != null) {
            logList.add(0, log);
            if (logList.size() > 100) {
                logList.remove(logList.size() - 1);
            }
        }
    }

    /**
     * 记录操作
     */
    public void log(String deviceName, String action) {
        OperationLog log = new OperationLog(
            String.valueOf(System.currentTimeMillis()),
            deviceName,
            action
        );
        addLog(log);
    }

    /**
     * 获取所有日志
     */
    public List<OperationLog> findAll() {
        return new ArrayList<>(logList);
    }

    /**
     * 获取最近N条日志
     */
    public List<OperationLog> findRecent(int count) {
        List<OperationLog> result = new ArrayList<>();
        int limit = Math.min(count, logList.size());
        for (int i = 0; i < limit; i++) {
            result.add(logList.get(i));
        }
        return result;
    }

    /**
     * 清空日志
     */
    public void clear() {
        logList.clear();
    }
}
