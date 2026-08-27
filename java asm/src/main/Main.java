package main;

import data.DataManager;
import gui.LoginFrame;

import javax.swing.*;

/**
 * Main Entry Point for APU Medical Centre Hospital Management System (HMS).
 */
public class Main {
    public static void main(String[] args) {
        // Initialize DataManager and load seed users
        DataManager.getInstance();

        // Configure modern UI look & feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Launch Login GUI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
