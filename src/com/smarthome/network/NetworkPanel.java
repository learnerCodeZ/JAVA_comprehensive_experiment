package com.smarthome.network;

import com.smarthome.entity.Device;
import com.smarthome.service.DeviceService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
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

        // 左侧：命令列表
        JPanel cmdListPanel = createCommandListPanel();

        // 右侧：服务端控制 + 客户端测试
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // 上方：服务端控制
        JPanel serverPanel = new JPanel(new BorderLayout(5, 5));
        serverPanel.setBorder(new TitledBorder("服务端控制"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("状态: 已停止");
        statusLabel.setForeground(Color.RED);
        btnToggle = new JButton("启动服务");
        btnToggle.addActionListener(this::doToggle);

        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(btnToggle);
        serverPanel.add(controlPanel, BorderLayout.NORTH);

        logArea = new JTextArea(6, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        serverPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // 下方：客户端测试
        JPanel clientPanel = new JPanel(new BorderLayout(5, 5));
        clientPanel.setBorder(new TitledBorder("客户端测试"));

        JPanel cmdPanel = new JPanel(new BorderLayout(5, 5));
        cmdField = new JTextField();
        JButton btnSend = new JButton("发送");
        btnSend.addActionListener(this::doSend);
        cmdPanel.add(new JLabel("命令:"), BorderLayout.WEST);
        cmdPanel.add(cmdField, BorderLayout.CENTER);
        cmdPanel.add(btnSend, BorderLayout.EAST);

        clientPanel.add(cmdPanel, BorderLayout.NORTH);

        clientResponseArea = new JTextArea(4, 30);
        clientResponseArea.setEditable(false);
        clientResponseArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        clientPanel.add(new JScrollPane(clientResponseArea), BorderLayout.CENTER);

        rightPanel.add(serverPanel, BorderLayout.CENTER);
        rightPanel.add(clientPanel, BorderLayout.SOUTH);

        add(cmdListPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createCommandListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(150, 0));
        panel.setBorder(new TitledBorder("快捷命令"));

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
        cmdList.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cmdList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 点击命令自动填入输入框
        cmdList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = cmdList.getSelectedValue();
                if (selected != null && !selected.startsWith("──")) {
                    cmdField.setText(selected);
                }
            }
        });

        panel.add(new JScrollPane(cmdList), BorderLayout.CENTER);

        // 设备ID对照表
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(new TitledBorder("设备ID对照"));
        String[] legends = {"d1 - 客厅灯", "d2 - 卧室灯", "d3 - 客厅空调", "d4 - 卧室空调", "d5 - 客厅窗帘", "d6 - 智能音箱"};
        for (String legend : legends) {
            JLabel lbl = new JLabel(legend);
            lbl.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            lbl.setForeground(Color.GRAY);
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
                statusLabel.setForeground(new Color(0, 153, 0));
                btnToggle.setText("停止服务");
            }
        } else {
            deviceServer.stopServer();
            statusLabel.setText("状态: 已停止");
            statusLabel.setForeground(Color.RED);
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
