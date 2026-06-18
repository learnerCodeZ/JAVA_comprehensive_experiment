package com.smarthome.ui;

import com.smarthome.entity.*;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.service.*;
import com.smarthome.network.*;
import com.smarthome.util.FileUtil;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * 主窗口类
 */
public class MainFrame extends JFrame {
    private DeviceService deviceService;
    private RoomService roomService;
    private LogService logService;
    private UserService userService;
    private MonitorService monitorService;
    private DeviceServer deviceServer;
    private NetworkPanel networkPanel;

    private JList<String> roomList;
    private JPanel devicePanel;
    private JTextArea logArea;
    private JLabel statusLabel;
    private String selectedRoomId = null;

    // 权限控制相关的菜单项
    private JMenuItem addDeviceItem;
    private JMenuItem removeDeviceItem;
    private JMenuItem sceneHomeItem;
    private JMenuItem sceneAwayItem;
    private JMenuItem sleepItem;
    private JButton btnExportLog;

    public MainFrame() {
        this(new UserService());
    }

    public MainFrame(UserService userService) {
        this.userService = userService;
        initServices();
        initSampleData();
        initUI();
        applyPermissions();
        startMonitor();
    }

    private void initServices() {
        deviceService = new DeviceService();
        roomService = new RoomService();
        logService = new LogService();
        monitorService = new MonitorService(deviceService, logService);
        deviceServer = new DeviceServer(deviceService);
    }

    private void initSampleData() {
        Room livingRoom = new Room("r1", "客厅");
        Room bedroom = new Room("r2", "卧室");
        Room kitchen = new Room("r3", "厨房");
        Room study = new Room("r4", "书房");

        roomService.addRoom(livingRoom);
        roomService.addRoom(bedroom);
        roomService.addRoom(kitchen);
        roomService.addRoom(study);

        // 非首次运行：从持久化数据重建房间-设备关联
        if (deviceService.getDeviceCount() > 0) {
            for (Device device : deviceService.findAll()) {
                Room room = roomService.findById(device.getRoomId());
                if (room != null) {
                    room.addDevice(device);
                }
            }
            return;
        }

        // 首次运行：创建示例设备
        Device light1 = new Light("d1", "客厅灯");
        Device light2 = new Light("d2", "卧室灯");
        Device ac1 = new AirConditioner("d3", "客厅空调");
        Device ac2 = new AirConditioner("d4", "卧室空调");
        Device curtain1 = new Curtain("d5", "客厅窗帘");
        Device speaker1 = new Speaker("d6", "智能音箱");

        livingRoom.addDevice(light1);
        bedroom.addDevice(light2);
        livingRoom.addDevice(ac1);
        bedroom.addDevice(ac2);
        livingRoom.addDevice(curtain1);
        livingRoom.addDevice(speaker1);

        deviceService.addDevice(light1);
        deviceService.addDevice(light2);
        deviceService.addDevice(ac1);
        deviceService.addDevice(ac2);
        deviceService.addDevice(curtain1);
        deviceService.addDevice(speaker1);
    }

