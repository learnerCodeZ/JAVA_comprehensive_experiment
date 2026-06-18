package com.smarthome.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * 场景选择对话框
 * 展示3种场景模式的图标、名称和效果详情
 */
public class SceneDialog extends JDialog {

    private String selectedScene = null;

    public SceneDialog(Frame parent) {
        super(parent, "场景模式", true);
        initUI();
    }

    private void initUI() {
        setSize(420, 380);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("请选择场景模式", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 三个场景卡片
        mainPanel.add(createSceneCard(
                "回家模式", "assets/icons/scene_home.png",
                "智能灯全部开启（亮度100%）",
                "空调开启，26°C制冷模式",
                "窗帘全部打开（开合度100%）",
                "home"));

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(createSceneCard(
                "离家模式", "assets/icons/scene_away.png",
                "所有设备关闭",
                "节省能源，安全出门",
                "",
                "away"));

        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(createSceneCard(
                "睡眠模式", "assets/icons/scene_sleep.png",
                "智能灯全部关闭",
                "空调开启，25°C制冷模式",
                "窗帘关闭，音箱音量调至10",
                "sleep"));

        mainPanel.add(Box.createVerticalStrut(15));

        // 取消按钮
        JButton btnCancel = new JButton("取消");
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.addActionListener(e -> dispose());
        mainPanel.add(btnCancel);

        add(mainPanel);
    }

    private JPanel createSceneCard(String title, String iconPath,
                                    String detail1, String detail2, String detail3,
                                    String sceneKey) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 左侧图标
        JLabel iconLabel = new JLabel();
        try {
            Image img = new ImageIcon(iconPath).getImage()
                    .getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            iconLabel.setText("[?]");
        }
        card.add(iconLabel, BorderLayout.WEST);

        // 右侧信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(title);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        infoPanel.add(nameLabel);

        if (!detail1.isEmpty()) {
            infoPanel.add(new JLabel("  • " + detail1));
        }
        if (!detail2.isEmpty()) {
            infoPanel.add(new JLabel("  • " + detail2));
        }
        if (!detail3.isEmpty()) {
            infoPanel.add(new JLabel("  • " + detail3));
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // 点击选择
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedScene = sceneKey;
                dispose();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(66, 133, 244), 2, true),
                        new EmptyBorder(9, 9, 9, 9)
                ));
                card.setBackground(new Color(240, 245, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                card.setBackground(UIManager.getColor("Panel.background"));
            }
        });

        return card;
    }

    public String getSelectedScene() {
        return selectedScene;
    }
}
