# 智能家居系统 - 类设计图

## 一、实体类层次结构

### 1. 设备类（继承）

```
    ┌─────────────────────────────┐
    │      <<abstract>>           │
    │         Device              │
    ├─────────────────────────────┤
    │ - id: String                │
    │ - name: String              │
    │ - status: boolean           │
    │ - room: Room                │
    ├─────────────────────────────┤
    │ + turnOn(): void            │
    │ + turnOff(): void           │
    │ + getStatus(): boolean      │
    │ + getInfo(): String         │
    │ + control(): void           │
    └──────────────┬──────────────┘
                   │ extends
       ┌───────────┼───────────┬────────────┐
       ▼           ▼           ▼            ▼
   ┌───────┐  ┌────────┐  ┌────────┐  ┌─────────┐
   │ Light │  │   AC   │  │Curtain │  │ Speaker │
   ├───────┤  ├────────┤  ├────────┤  ├─────────┤
   │-bright│  │-temp   │  │-pos    │  │-volume  │
   │-color │  │-mode   │  │        │  │-playing │
   ├───────┤  ├────────┤  ├────────┤  ├─────────┤
   │control│  │control │  │control │  │ control │
   └───────┘  └────────┘  └────────┘  └─────────┘
```

### 2. 房间类（组合）

```
    ┌─────────────────────────────┐
    │          Room               │
    ├─────────────────────────────┤
    │ - id: String                │
    │ - name: String              │
    │ - devices: List<Device>     │
    ├─────────────────────────────┤
    │ + addDevice(): void         │
    │ + removeDevice(): void      │
    │ + getDevices(): List        │
    │ + getDeviceCount(): int     │
    └─────────────────────────────┘
```

### 3. 用户类

```
    ┌─────────────────────────────┐
    │          User               │
    ├─────────────────────────────┤
    │ - id: String                │
    │ - username: String          │
    │ - password: String          │
    │ - role: String              │
    ├─────────────────────────────┤
    │ + login(): boolean          │
    │ + getInfo(): String         │
    └─────────────────────────────┘
```

### 4. 日志类

```
    ┌─────────────────────────────┐
    │        OperationLog         │
    ├─────────────────────────────┤
    │ - id: String                │
    │ - deviceName: String        │
    │ - action: String            │
    │ - timestamp: long           │
    ├─────────────────────────────┤
    │ + toString(): String        │
    └─────────────────────────────┘
```

---

## 二、服务类

```
    ┌─────────────────────────────┐
    │       DeviceService         │
    ├─────────────────────────────┤
    │ - deviceList: List<Device>  │
    ├─────────────────────────────┤
    │ + addDevice(): void         │
    │ + removeDevice(): void      │
    │ + updateDevice(): void      │
    │ + findById(): Device        │
    │ + findAll(): List           │
    │ + turnOn(): void            │
    │ + turnOff(): void           │
    └─────────────────────────────┘
```

```
    ┌─────────────────────────────┐
    │        RoomService          │
    ├─────────────────────────────┤
    │ - roomList: List<Room>      │
    ├─────────────────────────────┤
    │ + addRoom(): void           │
    │ + removeRoom(): void        │
    │ + getRoomDevices(): List    │
    └─────────────────────────────┘
```

```
    ┌─────────────────────────────┐
    │       <<interface>>         │
    │     MonitorService          │
    ├─────────────────────────────┤
    │ + startMonitor(): void      │
    │ + stopMonitor(): void       │
    │ + getStatus(): String       │
    └─────────────────────────────┘
```

---

## 三、工厂类

```
    ┌─────────────────────────────┐
    │       DeviceFactory         │
    ├─────────────────────────────┤
    ├─────────────────────────────┤
    │ + createDevice(type): Device│
    └─────────────────────────────┘
```

---

## 四、异常类

```
    ┌─────────────────────────────┐
    │      <<extends>>           │
    │     DeviceException        │
    ├─────────────────────────────┤
    │ - errorCode: int            │
    ├─────────────────────────────┤
    │ + getErrorCode(): int       │
    └─────────────────────────────┘
```

---

## 五、类关系总结

| 关系类型 | 类A | 类B | 说明 |
|---------|-----|-----|------|
| 继承 | Device | Light/AC/Curtain/Speaker | 设备子类 |
| 组合 | Room | Device | 房间包含设备 |
| 依赖 | DeviceService | Device | 服务操作实体 |
| 依赖 | DeviceFactory | Device | 工厂创建设备 |
| 实现 | MonitorService | Runnable | 多线程监控 |
