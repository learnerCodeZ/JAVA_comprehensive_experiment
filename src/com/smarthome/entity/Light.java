package com.smarthome.entity;

/**
 * 智能灯类
 * 继承Device，添加亮度和颜色属性
 */
public class Light extends Device {
    private int brightness;
    private String color;

    public Light() {
        super();
        setType("智能灯");
        this.brightness = 100;
        this.color = "白色";
    }

    public Light(String id, String name) {
        super(id, name, "智能灯");
        this.brightness = 100;
        this.color = "白色";
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        if (brightness < 0) brightness = 0;
        if (brightness > 100) brightness = 100;
        this.brightness = brightness;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public void control() {
        System.out.println("控制智能灯: " + getName());
        System.out.println("  亮度: " + brightness + "%");
        System.out.println("  颜色: " + color);
    }

    @Override
    public String getInfo() {
        return String.format("%s | 亮度:%d%% | 颜色:%s | 状态:%s",
                getName(), brightness, color, getStatusText());
    }
}
