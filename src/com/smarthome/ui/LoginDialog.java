package com.smarthome.ui;

import com.smarthome.service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 登录对话框
 */
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService;
    private boolean loginSuccess;

    public LoginDialog(Frame parent, UserService userService) {
        super(parent, "智能家居系统 - 登录", true);
        this.userService = userService;
        this.loginSuccess = false;
        initUI();
    }

    private void initUI() {
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("智能家居系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("用户名:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);
        panel.add(userPanel);

        JPanel passPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passPanel.add(new JLabel("密  码:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);
        panel.add(passPanel);

        panel.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginBtn = new JButton("登录");
        JButton cancelBtn = new JButton("取消");

        loginBtn.addActionListener(e -> doLogin());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel);

        add(panel);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (userService.login(username, password)) {
            loginSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}
