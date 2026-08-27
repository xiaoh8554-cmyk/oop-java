package gui;

import data.AuthService;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Main Landing Dashboard for APU Medical Centre HMS.
 * Dynamically customizes layout and metrics based on the authenticated User role:
 * 1. Admin Staff
 * 2. Medical Managers
 * 3. Doctors
 * 4. Patients
 */
public class MainDashboardFrame extends JFrame {
    private final User currentUser;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("APU Medical Centre HMS - Dashboard [" + currentUser.getRole().getDisplayName() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_MAIN);

        // 1. Top Application Bar
        mainContainer.add(createTopBar(), BorderLayout.NORTH);

        // 2. Center Split (Sidebar + Dashboard Content)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(UITheme.BG_MAIN);

        bodyPanel.add(createSidebar(), BorderLayout.WEST);
        bodyPanel.add(createDashboardContent(), BorderLayout.CENTER);

        mainContainer.add(bodyPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);
    }

    /**
     * Top Navigation Bar with Logo, System Title, User Badge, and Logout.
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.CARD_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(12, 24, 12, 24));

        // Brand Info (Left)
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);

        JLabel logoIcon = new JLabel("🏥");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);

        JLabel title = new JLabel("APU MEDICAL CENTRE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(UITheme.PRIMARY);

        JLabel subtitle = new JLabel("Hospital Management System • " + currentUser.getRole().getDisplayName() + " Portal");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_MUTED);

        brandText.add(title);
        brandText.add(subtitle);

        brand.add(logoIcon);
        brand.add(brandText);

        // User Profile & Logout (Right)
        JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userSection.setOpaque(false);

        // Current Date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"));
        JLabel dateLabel = new JLabel("📅 " + today);
        dateLabel.setFont(UITheme.FONT_SMALL_BOLD);
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        // Role Badge
        JLabel roleBadge = UITheme.createRoleBadge(currentUser.getRole());

        // User name
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setFont(UITheme.FONT_BODY_BOLD);
        nameLabel.setForeground(UITheme.TEXT_DARK);

        // Logout Button
        JButton logoutBtn = UITheme.createOutlineButton("Log Out");
        logoutBtn.addActionListener(e -> handleLogout());

        userSection.add(dateLabel);
        userSection.add(Box.createHorizontalStrut(8));
        userSection.add(nameLabel);
        userSection.add(roleBadge);
        userSection.add(Box.createHorizontalStrut(8));
        userSection.add(logoutBtn);

        topBar.add(brand, BorderLayout.WEST);
        topBar.add(userSection, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Sidebar navigation showing menu options customized to the user's role.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        JLabel menuTitle = new JLabel("PORTAL NAVIGATION");
        menuTitle.setFont(UITheme.FONT_SMALL_BOLD);
        menuTitle.setForeground(new Color(0x94, 0xA3, 0xB8)); // Muted slate
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(menuTitle);
        sidebar.add(Box.createVerticalStrut(14));

        // Active Dashboard Button
        sidebar.add(createSidebarButton("📊  Main Dashboard", true));
        sidebar.add(Box.createVerticalStrut(6));

        // Role-Specific Navigation Links (Placeholders for upcoming phases)
        String[] menuItems = getRoleMenuItems();
        for (String item : menuItems) {
            sidebar.add(createSidebarButton(item, false));
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalGlue());

        // Session status footer
        JPanel sessionCard = new JPanel();
        sessionCard.setLayout(new BoxLayout(sessionCard, BoxLayout.Y_AXIS));
        sessionCard.setOpaque(false);
        sessionCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sessUser = new JLabel("Account ID: " + currentUser.getId());
        sessUser.setFont(UITheme.FONT_SMALL);
        sessUser.setForeground(new Color(0x94, 0xA3, 0xB8));

        JLabel sessRole = new JLabel("Access: " + currentUser.getRole().getDisplayName());
        sessRole.setFont(UITheme.FONT_SMALL_BOLD);
        sessRole.setForeground(new Color(0x38, 0xBD, 0xF8)); // Sky 400

        sessionCard.add(sessUser);
        sessionCard.add(Box.createVerticalStrut(2));
        sessionCard.add(sessRole);

        sidebar.add(sessionCard);

        return sidebar;
    }

    private String[] getRoleMenuItems() {
        switch (currentUser.getRole()) {
            case ADMIN_STAFF:
                return new String[]{
                        "📝  Patient Registration",
                        "🗓️  Appointment Scheduling",
                        "💳  Billing & Payment Invoicing",
                        "🛏️  Bed & Ward Allocation"
                };
            case MEDICAL_MANAGER:
                return new String[]{
                        "📊  Clinical Analytics & Reports",
                        "🩺  Doctor Assignments & Roster",
                        "🏷️  Health Metric & Billing Rules",
                        "📋  Hospital Audit & Compliance"
                };
            case DOCTOR:
                return new String[]{
                        "🩺  Patient Queue",
                        "📅  Appointments Schedule",
                        "📋  Medical Assessments",
                        "💊  Prescriptions & Notes"
                };
            case PATIENT:
                return new String[]{
                        "📅  My Appointments",
                        "📑  Treatment History",
                        "🧾  Invoices & Payments",
                        "⭐  Doctor Feedback"
                };
            default:
                return new String[]{};
        }
    }

    private JButton createSidebarButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        if (isActive) {
            btn.setBackground(UITheme.PRIMARY_LIGHT);
            btn.setForeground(Color.WHITE);
            btn.setFont(UITheme.FONT_BODY_BOLD);
        } else {
            btn.setBackground(UITheme.SIDEBAR_BG);
            btn.setForeground(new Color(0xCB, 0xD5, 0xE1));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(UITheme.SIDEBAR_BG);
                }
            });

            btn.addActionListener(e -> {
                JOptionPane.showMessageDialog(
                        this,
                        "The module '" + text.trim() + "' is configured and will be connected in the next phase.",
                        "Module Status",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }

        return btn;
    }

    /**
     * Main Dashboard Content View with Hero Banner, KPI metrics, and profile details.
     */
    private JPanel createDashboardContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UITheme.BG_MAIN);
        container.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Welcome Hero Banner
        JPanel heroBanner = createHeroBanner();
        heroBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(heroBanner);
        container.add(Box.createVerticalStrut(20));

        // 2. KPI Summary Cards Grid
        JPanel kpiGrid = createKpiGrid();
        kpiGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(kpiGrid);
        container.add(Box.createVerticalStrut(20));

        // 3. User Profile Details Card (Demonstrates Object-Oriented Polymorphic data)
        JPanel profileCard = createProfileDetailsCard();
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(profileCard);
        container.add(Box.createVerticalStrut(20));

        // 4. Operational Announcement / Notice Banner
        JPanel noticeCard = createNoticeCard();
        noticeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(noticeCard);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UITheme.BG_MAIN);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Welcome Hero Banner with soft background and user greeting.
     */
    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xEE, 0xF2, 0xFF), getWidth(), getHeight(), new Color(0xCC, 0xFB, 0xF1));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.setColor(new Color(0xC7, 0xD2, 0xFE));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel greet = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        greet.setFont(UITheme.FONT_TITLE);
        greet.setForeground(UITheme.PRIMARY_DARK);

        JLabel sub = new JLabel("APU Medical Centre Portal  •  Role: " + currentUser.getRole().getDisplayName() + "  •  " + currentUser.getRole().getDescription());
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_MUTED);

        textPanel.add(greet);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(sub);

        hero.add(textPanel, BorderLayout.CENTER);
        return hero;
    }

    /**
     * Creates role-specific KPI summary cards.
     */
    private JPanel createKpiGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 16));
        grid.setOpaque(false);

        switch (currentUser.getRole()) {
            case ADMIN_STAFF:
                AdminStaff stf = (AdminStaff) currentUser;
                grid.add(createMetricCard("Department", stf.getDepartment(), stf.getStaffRank(), UITheme.PRIMARY));
                grid.add(createMetricCard("Current Shift", stf.getShift(), "On Duty", UITheme.TEAL));
                grid.add(createMetricCard("Waiting Queue", "4 In Queue", "Avg Wait: 10 mins", UITheme.WARNING));
                grid.add(createMetricCard("Today's Check-ins", "18 Patients", "Admissions Counter", UITheme.SUCCESS));
                break;

            case MEDICAL_MANAGER:
                MedicalManager mgr = (MedicalManager) currentUser;
                grid.add(createMetricCard("Clinical Division", mgr.getDivision(), mgr.getManagementTitle(), UITheme.PRIMARY));
                grid.add(createMetricCard("Active Doctors", "12 On Duty", "Across Clinical Units", UITheme.TEAL));
                grid.add(createMetricCard("Clinical Oversight", mgr.getAssignedDepartment(), "Supervision Scope", UITheme.INFO));
                grid.add(createMetricCard("Quality Compliance", "98.4%", "Audit Rating: Optimal", UITheme.SUCCESS));
                break;

            case DOCTOR:
                Doctor doc = (Doctor) currentUser;
                grid.add(createMetricCard("Consultation Room", doc.getRoomNumber(), "Assigned Clinic", UITheme.PRIMARY));
                grid.add(createMetricCard("Today's Appointments", "8 Patients", "Next: 10:30 AM", UITheme.TEAL));
                grid.add(createMetricCard("Pending Assessments", "3 Pending", "Requires Doctor Sign-off", UITheme.WARNING));
                grid.add(createMetricCard("Consultation Fee", "RM " + String.format("%.2f", doc.getConsultationFee()), "Standard Rate", UITheme.SUCCESS));
                break;

            case PATIENT:
                Patient pat = (Patient) currentUser;
                grid.add(createMetricCard("Blood Group", pat.getBloodGroup(), "Donor Compatibility", UITheme.DANGER));
                grid.add(createMetricCard("Next Appointment", "Tomorrow 10:00 AM", "With Dr. Sarah Smith", UITheme.PRIMARY));
                grid.add(createMetricCard("Medical Records", "3 Summaries", "Available to View", UITheme.TEAL));
                grid.add(createMetricCard("Outstanding Balance", "RM 0.00", "All Invoices Settled", UITheme.SUCCESS));
                break;
        }

        return grid;
    }

    private JPanel createMetricCard(String title, String value, String footer, Color accentColor) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL_BOLD);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valLbl.setForeground(accentColor);

        JLabel footLbl = new JLabel(footer);
        footLbl.setFont(UITheme.FONT_SMALL);
        footLbl.setForeground(UITheme.TEXT_MUTED);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(footLbl);

        return card;
    }

    /**
     * Displays the polymorphic profile details of the logged in user.
     */
    private JPanel createProfileDetailsCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel header = new JLabel("Account & Role Credentials");
        header.setFont(UITheme.FONT_SUBTITLE);
        header.setForeground(UITheme.TEXT_DARK);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel detailsGrid = new JPanel(new GridLayout(3, 2, 20, 10));
        detailsGrid.setOpaque(false);

        detailsGrid.add(createDetailItem("User ID / Reference", currentUser.getId()));
        detailsGrid.add(createDetailItem("Account Role", currentUser.getRole().getDisplayName()));
        detailsGrid.add(createDetailItem("Email / Gmail Address", currentUser.getEmail()));
        detailsGrid.add(createDetailItem("Contact Number", currentUser.getPhoneNumber()));
        detailsGrid.add(createDetailItem("Role-Specific Information", currentUser.getRoleSpecificInfo()));
        detailsGrid.add(createDetailItem("Account Created Date", currentUser.getCreatedAt()));

        card.add(header, BorderLayout.NORTH);
        card.add(detailsGrid, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetailItem(String label, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL_BOLD);
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value != null && !value.isEmpty() ? value : "—");
        val.setFont(UITheme.FONT_BODY_BOLD);
        val.setForeground(UITheme.TEXT_DARK);

        p.add(lbl);
        p.add(Box.createVerticalStrut(2));
        p.add(val);
        return p;
    }

    /**
     * Notice card stating readiness for next phase operations.
     */
    private JPanel createNoticeCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(14, 0));
        card.setBackground(new Color(0xF8, 0xFA, 0xFC));

        JLabel icon = new JLabel("ℹ️");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel title = new JLabel("Hospital Management System Status");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.PRIMARY);

        JLabel desc = new JLabel("<html>Authentication and role-based session initialization complete. You are currently viewing the main dashboard landing page for <b>" + currentUser.getRole().getDisplayName() + "</b>. Further clinical operations will be activated in upcoming phases.</html>");
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_MUTED);

        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(desc);

        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    /**
     * Handles logout and returns to login frame.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of APU Medical Centre HMS?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            AuthService.getInstance().logout();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                this.dispose();
            });
        }
    }
}