    private void initUI() {
        setTitle("智能家居控制系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = createLeftPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel rightPanel = createRightPanel();
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件(F)");
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // 设备菜单
        JMenu deviceMenu = new JMenu("设备(D)");
        addDeviceItem = new JMenuItem("添加设备");
        removeDeviceItem = new JMenuItem("删除设备");
        addDeviceItem.addActionListener(e -> showAddDeviceDialog());
        removeDeviceItem.addActionListener(e -> showRemoveDeviceDialog());
        deviceMenu.add(addDeviceItem);
        deviceMenu.add(removeDeviceItem);
        menuBar.add(deviceMenu);

        // 场景菜单
        JMenu sceneMenu = new JMenu("场景(S)");
        sceneHomeItem = new JMenuItem("回家模式", loadIcon("assets/design/scene_home.png"));
        sceneAwayItem = new JMenuItem("离家模式", loadIcon("assets/design/scene_away.png"));
        sleepItem = new JMenuItem("睡眠模式", loadIcon("assets/design/scene_sleep.png"));
        sceneHomeItem.addActionListener(e -> applySceneHome());
        sceneAwayItem.addActionListener(e -> applySceneAway());
        sleepItem.addActionListener(e -> applySceneSleep());
        sceneMenu.add(sceneHomeItem);
        sceneMenu.add(sceneAwayItem);
        sceneMenu.add(sleepItem);
        menuBar.add(sceneMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助(H)");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "智能家居控制系统 v1.0\n基于 Java SE + Swing 开发",
                "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        // 网络菜单
        JMenu networkMenu = new JMenu("网络(N)");
        JMenuItem networkItem = new JMenuItem("网络控制");
        networkItem.addActionListener(e -> showNetworkDialog());
        networkMenu.add(networkItem);
        menuBar.add(networkMenu);

        return menuBar;
    }

    // ==================== 权限控制 ====================

    private boolean isAdmin() {
        User user = userService.getCurrentUser();
        return user != null && "管理员".equals(user.getRole());
    }

    private void applyPermissions() {
        boolean admin = isAdmin();
        addDeviceItem.setEnabled(admin);
        removeDeviceItem.setEnabled(admin);
        sceneHomeItem.setEnabled(admin);
        sceneAwayItem.setEnabled(admin);
        sleepItem.setEnabled(admin);
        btnExportLog.setEnabled(admin);
    }

    // ==================== 场景模式 ====================

    private void applySceneHome() {
        for (Device device : deviceService.findAll()) {
            if (device instanceof Light) {
                device.turnOn();
                ((Light) device).setBrightness(100);
            } else if (device instanceof AirConditioner) {
                device.turnOn();
                ((AirConditioner) device).setTemperature(26);
                ((AirConditioner) device).setMode("制冷");
            } else if (device instanceof Curtain) {
                device.turnOn();
                ((Curtain) device).setPosition(100);
            }
        }
        logService.log("场景", "回家模式");
        updateLogArea();
        refreshDevicePanel();
    }

    private void applySceneAway() {
        for (Device device : deviceService.findAll()) {
            device.turnOff();
        }
        logService.log("场景", "离家模式");
        updateLogArea();
        refreshDevicePanel();
    }

    private void applySceneSleep() {
        for (Device device : deviceService.findAll()) {
            if (device instanceof Light) {
                device.turnOff();
            } else if (device instanceof AirConditioner) {
                device.turnOn();
                ((AirConditioner) device).setTemperature(25);
                ((AirConditioner) device).setMode("制冷");
            } else if (device instanceof Curtain) {
                device.turnOn();
                ((Curtain) device).setPosition(0);
            } else if (device instanceof Speaker) {
                device.turnOn();
                ((Speaker) device).setVolume(10);
            }
        }
        logService.log("场景", "睡眠模式");
        updateLogArea();
        refreshDevicePanel();
    }

    // ==================== 设备添加/删除 ====================

    private void showAddDeviceDialog() {
        JDialog dialog = new JDialog(this, "添加设备", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] types = {"light:智能灯", "ac:空调", "curtain:智能窗帘", "speaker:智能音箱"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        JTextField nameField = new JTextField();
        JComboBox<String> roomCombo = new JComboBox<>();
        for (Room room : roomService.findAll()) {
            roomCombo.addItem(room.getName());
        }

        panel.add(new JLabel("设备类型:"));
        panel.add(typeCombo);
        panel.add(new JLabel("设备名称:"));
        panel.add(nameField);
        panel.add(new JLabel("所在房间:"));
        panel.add(roomCombo);
        panel.add(new JLabel()); // 占位
        panel.add(new JLabel()); // 占位

        JButton btnConfirm = new JButton("确定");
        JButton btnCancel = new JButton("取消");
        panel.add(btnConfirm);
        panel.add(btnCancel);

        btnConfirm.addActionListener(e -> {
            String typeStr = (String) typeCombo.getSelectedItem();
            String type = typeStr.split(":")[0];
            String name = nameField.getText().trim();
            int roomIndex = roomCombo.getSelectedIndex();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入设备名称", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = "d" + System.currentTimeMillis() % 10000;
            Device device = DeviceFactory.createDevice(type, id, name);
            Room room = roomService.findAll().get(roomIndex);
            room.addDevice(device);
            deviceService.addDevice(device);
            logService.log(name, "添加设备");
            updateLogArea();
            refreshDevicePanel();
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showRemoveDeviceDialog() {
        List<Device> devices = deviceService.findAll();
        if (devices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可删除的设备", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] deviceNames = devices.stream()
                .map(d -> d.getName() + " [" + d.getType() + "]")
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "选择要删除的设备:", "删除设备",
                JOptionPane.PLAIN_MESSAGE, null, deviceNames, deviceNames[0]);

        if (selected != null) {
            int index = -1;
            for (int i = 0; i < deviceNames.length; i++) {
                if (deviceNames[i].equals(selected)) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                Device device = devices.get(index);
                Room room = roomService.findById(device.getRoomId());
                if (room != null) {
                    room.removeDevice(device);
                }
                deviceService.removeDevice(device.getId());
                logService.log(device.getName(), "删除设备");
                updateLogArea();
                refreshDevicePanel();
            }
        }
    }

    // ==================== 日志导出 ====================

    private void exportLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出日志");
        fileChooser.setSelectedFile(new java.io.File("操作日志.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            StringBuilder content = new StringBuilder();
            content.append("智能家居系统 - 操作日志\n");
            content.append("========================\n\n");
            for (OperationLog log : logService.findAll()) {
                content.append(log.toString()).append("\n");
            }
            FileUtil.writeFile(path, content.toString());
            JOptionPane.showMessageDialog(this, "日志已导出到: " + path, "导出成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ==================== 原有界面方法 ====================

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setBorder(new TitledBorder("房间列表"));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("全部");
        for (Room room : roomService.findAll()) {
            listModel.addElement(room.getName());
        }

        roomList = new JList<>(listModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRoomIndex = roomList.getSelectedIndex();
                refreshDevicePanel();
            }
        });

        panel.add(new JScrollPane(roomList), BorderLayout.CENTER);
        return panel;
    }

    private int selectedRoomIndex = 0;

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("设备列表"));

        devicePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        devicePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        refreshDevicePanel();

        panel.add(new JScrollPane(devicePanel), BorderLayout.CENTER);
        return panel;
    }

    private void refreshDevicePanel() {
        devicePanel.removeAll();

        List<Device> devices;
        if (selectedRoomIndex == 0) {
            devices = deviceService.findAll();
        } else {
            Room room = roomService.findAll().get(selectedRoomIndex - 1);
            devices = room.getDevices();
        }

        for (Device device : devices) {
            JPanel card = createDeviceCard(device);
            devicePanel.add(card);
        }

        devicePanel.revalidate();
        devicePanel.repaint();
    }

    private JPanel createDeviceCard(Device device) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(120, 100));

        boolean isOn = device.isStatus();

        Color borderColor = isOn ? new Color(255, 215, 0) : Color.GRAY;
        card.setBorder(BorderFactory.createLineBorder(borderColor, isOn ? 3 : 1));
        card.setBackground(isOn ? new Color(51, 51, 51) : Color.WHITE);

        String icon = getDeviceIcon(device);
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);

        if (isOn) {
            iconLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
            iconLabel.setForeground(new Color(255, 215, 0));
        } else {
            iconLabel.setFont(new Font("微软雅黑", Font.PLAIN, 30));
            iconLabel.setForeground(new Color(153, 153, 153));
        }

        card.add(iconLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(device.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        nameLabel.setForeground(isOn ? Color.WHITE : Color.BLACK);
        card.add(nameLabel, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showControlDialog(device);
            }
        });

        return card;
    }

    private String getDeviceIcon(Device device) {
        if (device instanceof Light) return "[L]";
        if (device instanceof AirConditioner) return "[A]";
        if (device instanceof Curtain) return "[C]";
        if (device instanceof Speaker) return "[S]";
        return "[?]";
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new TitledBorder("设备控制"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("选择设备进行控制"));
        panel.add(Box.createVerticalStrut(10));

        JButton btnOn = new JButton("全部开启");
        JButton btnOff = new JButton("全部关闭");

        btnOn.addActionListener(e -> {
            for (Device d : deviceService.findAll()) {
                d.turnOn();
            }
            logService.log("所有设备", "全部开启");
            updateLogArea();
            refreshDevicePanel();
        });

        btnOff.addActionListener(e -> {
            for (Device d : deviceService.findAll()) {
                d.turnOff();
            }
            logService.log("所有设备", "全部关闭");
            updateLogArea();
            refreshDevicePanel();
        });

        panel.add(btnOn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnOff);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setBorder(new TitledBorder("操作日志"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        btnExportLog = new JButton("导出日志");
        btnExportLog.addActionListener(e -> exportLogs());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnExportLog);

        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showControlDialog(Device device) {
        JDialog dialog = new JDialog(this, "设备控制 - " + device.getName(), true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("设备类型: " + device.getType()));
        panel.add(Box.createVerticalStrut(10));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("状态: " + device.getStatusText()));
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnOn = new JButton("开启");
        JButton btnOff = new JButton("关闭");
        JButton btnClose = new JButton("关闭窗口");

        btnOn.addActionListener(e -> {
            device.turnOn();
            logService.log(device.getName(), "开启");
            updateLogArea();
            refreshDevicePanel();
            dialog.dispose();
        });

        btnOff.addActionListener(e -> {
            device.turnOff();
            logService.log(device.getName(), "关闭");
            updateLogArea();
            refreshDevicePanel();
            dialog.dispose();
        });

        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnOn);
        btnPanel.add(btnOff);
        btnPanel.add(btnClose);
        panel.add(btnPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateLogArea() {
        logArea.setText("");
        List<OperationLog> logs = logService.findRecent(20);
        for (OperationLog log : logs) {
            logArea.append(log.toString() + "\n");
        }
    }

    private void startMonitor() {
        monitorService.startMonitor();
    }

    private void showNetworkDialog() {
        JDialog dialog = new JDialog(this, "网络控制", false);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        if (networkPanel == null) {
            networkPanel = new NetworkPanel(deviceServer);
        }
        dialog.setContentPane(networkPanel);
        dialog.setVisible(true);
    }

    public void dispose() {
        if (networkPanel != null) {
            networkPanel.stopServer();
        }
        monitorService.stopMonitor();
        super.dispose();
    }

    private Icon loadIcon(String path) {
        try {
            java.awt.Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
