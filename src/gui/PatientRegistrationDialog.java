package gui;

import data.DataManager;
import model.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Modern, accessible Patient Registration Dialog for APU Medical Centre.
 * Allows new patients to self-register with automatic ID generation and file persistence.
 */
public class PatientRegistrationDialog extends JDialog {
    private final DataManager dataManager;
    private final Consumer<String> onSuccessCallback;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dobField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> bloodGroupComboBox;
    private JTextField emergencyContactField;
    private JTextArea medicalHistoryArea;
    private JLabel errorLabel;

    public PatientRegistrationDialog(JFrame parent, Consumer<String> onSuccessCallback) {
        super(parent, "Patient Sign Up - APU Medical Centre", true);
        this.dataManager = DataManager.getInstance();
        this.onSuccessCallback = onSuccessCallback;

        initUI();
    }

    private void initUI() {
        setSize(560, 720);
        setMinimumSize(new Dimension(500, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        // Header Panel with medical gradient accent
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY, getWidth(), 0, UITheme.ACCENT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel("Create Patient Account");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Register to access your personal medical appointments and records");
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(new Color(0xCC, 0xFB, 0xF1));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(titleLabel);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(subtitleLabel);

        headerPanel.add(headerText, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Main Form Content inside ScrollPane
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Section 1: Account Credentials
        contentPanel.add(createSectionHeader("1. Account Credentials"));
        contentPanel.add(Box.createVerticalStrut(8));

        usernameField = new JTextField();
        UITheme.styleTextField(usernameField);
        contentPanel.add(createFieldWrapper("Username *", usernameField));

        passwordField = new JPasswordField();
        UITheme.stylePasswordField(passwordField);
        contentPanel.add(createFieldWrapper("Password *", passwordField));

        confirmPasswordField = new JPasswordField();
        UITheme.stylePasswordField(confirmPasswordField);
        contentPanel.add(createFieldWrapper("Confirm Password *", confirmPasswordField));

        fullNameField = new JTextField();
        UITheme.styleTextField(fullNameField);
        contentPanel.add(createFieldWrapper("Full Legal Name *", fullNameField));

        // Section 2: Contact Information
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(createSectionHeader("2. Contact Information"));
        contentPanel.add(Box.createVerticalStrut(8));

        emailField = new JTextField();
        UITheme.styleTextField(emailField);
        contentPanel.add(createFieldWrapper("Email Address * (e.g. john@gmail.com)", emailField));

        phoneField = new JTextField();
        UITheme.styleTextField(phoneField);
        contentPanel.add(createFieldWrapper("Phone Number * (e.g. +60 12-345 6789)", phoneField));

        emergencyContactField = new JTextField();
        UITheme.styleTextField(emergencyContactField);
        contentPanel.add(createFieldWrapper("Emergency Contact Name & Phone (e.g. +60 12-345 6789 (Soo - Father))", emergencyContactField));

        // Section 3: Health & Personal Details
        contentPanel.add(Box.createVerticalStrut(14));
        contentPanel.add(createSectionHeader("3. Health & Personal Details"));
        contentPanel.add(Box.createVerticalStrut(8));

        dobField = new JTextField("2000-01-01");
        UITheme.styleTextField(dobField);
        contentPanel.add(createFieldWrapper("Date of Birth (YYYY-MM-DD) *", dobField));

        // Gender & Blood Group in 2 columns
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        genderComboBox.setFont(UITheme.FONT_BODY);
        genderComboBox.setBackground(Color.WHITE);

        bloodGroupComboBox = new JComboBox<>(new String[]{"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-", "Unknown"});
        bloodGroupComboBox.setFont(UITheme.FONT_BODY);
        bloodGroupComboBox.setBackground(Color.WHITE);

        JPanel genderCol = new JPanel();
        genderCol.setLayout(new BoxLayout(genderCol, BoxLayout.Y_AXIS));
        genderCol.setOpaque(false);
        JLabel gLabel = new JLabel("Gender");
        gLabel.setFont(UITheme.FONT_SMALL_BOLD);
        gLabel.setForeground(UITheme.TEXT_DARK);
        genderCol.add(gLabel);
        genderCol.add(Box.createVerticalStrut(4));
        genderCol.add(genderComboBox);

        JPanel bloodCol = new JPanel();
        bloodCol.setLayout(new BoxLayout(bloodCol, BoxLayout.Y_AXIS));
        bloodCol.setOpaque(false);
        JLabel bLabel = new JLabel("Blood Group");
        bLabel.setFont(UITheme.FONT_SMALL_BOLD);
        bLabel.setForeground(UITheme.TEXT_DARK);
        bloodCol.add(bLabel);
        bloodCol.add(Box.createVerticalStrut(4));
        bloodCol.add(bloodGroupComboBox);

        rowPanel.add(genderCol);
        rowPanel.add(bloodCol);
        contentPanel.add(rowPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Medical History
        JLabel medLabel = new JLabel("Medical History / Allergies Summary (Optional):");
        medLabel.setFont(UITheme.FONT_SMALL_BOLD);
        medLabel.setForeground(UITheme.TEXT_DARK);
        medLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(medLabel);
        contentPanel.add(Box.createVerticalStrut(4));

        medicalHistoryArea = new JTextArea(3, 20);
        medicalHistoryArea.setFont(UITheme.FONT_BODY);
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        medicalHistoryArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane medScroll = new JScrollPane(medicalHistoryArea);
        medScroll.setBorder(new UITheme.RoundedBorder(UITheme.CARD_BORDER, 8, 1));
        medScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        medScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(medScroll);

        // Error message
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL_BOLD);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(errorLabel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Action Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.CARD_BORDER));

        JButton cancelButton = UITheme.createOutlineButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton submitButton = UITheme.createSecondaryButton("Complete Registration");
        submitButton.addActionListener(e -> handleRegistration());

        footerPanel.add(cancelButton);
        footerPanel.add(submitButton);
        add(footerPanel, BorderLayout.SOUTH);

        // Enter key listener on inputs
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleRegistration();
                }
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);
        confirmPasswordField.addKeyListener(enterListener);
        fullNameField.addKeyListener(enterListener);
        emailField.addKeyListener(enterListener);
        phoneField.addKeyListener(enterListener);
        dobField.addKeyListener(enterListener);
    }

