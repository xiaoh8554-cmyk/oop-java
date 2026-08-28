package gui;

import data.AuthService;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern, polished Login Frame for APU Medical Centre HMS.
 * Users sign in using their Username or Email + Password.
 * The system automatically detects their role and loads their role-specific dashboard.
 */
public class LoginFrame extends JFrame {
    private final AuthService authService;

    private JTextField identifierField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JLabel errorLabel;
    private JButton loginButton;

    public LoginFrame() {
        this.authService = AuthService.getInstance();
        initUI();
    }

    private void initUI() {
        setTitle("APU Medical Centre - Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 600);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setBackground(UITheme.BG_MAIN);

        // Root container with split layout (Branding Side + Login Form Side)
        JPanel rootPanel = new JPanel(new GridLayout(1, 2));
        rootPanel.setBackground(UITheme.BG_MAIN);

        // 1. Left Side: Hospital Branding Hero
        JPanel brandPanel = createBrandPanel();
        rootPanel.add(brandPanel);

        // 2. Right Side: Login Form Card
        JPanel formWrapper = createFormPanel();
        rootPanel.add(formWrapper);

        setContentPane(rootPanel);
    }

    /**
     * Builds the left-hand branding panel with medical motifs and system info.
     */
    private JPanel createBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vibrant Gradient background (Royal Navy to Ocean Blue)
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1E, 0x3A, 0x8A), getWidth(), getHeight(), new Color(0x02, 0x84, 0xC7));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle decorative medical circles
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-50, -50, 240, 240);
                g2.fillOval(getWidth() - 140, getHeight() - 140, 280, 280);
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(50, 44, 50, 44));

        // Medical Logo Icon
        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // White Medical Cross
                g2.setColor(Color.WHITE);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int w = 12;
                int len = 34;
                g2.fillRoundRect(cx - (w / 2), cy - (len / 2), w, len, 6, 6);
                g2.fillRoundRect(cx - (len / 2), cy - (w / 2), len, w, 6, 6);
                g2.dispose();
            }
        };
        logoBadge.setOpaque(false);
        logoBadge.setPreferredSize(new Dimension(64, 64));
        logoBadge.setMaximumSize(new Dimension(64, 64));
        logoBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("APU MEDICAL CENTRE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Hospital Management System (HMS)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitleLabel.setForeground(new Color(0x99, 0xF6, 0xE4)); // Bright Mint Teal
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><body style='width: 250px; color: #E2E8F0; font-family: Segoe UI; font-size: 12px; line-height: 1.6;'>"
                + "Integrated clinical operations, patient records, medical supervision, and transparent healthcare administration."
                + "</body></html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footerLabel = new JLabel("APU Medical Centre © 2026 • Single Sign-On");
        footerLabel.setFont(UITheme.FONT_SMALL);
        footerLabel.setForeground(new Color(255, 255, 255, 170));
        footerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(logoBadge);
        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(descLabel);
        panel.add(Box.createVerticalGlue());
        panel.add(footerLabel);

        return panel;
    }

    /**
     * Builds the right-hand login card with identifier (username/email) and password.
     */
    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_MAIN);

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 500));
        card.setMaximumSize(new Dimension(400, 520));

        // Header inside card
        JLabel welcomeLabel = new JLabel("Sign In");
        welcomeLabel.setFont(UITheme.FONT_TITLE);
        welcomeLabel.setForeground(UITheme.TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel promptLabel = new JLabel("Enter your username or email address to access your portal.");
        promptLabel.setFont(UITheme.FONT_SMALL);
        promptLabel.setForeground(UITheme.TEXT_MUTED);
        promptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username or Email Field
        JLabel idLabel = new JLabel("Username or Email Address:");
        idLabel.setFont(UITheme.FONT_HEADING);
        idLabel.setForeground(UITheme.TEXT_DARK);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        identifierField = new JTextField();
        UITheme.styleTextField(identifierField);
        identifierField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        identifierField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_HEADING);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        UITheme.stylePasswordField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(UITheme.FONT_SMALL);
        showPasswordCheckBox.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sign In Button
        loginButton = UITheme.createPrimaryButton("Sign In");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        // Register as Patient Button
        JButton registerButton = UITheme.createOutlineButton("Register as New Patient");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> openPatientRegistrationDialog());

        // Enter key listeners
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        identifierField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        // Assemble card
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(promptLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(idLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(identifierField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(4));
        card.add(showPasswordCheckBox);
        card.add(Box.createVerticalStrut(4));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(registerButton);

        outer.add(card);
        return outer;
    }

    private void openPatientRegistrationDialog() {
        PatientRegistrationDialog dialog = new PatientRegistrationDialog(this, (registeredUsername) -> {
            identifierField.setText(registeredUsername);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            errorLabel.setText(" ");
        });
        dialog.setVisible(true);
    }

    /**
     * Authenticates user by username or email, looks up their role, and opens their dashboard.
     */
    private void performLogin() {
        String identifier = identifierField.getText().trim();
        String password = new String(passwordField.getPassword());

        AuthService.AuthStatus status = authService.login(identifier, password);

        if (status == AuthService.AuthStatus.SUCCESS) {
            User loggedInUser = authService.getCurrentUser();
            errorLabel.setText(" ");
            // Launch main dashboard for the user's detected role
            SwingUtilities.invokeLater(() -> {
                MainDashboardFrame dashboard = new MainDashboardFrame(loggedInUser);
                dashboard.setVisible(true);
                this.dispose();
            });
        } else {
            errorLabel.setText("⚠ " + status.getMessage());
        }
    }
}
