package com.smarthome.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smarthome.config.AppConfig;
import com.smarthome.entity.*;
import com.smarthome.util.FileUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备数据访问对象
 * 负责设备数据的JSON文件读写
 */
public class DeviceDao {

    private String filePath;
    private Gson gson;

    public DeviceDao() {
        this.filePath = AppConfig.getInstance().getDataDirectory() + "/devices.json";
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Device> findAll() {
        String json = FileUtil.readFile(filePath);
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // 读取原始JSON列表，根据type字段重建子类实例
        List<DeviceData> dataList = gson.fromJson(json,
                new TypeToken<List<DeviceData>>() {}.getType());
        if (dataList == null) {
            return new ArrayList<>();
        }
        List<Device> devices = new ArrayList<>();
        for (DeviceData data : dataList) {
            Device device = dataToDevice(data);
            if (device != null) {
                devices.add(device);
            }
        }
        return devices;
    }

    public void saveAll(List<Device> devices) {
        List<DeviceData> dataList = new ArrayList<>();
        for (Device device : devices) {
            dataList.add(deviceToData(device));
        }
        String json = gson.toJson(dataList);
        FileUtil.writeFile(filePath, json);
    }

    public void add(Device device) {
        List<Device> devices = findAll();
        devices.add(device);
        saveAll(devices);
    }

    public void update(Device device) {
        List<Device> devices = findAll();
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getId().equals(device.getId())) {
                devices.set(i, device);
                break;
            }
        }
        saveAll(devices);
    }

    public void delete(String deviceId) {
        List<Device> devices = findAll();
        devices.removeIf(d -> d.getId().equals(deviceId));
        saveAll(devices);
    }

    /**
     * 内部数据传输对象，用于JSON序列化
     */
    private static class DeviceData {
        String id, name, type, roomId;
        boolean status;
        int brightness, temperature, position, volume;
        String color, mode;
        boolean playing;
    }

    private DeviceData deviceToData(Device device) {
        DeviceData data = new DeviceData();
        data.id = device.getId();
        data.name = device.getName();
        data.type = device.getType();
        data.roomId = device.getRoomId();
        data.status = device.isStatus();

        if (device instanceof Light) {
            Light light = (Light) device;
            data.brightness = light.getBrightness();
            data.color = light.getColor();
        } else if (device instanceof AirConditioner) {
            AirConditioner ac = (AirConditioner) device;
            data.temperature = ac.getTemperature();
            data.mode = ac.getMode();
        } else if (device instanceof Curtain) {
            Curtain curtain = (Curtain) device;
            data.position = curtain.getPosition();
        } else if (device instanceof Speaker) {
            Speaker speaker = (Speaker) device;
            data.volume = speaker.getVolume();
            data.playing = speaker.isPlaying();
        }
        return data;
    }

    private Device dataToDevice(DeviceData data) {
        Device device = null;
        switch (data.type) {
            case "智能灯":
                Light light = new Light(data.id, data.name);
                light.setBrightness(data.brightness);
                light.setColor(data.color);
                device = light;
                break;
            case "空调":
                AirConditioner ac = new AirConditioner(data.id, data.name);
                ac.setTemperature(data.temperature);
                ac.setMode(data.mode);
                device = ac;
                break;
            case "智能窗帘":
                Curtain curtain = new Curtain(data.id, data.name);
                curtain.setPosition(data.position);
                device = curtain;
                break;
            case "智能音箱":
                Speaker speaker = new Speaker(data.id, data.name);
                speaker.setVolume(data.volume);
                speaker.setPlaying(data.playing);
                device = speaker;
                break;
        }
        if (device != null) {
            device.setStatus(data.status);
            device.setRoomId(data.roomId);
        }
        return device;
    }
}
