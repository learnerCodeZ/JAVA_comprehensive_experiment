# 智能家居设备管理系统 (SmartHome Controller)

> Java程序设计综合实验 | 完成日期：2026年6月18日

## 项目简介

基于 Java SE 的智能家居设备管理系统，模拟管理家庭中的智能设备。通过面向对象设计、多线程监控、GUI可视化等技术，实现设备管理、状态监控、场景控制等功能。

## 项目结构

```
JAVA_comprehensive_experiment/
├── src/                        # 源代码目录
│   └── com/smarthome/
│       ├── entity/             # 实体类
│       │   ├── Device.java         # 设备抽象基类（实现Controllable, Monitorable接口）
│       │   ├── Light.java          # 智能灯
│       │   ├── AirConditioner.java # 空调
│       │   ├── Curtain.java        # 窗帘
│       │   ├── Speaker.java        # 智能音箱
│       │   ├── Room.java           # 房间
│       │   ├── User.java           # 用户
│       │   └── OperationLog.java   # 操作日志
│       ├── interfaces/          # 接口定义
│       │   ├── Controllable.java   # 可控制接口
│       │   └── Monitorable.java    # 可监控接口
│       ├── service/            # 业务逻辑层
│       │   ├── DeviceService.java      # 设备服务
│       │   ├── RoomService.java        # 房间服务
│       │   ├── UserService.java        # 用户服务
│       │   ├── MonitorService.java     # 监控服务（多线程）
│       │   └── LogService.java         # 日志服务
│       ├── dao/                # 数据访问层
│       │   ├── DeviceDao.java          # 设备数据操作（JSON持久化）
│       │   └── UserDao.java            # 用户数据操作（JSON持久化）
│       ├── ui/                 # GUI界面
│       │   ├── MainFrame.java          # 主窗口（户型图、设备卡片、批量控制）
│       │   ├── LoginDialog.java        # 登录对话框
│       │   └── SceneDialog.java        # 场景选择对话框
│       ├── network/            # 网络通信
│       │   ├── DeviceServer.java       # 服务端（Socket）
│       │   ├── DeviceClient.java       # 客户端（Socket）
│       │   └── NetworkPanel.java       # 网络控制面板
│       ├── exception/          # 异常处理
│       │   └── DeviceException.java    # 设备异常
│       ├── factory/            # 工厂模式
│       │   └── DeviceFactory.java      # 设备工厂
│       ├── config/             # 配置管理
│       │   └── AppConfig.java          # 应用配置（单例模式）
│       ├── util/               # 工具类
│       │   ├── FileUtil.java           # 文件操作
│       │   ├── JsonUtil.java           # JSON解析（Gson）
│       │   └── UIUtils.java            # UI工具（样式、渐变、圆角等）
│       └── Main.java           # 程序入口
├── assets/                     # 资源文件
│   └── icons/                  # 图标资源
│       ├── device_light.png        # 灯泡图标
│       ├── device_ac.png           # 空调图标
│       ├── device_curtain.png      # 窗帘图标
│       ├── device_speaker.png      # 音箱图标
│       ├── btn_add.png             # 添加按钮图标
│       ├── btn_remove.png          # 删除按钮图标
│       ├── btn_on.png              # 开启按钮图标
│       ├── btn_off.png             # 关闭按钮图标
│       ├── btn_ok.png              # 确定按钮图标
│       ├── menu_*.png              # 菜单图标
│       └── scene_*.png             # 场景图标
├── images/                     # 界面截图
│   ├── 01-login.png               # 登录界面
│   ├── 02-main-all-devices.png    # 主界面-全部设备
│   ├── 03-device-control.png      # 设备控制对话框
│   ├── 04-add-device.png          # 添加设备
│   ├── 05-remove-device.png       # 删除设备
│   ├── 06-scene-dialog.png        # 场景选择对话框
│   ├── 07-network-panel-1.png     # 网络控制-启动服务
│   └── 08-network-panel-2.png     # 网络控制-停止服务
├── docs/                       # 文档
│   ├── feature-guide.md           # 功能说明
│   ├── usage-guide.md             # 使用指南
│   └── test_command.md            # 测试命令
├── lib/                        # 第三方依赖
│   └── gson-2.10.1.jar            # Gson JSON库
├── data/                       # 数据存储目录
│   ├── devices.json            # 设备数据
│   └── users.json              # 用户数据
├── config/                     # 配置文件目录
│   └── config.properties       # 系统配置
├── tmp/                        # 临时文件
├── README.md                   # 项目说明
└── AGENTS.md                   # 开发规范
```

