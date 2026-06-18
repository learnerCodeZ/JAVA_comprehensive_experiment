package com.smarthome.entity;

/**
 * 空调类
 * 继承Device，添加温度和模式属性
 */
public class AirConditioner extends Device {
    private int temperature;
    private String mode;

    public AirConditioner() {
        super();
        setType("空调");
        this.temperature = 26;
        this.mode = "制冷";
    }

    public AirConditioner(String id, String name) {
        super(id, name, "空调");
        this.temperature = 26;
        this.mode = "制冷";
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        if (temperature < 16) temperature = 16;
        if (temperature > 30) temperature = 30;
        this.temperature = temperature;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public void control() {
        System.out.println("控制空调: " + getName());
        System.out.println("  温度: " + temperature + "°C");
        System.out.println("  模式: " + mode);
    }

    @Override
    public String getInfo() {
        return String.format("%s | 温度:%d°C | 模式:%s | 状态:%s",
                getName(), temperature, mode, getStatusText());
    }
}
