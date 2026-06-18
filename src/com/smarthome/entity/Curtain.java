package com.smarthome.entity;

/**
 * 窗帘类
 * 继承Device，添加开合度属性
 */
public class Curtain extends Device {
    private int position;

    public Curtain() {
        super();
        setType("智能窗帘");
        this.position = 0;
    }

    public Curtain(String id, String name) {
        super(id, name, "智能窗帘");
        this.position = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if (position < 0) position = 0;
        if (position > 100) position = 100;
        this.position = position;
    }

    @Override
    public void control() {
        System.out.println("控制窗帘: " + getName());
        System.out.println("  开合度: " + position + "%");
    }

    @Override
    public String getInfo() {
        return String.format("%s | 开合度:%d%% | 状态:%s",
                getName(), position, getStatusText());
    }
}
