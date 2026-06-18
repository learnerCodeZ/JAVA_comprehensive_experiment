package com.smarthome;

import com.smarthome.service.UserService;
import com.smarthome.ui.LoginDialog;
import com.smarthome.ui.MainFrame;
import javax.swing.*;
import java.awt.Font;

/**
 * 智能家居系统 - 主程序入口
 */
public class Main {
    public static void main(String[] args) {
        // 全局字体默认值
        Font defaultFont = new Font("微软雅黑", Font.PLAIN, 12);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Menu.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("MenuItem.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("TitledBorder.font", new Font("微软雅黑", Font.PLAIN, 11));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            UserService userService = new UserService();
            LoginDialog loginDialog = new LoginDialog(null, userService);
            loginDialog.setVisible(true);

            if (loginDialog.isLoginSuccess()) {
                MainFrame mainFrame = new MainFrame(userService);
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
