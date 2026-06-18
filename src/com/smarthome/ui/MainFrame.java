package com.smarthome.ui;

import com.smarthome.entity.*;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.service.*;
import com.smarthome.network.*;
import com.smarthome.util.FileUtil;
import com.smarthome.util.UIUtils;
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

    private JPanel floorPlanPanel;
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
        mainPanel.setBackground(UIUtils.BG_WHITE);

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
        menuBar.setBackground(new Color(245, 245, 245));
        menuBar.setBorder(new MatteBorder(0, 0, 2, 0, UIUtils.PRIMARY_START));

        // 文件菜单
        JMenu fileMenu = new JMenu("文件(F)");
        JMenuItem exitItem = new JMenuItem("退出", loadIcon("assets/icons/menu_exit.png"));
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // 设备菜单
        JMenu deviceMenu = new JMenu("设备(D)");
        addDeviceItem = new JMenuItem("添加设备", loadIcon("assets/icons/menu_add.png"));
        removeDeviceItem = new JMenuItem("删除设备", loadIcon("assets/icons/menu_remove.png"));
        addDeviceItem.addActionListener(e -> showAddDeviceDialog());
        removeDeviceItem.addActionListener(e -> showRemoveDeviceDialog());
        deviceMenu.add(addDeviceItem);
        deviceMenu.add(removeDeviceItem);
        menuBar.add(deviceMenu);

        // 场景菜单
        JMenu sceneMenu = new JMenu("场景(S)");
        sceneHomeItem = new JMenuItem("回家模式", loadIcon("assets/icons/scene_home.png"));
        sceneAwayItem = new JMenuItem("离家模式", loadIcon("assets/icons/scene_away.png"));
        sleepItem = new JMenuItem("睡眠模式", loadIcon("assets/icons/scene_sleep.png"));
        sceneHomeItem.addActionListener(e -> showSceneDialog());
        sceneAwayItem.addActionListener(e -> showSceneDialog());
        sleepItem.addActionListener(e -> showSceneDialog());
        sceneMenu.add(sceneHomeItem);
        sceneMenu.add(sceneAwayItem);
        sceneMenu.add(sleepItem);
        menuBar.add(sceneMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助(H)");
        JMenuItem aboutItem = new JMenuItem("关于", loadIcon("assets/icons/menu_about.png"));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "智能家居控制系统 v1.0\n基于 Java SE + Swing 开发",
                "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        // 网络菜单
        JMenu networkMenu = new JMenu("网络(N)");
        JMenuItem networkItem = new JMenuItem("网络控制", loadIcon("assets/icons/menu_network.png"));
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
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIUtils.BG_WHITE);

        wrapper.add(UIUtils.createGradientHeaderPanel("添加设备", 380, 70), BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setBackground(UIUtils.BG_WHITE);

        String[] types = {"light:智能灯", "ac:空调", "curtain:智能窗帘", "speaker:智能音箱"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(UIUtils.FONT_BODY_12);
        typeCombo.setBackground(UIUtils.BG_WHITE);
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String item = (String) value;
                String typeKey = item.split(":")[0];
                String iconPath = null;
                switch (typeKey) {
                    case "light": iconPath = "assets/icons/device_light.png"; break;
                    case "ac": iconPath = "assets/icons/device_ac.png"; break;
                    case "curtain": iconPath = "assets/icons/device_curtain.png"; break;
                    case "speaker": iconPath = "assets/icons/device_speaker.png"; break;
                }
                if (iconPath != null) {
                    try {
                        java.awt.Image img = new ImageIcon(iconPath).getImage()
                                .getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {}
                }
                label.setText(item.split(":")[1]);
                return label;
            }
        });
        JTextField nameField = new JTextField();
        nameField.setFont(UIUtils.FONT_BODY_14);
        JComboBox<String> roomCombo = new JComboBox<>();
        roomCombo.setFont(UIUtils.FONT_BODY_12);
        roomCombo.setBackground(UIUtils.BG_WHITE);
        for (Room room : roomService.findAll()) {
            roomCombo.addItem(room.getName());
        }

        JLabel l1 = UIUtils.createStyledLabel("设备类型:", UIUtils.FONT_BODY_12, UIUtils.TEXT_LABEL);
        JLabel l2 = UIUtils.createStyledLabel("设备名称:", UIUtils.FONT_BODY_12, UIUtils.TEXT_LABEL);
        JLabel l3 = UIUtils.createStyledLabel("所在房间:", UIUtils.FONT_BODY_12, UIUtils.TEXT_LABEL);

        panel.add(l1);
        panel.add(typeCombo);
        panel.add(l2);
        panel.add(nameField);
        panel.add(l3);
        panel.add(roomCombo);
        panel.add(new JLabel()); // 占位
        panel.add(new JLabel()); // 占位

        JButton btnConfirm = UIUtils.createGradientButton("确定");
        btnConfirm.setIcon(loadIcon("assets/icons/btn_add.png"));
        JButton btnCancel = UIUtils.createGradientButton("取消");
        btnCancel.addActionListener(e -> dialog.dispose());
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

        wrapper.add(panel, BorderLayout.CENTER);
        dialog.add(wrapper);
        dialog.setVisible(true);
    }

    private void showRemoveDeviceDialog() {
        List<Device> devices = deviceService.findAll();
        if (devices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可删除的设备", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "删除设备", true);
        dialog.setSize(350, 350);
        dialog.setLocationRelativeTo(this);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIUtils.BG_WHITE);
        wrapper.add(UIUtils.createGradientHeaderPanel("删除设备", 350, 60), BorderLayout.NORTH);

        DefaultListModel<Device> listModel = new DefaultListModel<>();
        for (Device d : devices) listModel.addElement(d);
        JList<Device> deviceList = new JList<>(listModel);
        deviceList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Device d = (Device) value;
                Icon icon = getDeviceIcon(d, 20);
                label.setIcon(icon);
                label.setText(d.getName());
                label.setFont(UIUtils.FONT_BODY_13);
                return label;
            }
        });
        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceList.setFont(UIUtils.FONT_BODY_13);
        if (!devices.isEmpty()) deviceList.setSelectedIndex(0);

        JScrollPane scroll = new JScrollPane(deviceList);
        scroll.setBorder(new EmptyBorder(10, 15, 5, 15));
        scroll.setBackground(UIUtils.BG_WHITE);
        wrapper.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setBackground(UIUtils.BG_WHITE);
        JButton btnConfirm = UIUtils.createGradientButton("删除");
        btnConfirm.setIcon(loadIcon("assets/icons/btn_remove.png"));
        JButton btnCancel = UIUtils.createGradientButton("取消");
        btnConfirm.addActionListener(e -> {
            Device device = deviceList.getSelectedValue();
            if (device != null) {
                Room room = roomService.findById(device.getRoomId());
                if (room != null) room.removeDevice(device);
                deviceService.removeDevice(device.getId());
                logService.log(device.getName(), "删除设备");
                updateLogArea();
                refreshDevicePanel();
            }
            dialog.dispose();
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(wrapper);
        dialog.setVisible(true);
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

    private int selectedRoomIndex = 0;
    // 布局（坐标基于 240x240 画布，外墙 8~232, 48~236）:
    //   客厅(左大) | 卧室(右上大)
    //   客厅       | 卫生间(右中小，不可选中)
    //   厨房(左下)  | 书房(右下)
    private static final int[][] ROOM_BOUNDS = {
        {10, 50, 130, 140},  // 客厅 - 左侧大
        {145, 50, 85, 95},   // 卧室 - 右上大
        {145, 150, 85, 30},  // 卫生间 - 右中小(不可选中)
        {10, 195, 80, 40},   // 厨房 - 左下
        {95, 195, 140, 40}   // 书房 - 右下（完整，无垂直内墙分隔）
    };

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(UIUtils.createStyledSectionBorder("户型图"));
        panel.setBackground(UIUtils.BG_WHITE);

        floorPlanPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawFloorPlan(g);
            }
        };
        floorPlanPanel.setPreferredSize(new Dimension(240, 240));
        floorPlanPanel.setBackground(UIUtils.BG_FLOOR);
        floorPlanPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX(), y = e.getY();
                // 检查"全部"区域
                if (y < 45) {
                    selectedRoomIndex = 0;
                } else {
                    // 检查房间区域
                    boolean found = false;
                    int roomIdx = 0;
                    for (int i = 0; i < ROOM_BOUNDS.length; i++) {
                        int[] b = ROOM_BOUNDS[i];
                        if (x >= b[0] && x < b[0] + b[2] && y >= b[1] && y < b[1] + b[3]) {
                            if (i == 2) {
                                // 卫生间不可选中
                                found = true;
                                break;
                            }
                            selectedRoomIndex = roomIdx + 1;
                            found = true;
                            break;
                        }
                        if (i != 2) roomIdx++;
                    }
                    if (!found) selectedRoomIndex = 0;
                }
                refreshDevicePanel();
                floorPlanPanel.repaint();
            }
        });
        floorPlanPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(floorPlanPanel, BorderLayout.NORTH);

        // 设备计数统计
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(UIUtils.createStyledSectionBorder("房间设备"));
        statsPanel.setBackground(UIUtils.BG_WHITE);
        List<Room> rooms = roomService.findAll();
        for (Room room : rooms) {
            JLabel lbl = new JLabel(room.getName() + ": " + room.getDeviceCount() + "个设备");
            lbl.setFont(UIUtils.FONT_SMALL_11);
            lbl.setForeground(UIUtils.TEXT_HINT);
            statsPanel.add(lbl);
        }
        JScrollPane statsScroll = new JScrollPane(statsPanel);
        statsScroll.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));
        panel.add(statsScroll, BorderLayout.CENTER);
        return panel;
    }

    private void drawFloorPlan(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        UIUtils.applyRenderingHints(g2);
        List<Room> rooms = roomService.findAll();

        // 顶部 "全部" 区域
        boolean allSelected = selectedRoomIndex == 0;
        g2.setColor(allSelected ? UIUtils.ALL_SEL : UIUtils.ALL_UNSEL);
        g2.fillRoundRect(10, 5, 220, 35, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(UIUtils.FONT_TITLE_13);
        FontMetrics fm = g2.getFontMetrics();
        String allText = "全部 (" + deviceService.getDeviceCount() + ")";
        g2.drawString(allText, 10 + (220 - fm.stringWidth(allText)) / 2, 28);

        // 画可选房间（跳过索引2卫生间）
        int roomIdx = 0;
        for (int i = 0; i < ROOM_BOUNDS.length; i++) {
            int[] b = ROOM_BOUNDS[i];

            if (i == 2) {
                // 卫生间 - 特殊样式，不可选中
                g2.setColor(UIUtils.BATHROOM_BG);
                g2.fillRect(b[0] + 1, b[1] + 1, b[2] - 2, b[3] - 2);
                g2.setColor(UIUtils.BATHROOM_TEXT);
                g2.setFont(UIUtils.FONT_SMALL_10);
                fm = g2.getFontMetrics();
                String name = "卫生间";
                g2.drawString(name, b[0] + (b[2] - fm.stringWidth(name)) / 2, b[1] + b[3] / 2 + 4);
                continue;
            }

            Room room = rooms.get(roomIdx);

            // 房间背景
            g2.setColor(UIUtils.ROOM_BG);
            g2.fillRect(b[0] + 1, b[1] + 1, b[2] - 2, b[3] - 2);

            // 选中高亮
            if (selectedRoomIndex == roomIdx + 1) {
                g2.setColor(UIUtils.SEL_HIGHLIGHT);
                g2.fillRect(b[0] + 1, b[1] + 1, b[2] - 2, b[3] - 2);
            }

            // 房间名称
            g2.setColor(UIUtils.TEXT_DARK);
            g2.setFont(UIUtils.FONT_TITLE_13);
            fm = g2.getFontMetrics();
            String name = room.getName();
            g2.drawString(name, b[0] + (b[2] - fm.stringWidth(name)) / 2, b[1] + b[3] / 2 - 5);

            // 设备数量
            g2.setColor(UIUtils.TEXT_HINT);
            g2.setFont(UIUtils.FONT_SMALL_10);
            fm = g2.getFontMetrics();
            String count = room.getDeviceCount() + "个设备";
            g2.drawString(count, b[0] + (b[2] - fm.stringWidth(count)) / 2, b[1] + b[3] / 2 + 15);

            roomIdx++;
        }

        // 画墙壁线 - 先画实心外墙（粗）
        g2.setColor(UIUtils.WALL_OUTER);
        g2.setStroke(new BasicStroke(5));
        // 四面外墙
        g2.drawLine(8, 48, 232, 48);      // 顶
        g2.drawLine(8, 236, 232, 236);    // 底
        g2.drawLine(8, 48, 8, 236);       // 左
        g2.drawLine(232, 48, 232, 236);   // 右

        // 内墙（稍细）
        g2.setColor(UIUtils.WALL_INNER);
        g2.setStroke(new BasicStroke(3));
        // 垂直内墙 - 客厅|右侧（止于y=192，不穿过书房）
        g2.drawLine(143, 48, 143, 192);
        // 水平内墙 - 卧室|卫生间
        g2.drawLine(143, 148, 232, 148);
        // 水平内墙 - 上下排
        g2.drawLine(8, 192, 232, 192);
        // 垂直内墙 - 厨房|书房
        g2.drawLine(93, 192, 93, 236);

        // 门标记（用背景色覆盖墙壁段，模拟门洞）
        g2.setColor(UIUtils.BG_FLOOR);
        g2.setStroke(new BasicStroke(5));
        // 大门 - 客厅左墙
        g2.drawLine(8, 90, 8, 115);
        // 客厅到卧室的门 - 内墙上方
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(143, 65, 143, 85);
        // 客厅到厨房的门
        g2.drawLine(20, 192, 40, 192);
        // 卫生间门 - 左侧从客厅进
        g2.drawLine(143, 155, 143, 172);
        // 书房门 - 从客厅进（水平内墙上，厨房|书房分隔线右侧）
        g2.drawLine(100, 192, 125, 192);
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(UIUtils.createStyledSectionBorder("设备列表"));
        panel.setBackground(UIUtils.BG_WHITE);

        devicePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        devicePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        devicePanel.setBackground(UIUtils.BG_WHITE);

        refreshDevicePanel();

        JScrollPane scrollPane = new JScrollPane(devicePanel);
        scrollPane.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
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

        // 蓝/白主题
        Color bgColor = isOn ? UIUtils.CARD_ON_BG : UIUtils.CARD_OFF_BG;
        Color borderColor = isOn ? UIUtils.CARD_ON_BORDER : UIUtils.CARD_OFF_BORDER;
        int borderWidth = isOn ? 2 : 1;

        card.setBorder(BorderFactory.createLineBorder(borderColor, borderWidth));
        card.setBackground(bgColor);

        Icon devIcon = getDeviceIcon(device, 36);
        JLabel iconLabel;
        if (devIcon != null) {
            iconLabel = new JLabel(devIcon, SwingConstants.CENTER);
        } else {
            iconLabel = new JLabel("[?]", SwingConstants.CENTER);
            iconLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
            iconLabel.setForeground(isOn ? UIUtils.CARD_ON_ICON : UIUtils.CARD_OFF_ICON);
        }
        card.add(iconLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(device.getName(), SwingConstants.CENTER);
        nameLabel.setFont(UIUtils.FONT_BODY_12);
        nameLabel.setForeground(UIUtils.TEXT_DARK);
        card.add(nameLabel, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showControlDialog(device);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!isOn) {
                    card.setBackground(UIUtils.CARD_HOVER_BG);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(bgColor);
            }
        });

        return card;
    }

    private Icon getDeviceIcon(Device device, int size) {
        String path = null;
        if (device instanceof Light) path = "assets/icons/device_light.png";
        else if (device instanceof AirConditioner) path = "assets/icons/device_ac.png";
        else if (device instanceof Curtain) path = "assets/icons/device_curtain.png";
        else if (device instanceof Speaker) path = "assets/icons/device_speaker.png";
        if (path == null) return null;
        try {
            java.awt.Image img = new ImageIcon(path).getImage()
                    .getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 0));
        panel.setBorder(UIUtils.createStyledSectionBorder("设备控制"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIUtils.BG_WHITE);

        panel.add(Box.createVerticalStrut(15));

        JLabel titleLabel = UIUtils.createStyledLabel("批量控制", UIUtils.FONT_TITLE_14, UIUtils.PRIMARY_START);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));

        JLabel hintLabel = UIUtils.createStyledLabel("一键控制所有设备", UIUtils.FONT_SMALL_10, UIUtils.TEXT_HINT);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hintLabel);
        panel.add(Box.createVerticalStrut(15));

        JButton btnOn = UIUtils.createGradientButton("全部开启", new Dimension(150, 36));
        btnOn.setIcon(loadIcon("assets/icons/btn_on.png"));
        btnOn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOn.addActionListener(e -> {
            for (Device d : deviceService.findAll()) {
                d.turnOn();
            }
            logService.log("所有设备", "全部开启");
            updateLogArea();
            refreshDevicePanel();
        });
        panel.add(btnOn);
        panel.add(Box.createVerticalStrut(10));

        JButton btnOff = UIUtils.createGradientButton("全部关闭", new Dimension(150, 36));
        btnOff.setIcon(loadIcon("assets/icons/btn_off.png"));
        btnOff.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOff.addActionListener(e -> {
            for (Device d : deviceService.findAll()) {
                d.turnOff();
            }
            logService.log("所有设备", "全部关闭");
            updateLogArea();
            refreshDevicePanel();
        });
        panel.add(btnOff);

        panel.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(160, 1));
        sep.setForeground(UIUtils.INPUT_BORDER);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(15));

        JButton btnScene = UIUtils.createGradientButton("场景模式", new Dimension(150, 36));
        btnScene.setIcon(loadIcon("assets/icons/scene_home.png"));
        btnScene.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnScene.addActionListener(e -> showSceneDialog());
        panel.add(btnScene);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 150));
        panel.setBorder(UIUtils.createStyledSectionBorder("操作日志"));
        panel.setBackground(UIUtils.BG_WHITE);

        logArea = UIUtils.createStyledTextArea(false, 0);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));

        btnExportLog = UIUtils.createGradientButton("导出日志", new Dimension(100, 30));
        btnExportLog.addActionListener(e -> exportLogs());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UIUtils.BG_WHITE);
        btnPanel.setBorder(new EmptyBorder(5, 0, 5, 5));
        btnPanel.add(btnExportLog);

        panel.add(logScroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showControlDialog(Device device) {
        JDialog dialog = new JDialog(this, "设备控制 - " + device.getName(), true);
        dialog.setSize(320, 420);
        dialog.setLocationRelativeTo(this);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIUtils.BG_WHITE);

        // 渐变头部
        wrapper.add(UIUtils.createGradientHeaderPanel("设备控制", 320, 70), BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(UIUtils.BG_WHITE);

        JLabel typeLabel = UIUtils.createStyledLabel("设备类型: " + device.getType(),
                UIUtils.FONT_BODY_12, UIUtils.TEXT_LABEL);
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(5));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(UIUtils.BG_WHITE);
        JLabel statusLabel = new JLabel("状态: " + device.getStatusText());
        statusLabel.setFont(UIUtils.FONT_BODY_13);
        statusLabel.setForeground(UIUtils.TEXT_DARK);
        statusPanel.add(statusLabel);
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(10));

        // 根据设备类型动态生成参数控件
        if (device instanceof Light) {
            addLightControls(panel, (Light) device);
        } else if (device instanceof AirConditioner) {
            addAirConditionerControls(panel, (AirConditioner) device);
        } else if (device instanceof Curtain) {
            addCurtainControls(panel, (Curtain) device);
        } else if (device instanceof Speaker) {
            addSpeakerControls(panel, (Speaker) device);
        }

        panel.add(Box.createVerticalStrut(10));

        // 按钮区
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnPanel.setBackground(UIUtils.BG_WHITE);
        JButton btnOn = UIUtils.createGradientButton("开启");
        btnOn.setIcon(loadIcon("assets/icons/btn_on.png"));
        JButton btnOff = UIUtils.createGradientButton("关闭");
        btnOff.setIcon(loadIcon("assets/icons/btn_off.png"));
        JButton btnConfirm = UIUtils.createGradientButton("确定");
        btnConfirm.setIcon(loadIcon("assets/icons/btn_ok.png"));

        btnOn.addActionListener(e -> {
            device.turnOn();
            statusLabel.setText("状态: " + device.getStatusText());
            logService.log(device.getName(), "开启");
            updateLogArea();
            refreshDevicePanel();
        });

        btnOff.addActionListener(e -> {
            device.turnOff();
            statusLabel.setText("状态: " + device.getStatusText());
            logService.log(device.getName(), "关闭");
            updateLogArea();
            refreshDevicePanel();
        });

        btnConfirm.addActionListener(e -> {
            logService.log(device.getName(), "参数调节");
            updateLogArea();
            refreshDevicePanel();
            dialog.dispose();
        });

        btnPanel.add(btnOn);
        btnPanel.add(btnOff);
        btnPanel.add(btnConfirm);
        panel.add(btnPanel);

        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(wrapper);
        dialog.setVisible(true);
    }

    private void addLightControls(JPanel panel, Light light) {
        // 亮度滑块
        JPanel brightnessPanel = new JPanel(new BorderLayout(5, 0));
        brightnessPanel.add(new JLabel("亮度:"), BorderLayout.WEST);
        JSlider brightnessSlider = new JSlider(0, 100, light.getBrightness());
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setPaintTicks(true);
        JLabel brightnessValue = new JLabel(light.getBrightness() + "%");
        brightnessSlider.addChangeListener(e -> {
            brightnessValue.setText(brightnessSlider.getValue() + "%");
            light.setBrightness(brightnessSlider.getValue());
        });
        brightnessPanel.add(brightnessSlider, BorderLayout.CENTER);
        brightnessPanel.add(brightnessValue, BorderLayout.EAST);
        panel.add(brightnessPanel);
        panel.add(Box.createVerticalStrut(10));

        // 颜色下拉框
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel("颜色:"));
        String[] colors = {"白色", "暖白", "红色", "橙色", "黄色", "绿色", "蓝色", "紫色"};
        JComboBox<String> colorCombo = new JComboBox<>(colors);
        colorCombo.setSelectedItem(light.getColor());
        colorCombo.addActionListener(e -> light.setColor((String) colorCombo.getSelectedItem()));
        colorPanel.add(colorCombo);
        panel.add(colorPanel);
    }

    private void addAirConditionerControls(JPanel panel, AirConditioner ac) {
        // 温度滑块
        JPanel tempPanel = new JPanel(new BorderLayout(5, 0));
        tempPanel.add(new JLabel("温度:"), BorderLayout.WEST);
        JSlider tempSlider = new JSlider(16, 30, ac.getTemperature());
        tempSlider.setMajorTickSpacing(2);
        tempSlider.setPaintTicks(true);
        JLabel tempValue = new JLabel(ac.getTemperature() + "°C");
        tempSlider.addChangeListener(e -> {
            tempValue.setText(tempSlider.getValue() + "°C");
            ac.setTemperature(tempSlider.getValue());
        });
        tempPanel.add(tempSlider, BorderLayout.CENTER);
        tempPanel.add(tempValue, BorderLayout.EAST);
        panel.add(tempPanel);
        panel.add(Box.createVerticalStrut(10));

        // 模式下拉框
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(new JLabel("模式:"));
        String[] modes = {"制冷", "制热", "除湿", "自动"};
        JComboBox<String> modeCombo = new JComboBox<>(modes);
        modeCombo.setSelectedItem(ac.getMode());
        modeCombo.addActionListener(e -> ac.setMode((String) modeCombo.getSelectedItem()));
        modePanel.add(modeCombo);
        panel.add(modePanel);
    }

    private void addCurtainControls(JPanel panel, Curtain curtain) {
        // 开合度滑块
        JPanel posPanel = new JPanel(new BorderLayout(5, 0));
        posPanel.add(new JLabel("开合度:"), BorderLayout.WEST);
        JSlider posSlider = new JSlider(0, 100, curtain.getPosition());
        posSlider.setMajorTickSpacing(25);
        posSlider.setPaintTicks(true);
        JLabel posValue = new JLabel(curtain.getPosition() + "%");
        posSlider.addChangeListener(e -> {
            posValue.setText(posSlider.getValue() + "%");
            curtain.setPosition(posSlider.getValue());
        });
        posPanel.add(posSlider, BorderLayout.CENTER);
        posPanel.add(posValue, BorderLayout.EAST);
        panel.add(posPanel);
    }

    private void addSpeakerControls(JPanel panel, Speaker speaker) {
        // 音量滑块
        JPanel volPanel = new JPanel(new BorderLayout(5, 0));
        volPanel.add(new JLabel("音量:"), BorderLayout.WEST);
        JSlider volSlider = new JSlider(0, 100, speaker.getVolume());
        volSlider.setMajorTickSpacing(25);
        volSlider.setPaintTicks(true);
        JLabel volValue = new JLabel(String.valueOf(speaker.getVolume()));
        volSlider.addChangeListener(e -> {
            volValue.setText(String.valueOf(volSlider.getValue()));
            speaker.setVolume(volSlider.getValue());
        });
        volPanel.add(volSlider, BorderLayout.CENTER);
        volPanel.add(volValue, BorderLayout.EAST);
        panel.add(volPanel);
        panel.add(Box.createVerticalStrut(10));

        // 播放状态
        JPanel playPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playPanel.add(new JLabel("播放:"));
        JRadioButton btnPlaying = new JRadioButton("是", speaker.isPlaying());
        JRadioButton btnNotPlaying = new JRadioButton("否", !speaker.isPlaying());
        ButtonGroup playGroup = new ButtonGroup();
        playGroup.add(btnPlaying);
        playGroup.add(btnNotPlaying);
        btnPlaying.addActionListener(e -> speaker.setPlaying(true));
        btnNotPlaying.addActionListener(e -> speaker.setPlaying(false));
        playPanel.add(btnPlaying);
        playPanel.add(btnNotPlaying);
        panel.add(playPanel);
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

    private void showSceneDialog() {
        SceneDialog sceneDialog = new SceneDialog(this);
        sceneDialog.setVisible(true);
        String scene = sceneDialog.getSelectedScene();
        if (scene == null) return;

        switch (scene) {
            case "home": applySceneHome(); break;
            case "away": applySceneAway(); break;
            case "sleep": applySceneSleep(); break;
        }
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
