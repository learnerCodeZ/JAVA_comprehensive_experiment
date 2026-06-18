package com.smarthome.ui;

import com.smarthome.service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
        setSize(420, 420);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 顶部装饰区域
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 渐变背景
                GradientPaint gradient = new GradientPaint(0, 0, new Color(41, 128, 185),
                        getWidth(), getHeight(), new Color(52, 152, 219));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // 房屋图标
                g2.setColor(new Color(255, 255, 255, 200));
                int cx = getWidth() / 2;
                int cy = 50;
                // 屋顶
                int[] roofX = {cx - 30, cx, cx + 30};
                int[] roofY = {cy + 15, cy - 10, cy + 15};
                g2.fillPolygon(roofX, roofY, 3);
                // 房体
                g2.fillRect(cx - 22, cy + 15, 44, 30);
                // 门
                g2.setColor(new Color(41, 128, 185));
                g2.fillRect(cx - 5, cy + 25, 10, 20);
                // 窗户
                g2.setColor(new Color(41, 128, 185));
                g2.fillRect(cx - 16, cy + 19, 7, 9);
                g2.fillRect(cx + 9, cy + 19, 7, 9);
            }
        };
        headerPanel.setPreferredSize(new Dimension(420, 100));

        // 标题写在 header 上
        JLabel titleLabel = new JLabel("智能家居控制系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 19));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 75, 420, 25);
        headerPanel.setLayout(null);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 表单区域 —— 使用 GridBagLayout 居中对齐
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        // 提示文字
        JLabel hintLabel = new JLabel("欢迎登录", SwingConstants.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        hintLabel.setForeground(new Color(150, 150, 150));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gbc.gridy = 0;
        formPanel.add(hintLabel, gbc);

        // 用户名标签
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 0, 2, 0);
        JLabel userLabel = new JLabel("用户名");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(userLabel, gbc);

        // 用户名输入框
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
        formPanel.add(usernameField, gbc);

        // 密码标签
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel passLabel = new JLabel("密码");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        passLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(passLabel, gbc);

        // 密码输入框
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
        formPanel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 8, 0);
        JButton loginBtn = new JButton("登 录") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(41, 128, 185);
                Color c2 = new Color(52, 152, 219);
                if (getModel().isRollover()) {
                    c1 = new Color(31, 97, 141);
                    c2 = new Color(41, 128, 185);
                }
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 15));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(300, 42));
        loginBtn.setContentAreaFilled(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn, gbc);

        // 账号提示
        gbc.gridy = 6;
        gbc.insets = new Insets(8, 0, 0, 0);
        JLabel tipLabel = new JLabel("admin / admin123    |    user / user123", SwingConstants.CENTER);
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        tipLabel.setForeground(new Color(180, 180, 180));
        formPanel.add(tipLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 回车登录
        getRootPane().setDefaultButton(loginBtn);

        add(mainPanel);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userService.login(username, password)) {
            loginSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocusInWindow();
        }
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}
