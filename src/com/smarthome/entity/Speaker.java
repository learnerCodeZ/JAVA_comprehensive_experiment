package com.smarthome.entity;

/**
 * 智能音箱类
 * 继承Device，添加音量和播放状态属性
 */
public class Speaker extends Device {
    private int volume;
    private boolean playing;

    public Speaker() {
        super();
        setType("智能音箱");
        this.volume = 50;
        this.playing = false;
    }

    public Speaker(String id, String name) {
        super(id, name, "智能音箱");
        this.volume = 50;
        this.playing = false;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if (volume < 0) volume = 0;
        if (volume > 100) volume = 100;
        this.volume = volume;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public void control() {
        System.out.println("控制音箱: " + getName());
        System.out.println("  音量: " + volume);
        System.out.println("  播放中: " + (playing ? "是" : "否"));
    }

    @Override
    public String getInfo() {
        return String.format("%s | 音量:%d | 播放:%s | 状态:%s",
                getName(), volume, playing ? "是" : "否", getStatusText());
    }
}
