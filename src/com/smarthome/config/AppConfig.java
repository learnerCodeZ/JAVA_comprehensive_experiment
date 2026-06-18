package com.smarthome.config;

import com.smarthome.util.FileUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 应用配置管理（单例模式）
 */
public class AppConfig {

    private static AppConfig instance;
    private Properties properties;

    private AppConfig() {
        properties = new Properties();
        String configPath = "config/config.properties";
        if (FileUtil.exists(configPath)) {
            try (FileInputStream fis = new FileInputStream(configPath)) {
                properties.load(fis);
            } catch (IOException e) {
                loadDefaults();
            }
        } else {
            loadDefaults();
        }
    }

    private void loadDefaults() {
        properties.setProperty("monitor.interval", "5000");
        properties.setProperty("data.directory", "data");
        properties.setProperty("config.directory", "config");
        properties.setProperty("max.log.count", "100");
        properties.setProperty("server.port", "8888");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public int getMonitorInterval() {
        return Integer.parseInt(properties.getProperty("monitor.interval", "5000"));
    }

    public String getDataDirectory() {
        return properties.getProperty("data.directory", "data");
    }

    public String getConfigDirectory() {
        return properties.getProperty("config.directory", "config");
    }

    public int getMaxLogCount() {
        return Integer.parseInt(properties.getProperty("max.log.count", "100"));
    }

    public int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "8888"));
    }
}
