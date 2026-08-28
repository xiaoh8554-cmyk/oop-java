package gui;

import model.UserRole;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Design System and Theme Utilities for APU Medical Centre HMS.
 */
public class UITheme {

    // Brand Colors
    public static final Color PRIMARY = new Color(0x1E, 0x3A, 0x8A);       // Deep Navy
    public static final Color PRIMARY_DARK = new Color(0x17, 0x25, 0x54);  // Midnight Navy
    public static final Color PRIMARY_LIGHT = new Color(0x3B, 0x82, 0xF6); // Vivid Blue
    public static final Color TEAL = new Color(0x0D, 0x94, 0x88);          // Medical Teal
    public static final Color TEAL_LIGHT = new Color(0xCC, 0xFB, 0xF1);    // Soft Mint
    public static final Color ACCENT = new Color(0x02, 0x84, 0xC7);        // Ocean Blue

    // Surface & Background Colors
    public static final Color BG_MAIN = new Color(0xF8, 0xFA, 0xFC);       // Soft Slate White
    public static final Color CARD_BG = new Color(0xFF, 0xFF, 0xFF);       // Pure White
    public static final Color CARD_BORDER = new Color(0xE2, 0xE8, 0xF0);   // Subtle Gray
    public static final Color SIDEBAR_BG = new Color(0x0F, 0x17, 0x2A);    // Dark Slate
    public static final Color SIDEBAR_HOVER = new Color(0x1E, 0x29, 0x3B); // Slate Hover

    // Text Colors
    public static final Color TEXT_DARK = new Color(0x0F, 0x17, 0x2A);     // Primary Text
    public static final Color TEXT_MUTED = new Color(0x64, 0x74, 0x8B);    // Secondary/Muted
    public static final Color TEXT_LIGHT = new Color(0xF1, 0xF5, 0xF9);    // Inverse Light Text

    // Status Colors
    public static final Color SUCCESS = new Color(0x10, 0xB9, 0x81);       // Emerald Green
    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);        // Crimson Red
    public static final Color WARNING = new Color(0xF5, 0x9E, 0x0B);       // Amber Orange
    public static final Color INFO = new Color(0x3B, 0x82, 0xF6);          // Blue Info

    // Modern Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Creates a styled primary action button with smooth hover animation.
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_DARK, Color.WHITE);
    }

    /**
     * Creates a styled secondary action button.
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, TEAL, new Color(0x0F, 0x76, 0x6E), Color.WHITE);
    }

    /**
     * Creates an outline/ghost button.
     */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new RoundedBorder(CARD_BORDER, 8, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0xF1, 0xF5, 0xF9));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    /**
     * Creates a customized modern button with rounded corners.
     */
    public static JButton createStyledButton(String text, Color baseColor, Color hoverColor, Color textColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(textColor);
        btn.setBackground(baseColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
                btn.repaint();
            }
        });

        return btn;
    }

    /**
     * Styles a text field with comfortable padding and a subtle rounded border.
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(PRIMARY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(CARD_BORDER, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    /**
     * Styles a password field with comfortable padding.
     */
    public static void stylePasswordField(JPasswordField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(PRIMARY);
        field.setBorder(new CompoundBorder(
                new RoundedBorder(CARD_BORDER, 8, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    /**
     * Creates a modern rounded card panel.
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(CARD_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }

    /**
     * Creates a badge pill component showing a role or tag.
     */
    public static JLabel createRoleBadge(UserRole role) {
        JLabel badge = new JLabel(" " + role.getDisplayName().toUpperCase() + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(role.getBadgeColor());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        badge.setFont(FONT_SMALL_BOLD);
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        return badge;
    }

    /**
     * Custom rounded border implementation.
     */
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 4, thickness + 2, thickness + 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = thickness + 4;
            insets.top = insets.bottom = thickness + 2;
            return insets;
        }
    }
}