## 功能特性

### 1. 设备管理
- 设备增删改查
- 设备状态切换（开/关）
- 设备参数调节（亮度、温度、音量、窗帘开合度等）

### 2. 房间管理
- 户型图可视化面板，点击房间筛选设备
- 设备按房间分组
- 房间设备统计

### 3. 场景模式
- 回家模式：自动开启常用设备
- 离家模式：关闭所有设备
- 睡眠模式：调整设备至睡眠状态

### 4. 状态监控
- 多线程实时监控设备状态
- 设备离线告警
- 状态自动刷新

### 5. 操作日志
- 记录所有设备操作
- 按时间查询日志
- 操作记录导出

### 6. 用户管理
- 用户登录验证
- 管理员/普通用户角色权限控制

### 7. 网络通信
- Socket 服务端/客户端
- 远程设备控制命令
- 网络控制面板

## 用户角色与权限

| 角色 | 默认账号 | 权限说明 |
|------|----------|----------|
| 管理员 | admin / admin123 | 全部权限：设备增删改查、用户管理、系统配置、场景设置、日志查看与导出 |
| 普通用户 | user / user123 | 基本权限：查看设备状态、控制设备开关、查看操作日志 |

### 权限详情

| 功能模块 | 管理员 | 普通用户 |
|----------|--------|----------|
| 设备查看 | ✅ | ✅ |
| 设备控制 | ✅ | ✅ |
| 设备添加 | ✅ | ❌ |
| 设备删除 | ✅ | ❌ |
| 场景设置 | ✅ | ❌ |
| 用户管理 | ✅ | ❌ |
| 日志查看 | ✅ | ✅ |
| 日志导出 | ✅ | ❌ |

## 运行方式

### 环境要求
- JDK 8.0 或更高版本
- Gson 2.10.1（已包含在 lib/ 目录）

### 编译
```bash
# Windows
javac -d out -sourcepath src -cp "lib/*" src\com\smarthome\Main.java

# Linux/Mac
javac -d out -sourcepath src -cp "lib/*" src/com/smarthome/Main.java
```

### 运行
```bash
# Windows
java -cp "out;lib/*" com.smarthome.Main

# Linux/Mac
java -cp "out:lib/*" com.smarthome.Main
```

## 技术栈

| 技术 | 用途 |
|------|------|
| Java SE | 核心开发语言 |
| Swing | GUI界面开发 |
| Socket | 网络通信 |
| 多线程 | 设备状态监控 |
| JSON | 数据存储格式 |
| Properties | 配置文件管理 |

## 设计模式

- **工厂模式**：DeviceFactory 创建设备对象
- **单例模式**：AppConfig 配置管理
- **观察者模式**：设备状态变化通知（Monitorable接口）

## 课程知识点覆盖

| 知识点 | 对应实现 |
|--------|----------|
| 类与对象 | Device、Room、User 等实体类 |
| 继承 | Light、AirConditioner 继承 Device |
| 多态 | 统一接口控制不同设备 |
| 抽象类 | Device 抽象基类 |
| 接口 | Controllable、Monitorable |
| 异常处理 | DeviceException 自定义异常 |
| 包结构 | entity/service/dao/ui 分层设计 |
| 多线程 | MonitorService 监控线程 |
| GUI编程 | Swing 界面组件 |
| 网络通信 | Socket 客户端/服务端 |
| 文件存储 | JSON 文件读写（Gson） |
| 配置文件 | config.properties + AppConfig 单例 |
| 设计模式 | 工厂模式、单例模式 |
