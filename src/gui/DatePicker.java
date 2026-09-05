package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern, interactive Date Picker component for APU Medical Centre Swing GUI.
 * Uses high-DPI vector rendering for calendar icons, arrows, and day cells
 * to avoid missing font glyph boxes and text truncation dots.
 */
public class DatePicker extends JPanel {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JTextField textField;
    private final JButton calendarButton;

    private LocalDate selectedDate;
    private final List<Consumer<LocalDate>> dateChangeListeners = new ArrayList<>();

    public DatePicker() {
        this(LocalDate.of(2000, 1, 1));
    }

    public DatePicker(LocalDate initialDate) {
        this.selectedDate = initialDate != null ? initialDate : LocalDate.now();

        setLayout(new BorderLayout(6, 0));
        setOpaque(false);

        textField = new JTextField(selectedDate.format(DATE_FORMATTER));
        UITheme.styleTextField(textField);
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
        textField.setToolTipText("Click to choose a date from calendar");
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openCalendarDialog();
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    openCalendarDialog();
                }
            }
        });

        calendarButton = new JButton() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background
                g2.setColor(hovered ? UITheme.TEAL_LIGHT : new Color(0xF1, 0xF5, 0xF9));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border
                g2.setColor(hovered ? UITheme.TEAL : UITheme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                // Draw crisp vector calendar icon
                int iconSize = 16;
                int iconX = (getWidth() - iconSize) / 2;
                int iconY = (getHeight() - iconSize) / 2;
                drawVectorCalendar(g2, iconX, iconY, iconSize, hovered ? UITheme.TEAL : UITheme.PRIMARY);

                g2.dispose();
            }
        };

        calendarButton.setFocusPainted(false);
        calendarButton.setBorderPainted(false);
        calendarButton.setContentAreaFilled(false);
        calendarButton.setOpaque(false);
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calendarButton.setPreferredSize(new Dimension(42, 36));
        calendarButton.setToolTipText("Open calendar date picker");

        calendarButton.addActionListener(e -> openCalendarDialog());

        add(textField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }

    private static void drawVectorCalendar(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        // Calendar base rectangle
        g2.drawRoundRect(x, y + 3, size, size - 3, 3, 3);
        // Calendar top header strip
        g2.fillRect(x, y + 3, size, 4);
        // Top binding rings
        g2.drawLine(x + 4, y, x + 4, y + 4);
        g2.drawLine(x + size - 4, y, x + size - 4, y + 4);
        // Calendar date grid dots
        int dot = 2;
        g2.fillRect(x + 3, y + 9, dot, dot);
        g2.fillRect(x + 7, y + 9, dot, dot);
        g2.fillRect(x + 11, y + 9, dot, dot);
        g2.fillRect(x + 3, y + 12, dot, dot);
        g2.fillRect(x + 7, y + 12, dot, dot);
        g2.fillRect(x + 11, y + 12, dot, dot);
    }

    private void openCalendarDialog() {
        parseAndSyncTypedText();
        LocalDate baseDate = selectedDate != null ? selectedDate : LocalDate.now();

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        CalendarDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new CalendarDialog((Frame) parentWindow, baseDate);
        } else if (parentWindow instanceof Dialog) {
            dialog = new CalendarDialog((Dialog) parentWindow, baseDate);
        } else {
            dialog = new CalendarDialog((Frame) null, baseDate);
        }

        // Position dialog right below the DatePicker component
        Point screenPos = getLocationOnScreen();
        int dialogX = screenPos.x;
        int dialogY = screenPos.y + getHeight() + 4;

        dialog.pack();

        // Ensure popup stays on screen
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screenBounds = gc != null ? gc.getBounds() : new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        if (dialogX + dialog.getWidth() > screenBounds.x + screenBounds.width) {
            dialogX = screenBounds.x + screenBounds.width - dialog.getWidth() - 10;
        }
        if (dialogY + dialog.getHeight() > screenBounds.y + screenBounds.height) {
            dialogY = screenPos.y - dialog.getHeight() - 4; // Display above if no space below
        }
        if (dialogX < screenBounds.x) dialogX = screenBounds.x + 10;
        if (dialogY < screenBounds.y) dialogY = screenBounds.y + 10;

        dialog.setLocation(dialogX, dialogY);
        dialog.setVisible(true);

        if (dialog.isConfirmed() && dialog.getSelectedDate() != null) {
            setSelectedDate(dialog.getSelectedDate());
        }
    }

    private void parseAndSyncTypedText() {
        String txt = textField.getText().trim();
        if (txt.isEmpty()) return;
        try {
            this.selectedDate = LocalDate.parse(txt, DATE_FORMATTER);
        } catch (DateTimeParseException ignored) {
            // Keep text for user to fix
        }
    }

    public LocalDate getDate() {
        parseAndSyncTypedText();
        try {
            return LocalDate.parse(textField.getText().trim(), DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        if (date != null) {
            this.textField.setText(date.format(DATE_FORMATTER));
        } else {
            this.textField.setText("");
        }
        for (Consumer<LocalDate> listener : dateChangeListeners) {
            listener.accept(date);
        }
    }

    public String getText() {
        return textField.getText().trim();
    }

    public void setText(String text) {
        textField.setText(text);
        parseAndSyncTypedText();
    }

    public JTextField getTextField() {
        return textField;
    }

    public JButton getCalendarButton() {
        return calendarButton;
    }

    public void addDateChangeListener(Consumer<LocalDate> listener) {
        dateChangeListeners.add(listener);
    }

    @Override
    public void addKeyListener(KeyListener l) {
        textField.addKeyListener(l);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }

    /**
     * Dedicated Modal Calendar Dialog with vector rendering for icons and custom day text painting.
     */
    private static class CalendarDialog extends JDialog {
        private LocalDate currentSelectedDate;
        private YearMonth displayedYearMonth;
        private boolean confirmed = false;

        private JComboBox<String> monthCombo;
        private JComboBox<Integer> yearCombo;
        private JPanel daysGridPanel;
        private boolean updatingControls = false;

        public CalendarDialog(Frame parent, LocalDate initialDate) {
            super(parent, true);
            init(initialDate);
        }

        public CalendarDialog(Dialog parent, LocalDate initialDate) {
            super(parent, true);
            init(initialDate);
        }

        private void init(LocalDate initialDate) {
            setUndecorated(true);
            this.currentSelectedDate = initialDate != null ? initialDate : LocalDate.now();
            this.displayedYearMonth = YearMonth.from(this.currentSelectedDate);

            JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
            rootPanel.setBackground(Color.WHITE);
            rootPanel.setBorder(BorderFactory.createCompoundBorder(
                    new UITheme.RoundedBorder(UITheme.PRIMARY, 12, 2),
                    new EmptyBorder(12, 14, 12, 14)
            ));

            // 1. Header Bar: Title with Vector Icon + Close Button
            JPanel titleBar = new JPanel(new BorderLayout(8, 0));
            titleBar.setOpaque(false);

            JPanel titleLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    drawVectorCalendar(g2, 0, (getHeight() - 15) / 2, 15, UITheme.PRIMARY);
                    g2.dispose();
                }
            };
            titleLeft.setOpaque(false);
            titleLeft.setBorder(new EmptyBorder(0, 20, 0, 0));

            JLabel titleLabel = new JLabel("Select Date");
            titleLabel.setFont(UITheme.FONT_HEADING);
            titleLabel.setForeground(UITheme.PRIMARY);
            titleLeft.add(titleLabel);

            JButton closeBtn = new JButton() {
                private boolean hovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                        @Override
                        public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    });
                }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(hovered ? UITheme.DANGER : UITheme.TEXT_MUTED);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    int r = 5;
                    g2.drawLine(cx - r, cy - r, cx + r, cy + r);
                    g2.drawLine(cx + r, cy - r, cx - r, cy + r);
                    g2.dispose();
                }
            };
            closeBtn.setPreferredSize(new Dimension(24, 24));
            closeBtn.setContentAreaFilled(false);
            closeBtn.setBorderPainted(false);
            closeBtn.setFocusPainted(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            closeBtn.addActionListener(e -> dispose());

            titleBar.add(titleLeft, BorderLayout.WEST);
            titleBar.add(closeBtn, BorderLayout.EAST);
            rootPanel.add(titleBar, BorderLayout.NORTH);

            // 2. Month & Year Controls Bar
            JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
            contentPanel.setOpaque(false);

            JPanel navControls = new JPanel(new BorderLayout(6, 0));
            navControls.setOpaque(false);

            JButton prevBtn = createArrowButton(true);
            prevBtn.addActionListener(e -> {
                displayedYearMonth = displayedYearMonth.minusMonths(1);
                syncControls();
                refreshDaysGrid();
            });

            JButton nextBtn = createArrowButton(false);
            nextBtn.addActionListener(e -> {
                displayedYearMonth = displayedYearMonth.plusMonths(1);
                syncControls();
                refreshDaysGrid();
            });

            JPanel selectors = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            selectors.setOpaque(false);

            String[] months = {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            monthCombo = new JComboBox<>(months);
            monthCombo.setFont(UITheme.FONT_BODY_BOLD);
            monthCombo.setBackground(Color.WHITE);
            monthCombo.setMaximumRowCount(12);
            monthCombo.setSelectedIndex(displayedYearMonth.getMonthValue() - 1);
            monthCombo.addActionListener(e -> {
                if (!updatingControls && monthCombo.getSelectedIndex() >= 0) {
                    displayedYearMonth = displayedYearMonth.withMonth(monthCombo.getSelectedIndex() + 1);
                    refreshDaysGrid();
                }
            });

            int currentYear = LocalDate.now().getYear();
            Integer[] years = new Integer[currentYear - 1920 + 6]; // 1920 to current + 5
            for (int i = 0; i < years.length; i++) {
                years[i] = currentYear + 5 - i;
            }
            yearCombo = new JComboBox<>(years);
            yearCombo.setFont(UITheme.FONT_BODY_BOLD);
            yearCombo.setBackground(Color.WHITE);
            yearCombo.setMaximumRowCount(10);
            yearCombo.setSelectedItem(displayedYearMonth.getYear());
            yearCombo.addActionListener(e -> {
                if (!updatingControls && yearCombo.getSelectedItem() != null) {
                    displayedYearMonth = displayedYearMonth.withYear((Integer) yearCombo.getSelectedItem());
                    refreshDaysGrid();
                }
            });

            selectors.add(monthCombo);
            selectors.add(yearCombo);

            navControls.add(prevBtn, BorderLayout.WEST);
            navControls.add(selectors, BorderLayout.CENTER);
            navControls.add(nextBtn, BorderLayout.EAST);
            contentPanel.add(navControls, BorderLayout.NORTH);

            // 3. Calendar Day Matrix
            JPanel calendarGridWrapper = new JPanel(new BorderLayout(0, 4));
            calendarGridWrapper.setOpaque(false);

            JPanel weekHeaderPanel = new JPanel(new GridLayout(1, 7, 4, 2));
            weekHeaderPanel.setOpaque(false);
            String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String day : daysOfWeek) {
                JLabel lbl = new JLabel(day, SwingConstants.CENTER);
                lbl.setFont(UITheme.FONT_SMALL_BOLD);
                lbl.setForeground(day.equals("Sun") || day.equals("Sat") ? UITheme.ACCENT : UITheme.TEXT_MUTED);
                weekHeaderPanel.add(lbl);
            }
            calendarGridWrapper.add(weekHeaderPanel, BorderLayout.NORTH);

            daysGridPanel = new JPanel(new GridLayout(6, 7, 4, 4));
            daysGridPanel.setOpaque(false);
            calendarGridWrapper.add(daysGridPanel, BorderLayout.CENTER);

            contentPanel.add(calendarGridWrapper, BorderLayout.CENTER);
            rootPanel.add(contentPanel, BorderLayout.CENTER);

            // 4. Footer Actions Bar
            JPanel footerPanel = new JPanel(new BorderLayout());
            footerPanel.setOpaque(false);
            footerPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

            JButton todayBtn = new JButton("Today");
            todayBtn.setFont(UITheme.FONT_SMALL_BOLD);
            todayBtn.setForeground(UITheme.PRIMARY);
            todayBtn.setBackground(Color.WHITE);
            todayBtn.setBorder(BorderFactory.createCompoundBorder(
                    new UITheme.RoundedBorder(UITheme.CARD_BORDER, 6, 1),
                    new EmptyBorder(5, 12, 5, 12)
            ));
            todayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            todayBtn.addActionListener(e -> {
                currentSelectedDate = LocalDate.now();
                confirmed = true;
                dispose();
            });

            JButton cancelBtn = UITheme.createOutlineButton("Cancel");
            cancelBtn.setFont(UITheme.FONT_SMALL);
            cancelBtn.addActionListener(e -> dispose());

            footerPanel.add(todayBtn, BorderLayout.WEST);
            footerPanel.add(cancelBtn, BorderLayout.EAST);
            rootPanel.add(footerPanel, BorderLayout.SOUTH);

            // Close on ESC key
            getRootPane().registerKeyboardAction(e -> dispose(),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);

            setContentPane(rootPanel);
            setPreferredSize(new Dimension(360, 360));

            refreshDaysGrid();
        }

        private JButton createArrowButton(boolean isLeft) {
            JButton btn = new JButton() {
                private boolean hovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                        @Override
                        public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    });
                }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(hovered ? new Color(0xF1, 0xF5, 0xF9) : Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(UITheme.CARD_BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                    // Draw vector triangle arrow
                    g2.setColor(hovered ? UITheme.PRIMARY : UITheme.TEXT_DARK);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    Polygon arrow = new Polygon();
                    if (isLeft) {
                        arrow.addPoint(cx + 3, cy - 5);
                        arrow.addPoint(cx - 3, cy);
                        arrow.addPoint(cx + 3, cy + 5);
                    } else {
                        arrow.addPoint(cx - 3, cy - 5);
                        arrow.addPoint(cx + 3, cy);
                        arrow.addPoint(cx - 3, cy + 5);
                    }
                    g2.fillPolygon(arrow);
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(32, 28));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        private void syncControls() {
            updatingControls = true;
            if (monthCombo != null) {
                monthCombo.setSelectedIndex(displayedYearMonth.getMonthValue() - 1);
            }
            if (yearCombo != null) {
                yearCombo.setSelectedItem(displayedYearMonth.getYear());
            }
            updatingControls = false;
        }

        private void refreshDaysGrid() {
            daysGridPanel.removeAll();

            LocalDate firstOfMonth = displayedYearMonth.atDay(1);
            int startDayCol = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday=0, Monday=1...
            int daysInMonth = displayedYearMonth.lengthOfMonth();

            LocalDate today = LocalDate.now();

            // Previous month trailing days
            YearMonth prevMonth = displayedYearMonth.minusMonths(1);
            int prevMonthDays = prevMonth.lengthOfMonth();
            for (int i = startDayCol - 1; i >= 0; i--) {
                int dayNum = prevMonthDays - i;
                LocalDate date = prevMonth.atDay(dayNum);
                daysGridPanel.add(createDayButton(String.valueOf(dayNum), date, false, false, false));
            }

            // Current month days
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = displayedYearMonth.atDay(day);
                boolean isSelected = currentSelectedDate != null && currentSelectedDate.equals(date);
                boolean isToday = today.equals(date);
                daysGridPanel.add(createDayButton(String.valueOf(day), date, true, isSelected, isToday));
            }

            // Next month filler days (fill up to 42 cells)
            int remainingCells = 42 - (startDayCol + daysInMonth);
            YearMonth nextMonth = displayedYearMonth.plusMonths(1);
            for (int day = 1; day <= remainingCells; day++) {
                LocalDate date = nextMonth.atDay(day);
                daysGridPanel.add(createDayButton(String.valueOf(day), date, false, false, false));
            }

            daysGridPanel.revalidate();
            daysGridPanel.repaint();
        }

        private JButton createDayButton(String dayText, LocalDate date, boolean isCurrentMonth, boolean isSelected, boolean isToday) {
            JButton btn = new JButton() {
                private boolean hovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                        @Override
                        public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    if (isSelected) {
                        g2.setColor(UITheme.PRIMARY);
                        g2.fillRoundRect(1, 1, w - 2, h - 2, 8, 8);
                        g2.setColor(Color.WHITE);
                        g2.setFont(UITheme.FONT_BODY_BOLD);
                    } else if (isToday) {
                        g2.setColor(UITheme.TEAL_LIGHT);
                        g2.fillRoundRect(1, 1, w - 2, h - 2, 8, 8);
                        g2.setColor(UITheme.TEAL);
                        g2.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
                        g2.setColor(UITheme.TEAL);
                        g2.setFont(UITheme.FONT_BODY_BOLD);
                    } else if (hovered) {
                        g2.setColor(new Color(0xEE, 0xF2, 0xF6));
                        g2.fillRoundRect(1, 1, w - 2, h - 2, 8, 8);
                        g2.setColor(isCurrentMonth ? UITheme.TEXT_DARK : new Color(0x94, 0xA3, 0xB8));
                        g2.setFont(UITheme.FONT_BODY);
                    } else {
                        g2.setColor(isCurrentMonth ? UITheme.TEXT_DARK : new Color(0x94, 0xA3, 0xB8));
                        g2.setFont(UITheme.FONT_BODY);
                    }

                    // Centered day number drawing to completely prevent truncation "..."
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(dayText);
                    int textHeight = fm.getAscent();
                    int textX = (w - textWidth) / 2;
                    int textY = (h + textHeight) / 2 - 2;
                    g2.drawString(dayText, textX, textY);

                    g2.dispose();
                }
            };

            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                currentSelectedDate = date;
                confirmed = true;
                dispose();
            });

            return btn;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public LocalDate getSelectedDate() {
            return currentSelectedDate;
        }
    }
}
