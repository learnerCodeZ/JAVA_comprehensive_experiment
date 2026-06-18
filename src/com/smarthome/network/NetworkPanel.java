package com.smarthome.network;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 网络控制面板
 * 提供服务端启动/停止和客户端测试功能
 */
public class NetworkPanel extends JPanel {
    private DeviceServer deviceServer;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JButton btnStart;
    private JButton btnStop;

    // 客户端测试组件
    private JTextField cmdField;
    private JTextArea clientResponseArea;

    public NetworkPanel(DeviceServer deviceServer) {
        this.deviceServer = deviceServer;
        deviceServer.setLogCallback(msg -> SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
        }));
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 上方：服务端控制
        JPanel serverPanel = new JPanel(new BorderLayout(5, 5));
        serverPanel.setBorder(new TitledBorder("服务端控制"));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("状态: 已停止");
        statusLabel.setForeground(Color.RED);
        btnStart = new JButton("启动服务");
        btnStop = new JButton("停止服务");
        btnStop.setEnabled(false);

        btnStart.addActionListener(this::doStart);
        btnStop.addActionListener(this::doStop);

        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(btnStart);
        controlPanel.add(btnStop);
        serverPanel.add(controlPanel, BorderLayout.NORTH);

        logArea = new JTextArea(8, 30);
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

        add(serverPanel, BorderLayout.CENTER);
        add(clientPanel, BorderLayout.SOUTH);
    }

    private void doStart(ActionEvent e) {
        deviceServer.startServer();
        if (deviceServer.isRunning()) {
            statusLabel.setText("状态: 运行中 (端口 " + deviceServer.getPort() + ")");
            statusLabel.setForeground(new Color(0, 153, 0));
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }

    private void doStop(ActionEvent e) {
        deviceServer.stopServer();
        statusLabel.setText("状态: 已停止");
        statusLabel.setForeground(Color.RED);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
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