    private JLabel createSectionHeader(String title) {
        JLabel label = new JLabel(title);
        label.setFont(UITheme.FONT_HEADING);
        label.setForeground(UITheme.PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createFieldWrapper(String labelText, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_SMALL_BOLD);
        label.setForeground(UITheme.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(4));
        wrapper.add(field);
        wrapper.add(Box.createVerticalStrut(8));

        return wrapper;
    }

    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String bloodGroup = (String) bloodGroupComboBox.getSelectedItem();
        String emergencyContact = emergencyContactField.getText().trim();
        String medicalHistory = medicalHistoryArea.getText().trim();

        // 1. Validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            errorLabel.setText("⚠ Please fill in all required fields (*).");
            return;
        }

        if (username.contains("|") || username.contains(" ")) {
            errorLabel.setText("⚠ Username cannot contain spaces or the '|' character.");
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("⚠ Password must be at least 4 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("⚠ Passwords do not match. Please re-enter.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            errorLabel.setText("⚠ Please enter a valid email address.");
            return;
        }

        if (dataManager.isUsernameTaken(username)) {
            errorLabel.setText("⚠ Username '" + username + "' is already taken. Choose another.");
            return;
        }

        if (dataManager.isEmailTaken(email)) {
            errorLabel.setText("⚠ Email '" + email + "' is already registered.");
            return;
        }

        // Sanitization defaults
        if (emergencyContact.isEmpty()) emergencyContact = "N/A";
        if (medicalHistory.isEmpty()) medicalHistory = "None";
        // Clean pipe symbols to prevent text database delimiter corruption
        fullName = fullName.replace("|", " ");
        emergencyContact = emergencyContact.replace("|", " ");
        medicalHistory = medicalHistory.replace("|", " ").replace("\n", "; ");

        // 2. Generate Next ID & Create Patient Object
        String newPatientId = dataManager.generateNextPatientId();
        Patient newPatient = new Patient(
                newPatientId,
                username,
                password,
                fullName,
                email,
                phone,
                dob,
                gender,
                bloodGroup,
                emergencyContact,
                medicalHistory
        );

        // 3. Persist to DataManager (users.txt & patients.txt)
        dataManager.addUser(newPatient);

        JOptionPane.showMessageDialog(
                this,
                "<html><body style='width: 280px;'>"
                        + "<h3 style='color: #0D9488; margin-bottom: 6px;'>Registration Successful!</h3>"
                        + "<p>Welcome, <b>" + fullName + "</b>!</p>"
                        + "<p>Your Patient ID is: <b>" + newPatientId + "</b></p>"
                        + "<p style='color: #64748B;'>You can now sign in using your username or email address.</p>"
                        + "</body></html>",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (onSuccessCallback != null) {
            onSuccessCallback.accept(username);
        }

        dispose();
    }
}
