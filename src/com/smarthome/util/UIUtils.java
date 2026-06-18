package com.smarthome.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * UI工具类 - 提供统一的界面样式常量与静态工厂方法
 *
 * 设计参考: LoginDialog (钢蓝色渐变风格)
 * 色彩主调: #2980b9 (钢蓝) → #3498db (浅蓝)
 */
public class UIUtils {

    // ==================== 配色 ====================

    // 主色调 - 钢蓝渐变
    public static final Color PRIMARY_START   = new Color(41, 128, 185);
    public static final Color PRIMARY_END     = new Color(52, 152, 219);
    public static final Color HOVER_START     = new Color(31, 97, 141);
    public static final Color HOVER_END       = new Color(41, 128, 185);

    // 背景 & 边框
    public static final Color BG_WHITE        = Color.WHITE;
    public static final Color BG_FLOOR        = new Color(245, 245, 240);
    public static final Color INPUT_BORDER    = new Color(210, 210, 210);
    public static final Color CARD_BORDER     = new Color(200, 200, 200);

    // 文字
    public static final Color TEXT_LABEL      = new Color(100, 100, 100);
    public static final Color TEXT_HINT       = new Color(150, 150, 150);
    public static final Color TEXT_DIM        = new Color(180, 180, 180);
    public static final Color TEXT_DARK       = new Color(50, 50, 50);

    // 状态色
    public static final Color STATUS_RUNNING  = new Color(0, 153, 0);
    public static final Color STATUS_STOPPED  = Color.RED;

    // 设备卡片 (ON 蓝色主题)
    public static final Color CARD_ON_BG      = new Color(235, 245, 255);
    public static final Color CARD_ON_BORDER  = new Color(41, 128, 185);
    public static final Color CARD_ON_ICON    = new Color(41, 128, 185);
    public static final Color CARD_OFF_BG     = Color.WHITE;
    public static final Color CARD_OFF_BORDER = new Color(200, 200, 200);
    public static final Color CARD_OFF_ICON   = new Color(180, 180, 180);
    public static final Color CARD_HOVER_BG   = new Color(230, 242, 255);
    public static final Color CARD_HOVER_BORDER = new Color(41, 128, 185);

    // 户型图
    public static final Color WALL_OUTER      = new Color(80, 80, 80);
    public static final Color WALL_INNER      = new Color(100, 100, 100);
    public static final Color ROOM_BG         = new Color(235, 242, 250);
    public static final Color BATHROOM_BG     = new Color(220, 230, 220);
    public static final Color BATHROOM_TEXT   = new Color(130, 150, 130);
    public static final Color ALL_UNSEL       = new Color(180, 200, 220);
    public static final Color ALL_SEL         = new Color(70, 130, 180);
    public static final Color SEL_HIGHLIGHT   = new Color(70, 130, 180, 60);

    // ==================== 字体 ====================

    public static final Font FONT_TITLE_19  = new Font("微软雅黑", Font.BOLD, 19);
    public static final Font FONT_TITLE_16  = new Font("微软雅黑", Font.BOLD, 16);
    public static final Font FONT_TITLE_14  = new Font("微软雅黑", Font.BOLD, 14);
    public static final Font FONT_TITLE_13  = new Font("微软雅黑", Font.BOLD, 13);
    public static final Font FONT_BTN_15    = new Font("微软雅黑", Font.BOLD, 15);
    public static final Font FONT_BODY_14   = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font FONT_BODY_13   = new Font("微软雅黑", Font.PLAIN, 13);
    public static final Font FONT_BODY_12   = new Font("微软雅黑", Font.PLAIN, 12);
    public static final Font FONT_SMALL_11  = new Font("微软雅黑", Font.PLAIN, 11);
    public static final Font FONT_SMALL_10  = new Font("微软雅黑", Font.PLAIN, 10);

    // ==================== 工厂方法 ====================

    public static void applyRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    }

    /** 创建渐变风格按钮 */
    public static JButton createGradientButton(String text) {
        return createGradientButton(text, null);
    }

    /** 创建渐变风格按钮（指定尺寸） */
    public static JButton createGradientButton(String text, Dimension size) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                applyRenderingHints(g2);
                Color c1 = getModel().isRollover() ? HOVER_START : PRIMARY_START;
                Color c2 = getModel().isRollover() ? HOVER_END : PRIMARY_END;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(FONT_BTN_15);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (size != null) {
            btn.setPreferredSize(size);
            btn.setMinimumSize(size);
            btn.setMaximumSize(size);
        }
        return btn;
    }

    /** 创建圆角样式输入框 */
    public static JTextField createStyledTextField(Dimension size) {
        JTextField field = new JTextField() {
            @Override
            protected void paintBorder(Graphics g) {
                // 不画默认边框
            }
        };
        field.setFont(FONT_BODY_14);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(INPUT_BORDER, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
        if (size != null) {
            field.setPreferredSize(size);
        }
        return field;
    }

    /** 创建统一标签 */
    public static JLabel createStyledLabel(String text) {
        return new JLabel(text);
    }

    /** 创建统一样式标签 */
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /** 创建统一 TitledBorder */
    public static TitledBorder createStyledSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(FONT_SMALL_11);
        border.setTitleColor(TEXT_LABEL);
        return border;
    }

    /** 创建统一文本域 */
    public static JTextArea createStyledTextArea(boolean editable, int rows) {
        JTextArea area = new JTextArea();
        area.setEditable(editable);
        area.setFont(FONT_BODY_12);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        if (rows > 0) area.setRows(rows);
        return area;
    }

    /** 创建渐变头部面板 */
    public static JPanel createGradientHeaderPanel(String title, int width, int height) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                applyRenderingHints(g2);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        panel.setLayout(null);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE_19);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, height - 30, width, 25);
        panel.add(titleLabel);

        return panel;
    }
}
