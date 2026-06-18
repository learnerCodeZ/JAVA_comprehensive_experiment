package com.smarthome.ui;

import com.smarthome.util.UIUtils;
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
        setSize(420, 440);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIUtils.BG_WHITE);

        // 渐变头部
        wrapper.add(UIUtils.createGradientHeaderPanel("请选择场景模式", 420, 80), BorderLayout.NORTH);

        // 场景卡片区域
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(UIUtils.BG_WHITE);
        cardsPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        cardsPanel.add(createSceneCard(
                "回家模式", "assets/icons/scene_home.png",
                "智能灯全部开启（亮度100%）",
                "空调开启，26°C制冷模式",
                "窗帘全部打开（开合度100%）",
                "home"));
        cardsPanel.add(Box.createVerticalStrut(10));

        cardsPanel.add(createSceneCard(
                "离家模式", "assets/icons/scene_away.png",
                "所有设备关闭",
                "节省能源，安全出门",
                "",
                "away"));
        cardsPanel.add(Box.createVerticalStrut(10));

        cardsPanel.add(createSceneCard(
                "睡眠模式", "assets/icons/scene_sleep.png",
                "智能灯全部关闭",
                "空调开启，25°C制冷模式",
                "窗帘关闭，音箱音量调至10",
                "sleep"));

        wrapper.add(cardsPanel, BorderLayout.CENTER);

        // 取消按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(UIUtils.BG_WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JButton btnCancel = UIUtils.createGradientButton("取消");
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnCancel);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        add(wrapper);
    }

    private JPanel createSceneCard(String title, String iconPath,
                                    String detail1, String detail2, String detail3,
                                    String sceneKey) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(UIUtils.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIUtils.CARD_BORDER, 1, true),
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
        infoPanel.setBackground(UIUtils.BG_WHITE);

        JLabel nameLabel = new JLabel(title);
        nameLabel.setFont(UIUtils.FONT_TITLE_14);
        infoPanel.add(nameLabel);

        if (!detail1.isEmpty()) {
            JLabel d1 = new JLabel("  • " + detail1);
            d1.setFont(UIUtils.FONT_SMALL_11);
            d1.setForeground(UIUtils.TEXT_LABEL);
            infoPanel.add(d1);
        }
        if (!detail2.isEmpty()) {
            JLabel d2 = new JLabel("  • " + detail2);
            d2.setFont(UIUtils.FONT_SMALL_11);
            d2.setForeground(UIUtils.TEXT_LABEL);
            infoPanel.add(d2);
        }
        if (!detail3.isEmpty()) {
            JLabel d3 = new JLabel("  • " + detail3);
            d3.setFont(UIUtils.FONT_SMALL_11);
            d3.setForeground(UIUtils.TEXT_LABEL);
            infoPanel.add(d3);
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // 点击/悬停事件
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedScene = sceneKey;
                dispose();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIUtils.CARD_HOVER_BORDER, 2, true),
                        new EmptyBorder(9, 9, 9, 9)));
                card.setBackground(UIUtils.CARD_HOVER_BG);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(UIUtils.CARD_BORDER, 1, true),
                        new EmptyBorder(10, 10, 10, 10)));
                card.setBackground(UIUtils.BG_WHITE);
            }
        });

        return card;
    }

    public String getSelectedScene() {
        return selectedScene;
    }
}
