# 开发规范 (AGENTS)

## 编译与运行

### 编译命令
```bash
# Windows
javac -d out -sourcepath src -cp "lib/*" src\com\smarthome\Main.java

# Linux/Mac
javac -d out -sourcepath src -cp "lib/*" src/com/smarthome/Main.java
```

### 运行命令
```bash
# Windows
java -cp "out;lib/*" com.smarthome.Main

# Linux/Mac
java -cp "out:lib/*" com.smarthome.Main
```

### 清理编译产物
```bash
# Windows
rmdir /s /q out

# Linux/Mac
rm -rf out
```

## 包结构规范

```
com.smarthome
├── entity      # 实体类（数据模型）
├── interfaces  # 接口定义
├── service     # 业务逻辑层
├── dao         # 数据访问层
├── ui          # GUI界面层
├── network     # 网络通信层
├── exception   # 异常定义
├── factory     # 工厂类
├── config      # 配置管理
└── util        # 工具类
```

## 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 (PascalCase) | `DeviceService` |
| 方法名 | 小驼峰 (camelCase) | `getDeviceStatus()` |
| 变量名 | 小驼峰 (camelCase) | `deviceList` |
| 常量 | 全大写+下划线 | `MAX_DEVICE_COUNT` |
| 包名 | 全小写 | `com.smarthome.entity` |

## 代码规范

### 类结构顺序
1. 包声明 (package)
2. 导入语句 (import)
3. 类注释
4. 类定义
   - 成员变量
   - 构造方法
   - 成员方法
   - 内部类

### 方法规范
- 公共方法必须添加 JavaDoc 注释
- 方法长度不超过 50 行
- 单一职责原则

### 异常处理
- 使用自定义异常 `DeviceException`
- 捕获具体异常，避免 `catch(Exception e)`
- 异常信息要清晰明确

## 文件存储规范

### 数据文件
- 位置：`data/` 目录
- 格式：JSON
- 文件：`devices.json`、`users.json`、`logs.json`

### 配置文件
- 位置：`config/` 目录
- 格式：properties
- 文件：`config.properties`

## Git 提交规范

### 提交信息格式
```
<type>: <description>

# 示例
feat: 添加设备控制功能
fix: 修复设备状态显示错误
docs: 更新README文档
```

### 类型说明
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `refactor`: 重构代码
- `test`: 测试相关

## 开发流程

1. 创建功能分支：`git checkout -b feature/xxx`
2. 编写代码
3. 本地测试
4. 提交代码
5. 合并到主分支

## 注意事项

- 不要提交敏感信息（密码、密钥等）
- 每个类必须有 package 声明
- 使用 UTF-8 编码
- 缩进使用 4 个空格
