package com.smarthome;

import com.smarthome.service.UserService;
import com.smarthome.ui.LoginDialog;
import com.smarthome.ui.MainFrame;
import javax.swing.*;

/**
 * 智能家居系统 - 主程序入口
 */
public class Main {
    public static void main(String[] args) {
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
