package com.smarthome.network;

import com.smarthome.config.AppConfig;

import java.io.*;
import java.net.Socket;

/**
 * 设备控制客户端
 * 连接服务端发送设备控制命令
 */
public class DeviceClient {
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public DeviceClient() {
        this.host = "localhost";
        this.port = AppConfig.getInstance().getServerPort();
    }

    public DeviceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String sendCommand(String command) {
        if (out == null) return "ERROR:未连接";
        out.println(command);
        try {
            return in.readLine();
        } catch (IOException e) {
            return "ERROR:读取响应失败";
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
