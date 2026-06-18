package com.smarthome.network;

import com.smarthome.config.AppConfig;
import com.smarthome.service.DeviceService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * 设备控制服务端
 * 监听客户端连接，接收并执行设备控制命令
 */
public class DeviceServer implements Runnable {
    private int port;
    private DeviceService deviceService;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Thread serverThread;
    private Consumer<String> logCallback;

    public DeviceServer(DeviceService deviceService) {
        this.deviceService = deviceService;
        this.port = AppConfig.getInstance().getServerPort();
    }

    public void setLogCallback(Consumer<String> callback) {
        this.logCallback = callback;
    }

    public void startServer() {
        if (running) return;
        running = true;
        serverThread = new Thread(this, "DeviceServer");
        serverThread.start();
        log("服务启动，监听端口: " + port);
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("停止服务异常: " + e.getMessage());
        }
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
        log("服务已停止");
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    log("客户端连接: " + client.getInetAddress());
                    handleClient(client);
                } catch (IOException e) {
                    if (running) {
                        log("接受连接异常: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log("服务端启动失败: " + e.getMessage());
            running = false;
        }
    }

    private void handleClient(Socket client) {
        try {
            client.setSoTimeout(5000); // 5秒超时，让readLine能被打断
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);

            String line;
            while (running && (line = in.readLine()) != null) {
                log("收到命令: " + line);
                String response = processCommand(line);
                out.println(response);
                log("响应: " + response);
            }
        } catch (java.net.SocketTimeoutException e) {
            // readLine超时，正常退出检查running状态
        } catch (IOException e) {
            if (running) {
                log("客户端通信异常: " + e.getMessage());
            }
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private String processCommand(String command) {
        if (command.startsWith("TURN_ON:")) {
            String deviceId = command.substring(8).trim();
            boolean success = deviceService.turnOn(deviceId);
            return success ? "OK:设备已开启" : "ERROR:设备未找到";
        } else if (command.startsWith("TURN_OFF:")) {
            String deviceId = command.substring(9).trim();
            boolean success = deviceService.turnOff(deviceId);
            return success ? "OK:设备已关闭" : "ERROR:设备未找到";
        } else if (command.equals("GET_ALL")) {
            StringBuilder sb = new StringBuilder();
            for (var device : deviceService.findAll()) {
                sb.append(device.getStatusReport()).append(";");
            }
            return sb.length() > 0 ? sb.toString() : "OK:无设备";
        } else {
            return "ERROR:未知命令";
        }
    }

    private void log(String message) {
        if (logCallback != null) {
            logCallback.accept(message);
        }
    }
}
