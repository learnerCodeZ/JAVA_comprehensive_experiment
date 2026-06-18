package com.smarthome.service;

import com.smarthome.entity.Room;
import com.smarthome.entity.Device;
import java.util.ArrayList;
import java.util.List;

/**
 * 房间服务类
 */
public class RoomService {
    private List<Room> roomList;

    public RoomService() {
        this.roomList = new ArrayList<>();
    }

    /**
     * 添加房间
     */
    public void addRoom(Room room) {
        if (room != null && !roomList.contains(room)) {
            roomList.add(room);
        }
    }

    /**
     * 删除房间
     */
    public boolean removeRoom(String roomId) {
        Room room = findById(roomId);
        if (room != null) {
            roomList.remove(room);
            return true;
        }
        return false;
    }

    /**
     * 根据ID查找房间
     */
    public Room findById(String roomId) {
        for (Room room : roomList) {
            if (room.getId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    /**
     * 获取所有房间
     */
    public List<Room> findAll() {
        return new ArrayList<>(roomList);
    }

    /**
     * 获取房间设备
     */
    public List<Device> getRoomDevices(String roomId) {
        Room room = findById(roomId);
        if (room != null) {
            return room.getDevices();
        }
        return new ArrayList<>();
    }

    /**
     * 获取房间总数
     */
    public int getRoomCount() {
        return roomList.size();
    }
}
