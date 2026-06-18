package com.smarthome.network;

import com.smarthome.entity.Device;
import com.smarthome.service.DeviceService;
import com.smarthome.util.UIUtils;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 网络控制面板
 * 提供服务端启动/停止、命令列表和客户端测试功能
 */
public class NetworkPanel extends JPanel {
    private DeviceServer deviceServer;
    private DeviceService deviceService;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JButton btnToggle;

    // 客户端测试组件
    private JTextField cmdField;
    private JTextArea clientResponseArea;
    private JList<String> cmdList;

    public NetworkPanel(DeviceServer deviceServer) {
        this.deviceServer = deviceServer;
        this.deviceService = null;
        deviceServer.setLogCallback(msg -> SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
        }));
        initUI();
    }

    public NetworkPanel(DeviceServer deviceServer, DeviceService deviceService) {
        this.deviceServer = deviceServer;
        this.deviceService = deviceService;
        deviceServer.setLogCallback(msg -> SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
        }));
        initUI();
    }

    public void setDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.BG_WHITE);

        // 左侧：命令列表
        JPanel cmdListPanel = createCommandListPanel();

        // 右侧：服务端控制 + 客户端测试
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(UIUtils.BG_WHITE);

        // 上方：服务端控制
        JPanel serverPanel = new JPanel(new BorderLayout(5, 5));
        serverPanel.setBorder(UIUtils.createStyledSectionBorder("服务端控制"));
        serverPanel.setBackground(UIUtils.BG_WHITE);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setBackground(UIUtils.BG_WHITE);
        controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        statusLabel = new JLabel("状态: 已停止");
        statusLabel.setFont(UIUtils.FONT_BODY_13);
        statusLabel.setForeground(UIUtils.STATUS_STOPPED);
        btnToggle = UIUtils.createGradientButton("启动服务", new Dimension(120, 32));
        btnToggle.addActionListener(this::doToggle);

        controlPanel.add(statusLabel, BorderLayout.CENTER);
        controlPanel.add(btnToggle, BorderLayout.EAST);
        serverPanel.add(controlPanel, BorderLayout.NORTH);

        logArea = UIUtils.createStyledTextArea(false, 6);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));
        serverPanel.add(logScroll, BorderLayout.CENTER);

        // 下方：客户端测试
        JPanel clientPanel = new JPanel(new BorderLayout(5, 5));
        clientPanel.setBorder(UIUtils.createStyledSectionBorder("客户端测试"));
        clientPanel.setBackground(UIUtils.BG_WHITE);

        JPanel cmdPanel = new JPanel(new BorderLayout(5, 5));
        cmdPanel.setBackground(UIUtils.BG_WHITE);
        JLabel cmdLabel = new JLabel("命令:");
        cmdLabel.setFont(UIUtils.FONT_BODY_12);
        cmdLabel.setForeground(UIUtils.TEXT_LABEL);
        cmdPanel.add(cmdLabel, BorderLayout.WEST);
        cmdField = new JTextField();
        cmdField.setFont(UIUtils.FONT_BODY_14);
        cmdPanel.add(cmdField, BorderLayout.CENTER);
        JButton btnSend = UIUtils.createGradientButton("发送");
        btnSend.addActionListener(this::doSend);
        cmdPanel.add(btnSend, BorderLayout.EAST);

        clientPanel.add(cmdPanel, BorderLayout.NORTH);

        clientResponseArea = UIUtils.createStyledTextArea(false, 4);
        JScrollPane respScroll = new JScrollPane(clientResponseArea);
        respScroll.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));
        clientPanel.add(respScroll, BorderLayout.CENTER);

        rightPanel.add(serverPanel, BorderLayout.CENTER);
        rightPanel.add(clientPanel, BorderLayout.SOUTH);

        add(cmdListPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createCommandListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setBorder(UIUtils.createStyledSectionBorder("快捷命令"));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        // 查询命令
        listModel.addElement("── 查询 ──");
        listModel.addElement("GET_ALL");

        // 开启命令
        listModel.addElement("── 开启设备 ──");
        listModel.addElement("TURN_ON:d1");
        listModel.addElement("TURN_ON:d2");
        listModel.addElement("TURN_ON:d3");
        listModel.addElement("TURN_ON:d4");
        listModel.addElement("TURN_ON:d5");
        listModel.addElement("TURN_ON:d6");

        // 关闭命令
        listModel.addElement("── 关闭设备 ──");
        listModel.addElement("TURN_OFF:d1");
        listModel.addElement("TURN_OFF:d2");
        listModel.addElement("TURN_OFF:d3");
        listModel.addElement("TURN_OFF:d4");
        listModel.addElement("TURN_OFF:d5");
        listModel.addElement("TURN_OFF:d6");

        cmdList = new JList<>(listModel);
        cmdList.setFont(UIUtils.FONT_BODY_12);
        cmdList.setBackground(UIUtils.BG_WHITE);
        cmdList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmdList.setSelectionBackground(UIUtils.CARD_ON_BG);
        cmdList.setSelectionForeground(UIUtils.PRIMARY_START);

        // 点击命令自动填入输入框
        cmdList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = cmdList.getSelectedValue();
                if (selected != null && !selected.startsWith("──")) {
                    cmdField.setText(selected);
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(cmdList);
        listScroll.setBorder(new LineBorder(UIUtils.INPUT_BORDER, 1));
        panel.add(listScroll, BorderLayout.CENTER);

        // 设备ID对照表
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(UIUtils.createStyledSectionBorder("设备ID对照"));
        legendPanel.setBackground(UIUtils.BG_WHITE);
        String[] legends = {"d1 - 客厅灯", "d2 - 卧室灯", "d3 - 客厅空调", "d4 - 卧室空调", "d5 - 客厅窗帘", "d6 - 智能音箱"};
        for (String legend : legends) {
            JLabel lbl = new JLabel(legend);
            lbl.setFont(UIUtils.FONT_SMALL_11);
            lbl.setForeground(UIUtils.TEXT_HINT);
            legendPanel.add(lbl);
        }
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void doToggle(ActionEvent e) {
        if (!deviceServer.isRunning()) {
            deviceServer.startServer();
            if (deviceServer.isRunning()) {
                statusLabel.setText("状态: 运行中 (端口 " + deviceServer.getPort() + ")");
                statusLabel.setForeground(UIUtils.STATUS_RUNNING);
                btnToggle.setText("停止服务");
            }
        } else {
            deviceServer.stopServer();
            statusLabel.setText("状态: 已停止");
            statusLabel.setForeground(UIUtils.STATUS_STOPPED);
            btnToggle.setText("启动服务");
        }
    }

    private void doSend(ActionEvent e) {
        String cmd = cmdField.getText().trim();
        if (cmd.isEmpty()) return;

        DeviceClient client = new DeviceClient();
        if (client.connect()) {
            String response = client.sendCommand(cmd);
            clientResponseArea.append("> " + cmd + "\n");
            clientResponseArea.append("< " + response + "\n");
            client.disconnect();
        } else {
            clientResponseArea.append("连接服务端失败，请先启动服务\n");
        }
        cmdField.setText("");
    }

    public void stopServer() {
        if (deviceServer.isRunning()) {
            deviceServer.stopServer();
        }
    }
}
