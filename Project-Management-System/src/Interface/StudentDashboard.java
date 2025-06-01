package Interface;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import Application.Controller;
import Application.Session;
import Database.*;

public class StudentDashboard extends JFrame {
    private Controller controller;

    public StudentDashboard() {
        setTitle("Student Dashboard - Project Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        controller = new Controller();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        JLabel welcomeLabel = new JLabel("Welcome, " + Session.getUsername(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(welcomeLabel, gbc);

        JButton viewProjectsBtn = new JButton("Register Project");
        viewProjectsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewProjectsBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(viewProjectsBtn, gbc);

        JButton viewRegisteredBtn = new JButton("View Registered Project");
        viewRegisteredBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewRegisteredBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(viewRegisteredBtn, gbc);

        JButton uploadWorkBtn = new JButton("Upload Work Product");
        uploadWorkBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        uploadWorkBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(uploadWorkBtn, gbc);

        JButton viewUpdateWorkBtn = new JButton("View/Update Work Product");
        viewUpdateWorkBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewUpdateWorkBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(viewUpdateWorkBtn, gbc);

        JButton viewFeedbackBtn = new JButton("View Feedback");
        viewFeedbackBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewFeedbackBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(viewFeedbackBtn, gbc);

        JButton viewNotificationsBtn = new JButton("View Notifications");
        viewNotificationsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewNotificationsBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(viewNotificationsBtn, gbc);

        JButton updateProfileBtn = new JButton("Update Profile");
        updateProfileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        updateProfileBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(updateProfileBtn, gbc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(logoutBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);

        viewProjectsBtn.addActionListener(e -> new RegisterProjectWindow());

        viewRegisteredBtn.addActionListener(e -> new ViewRegisteredProjectWindow());

        uploadWorkBtn.addActionListener(e -> new UploadWorkProductWindow());

        viewUpdateWorkBtn.addActionListener(e -> new ViewUpdateWorkProductWindow());

        viewFeedbackBtn.addActionListener(e -> new ViewFeedbackWindow());

        viewNotificationsBtn.addActionListener(e -> {
            List<Database.Notification> notifications = controller.getNotifications();
            if (notifications.isEmpty()) {
                new Notification("No notifications available.");
            } else {
                StringBuilder message = new StringBuilder();
                for (Database.Notification n : notifications) {
                    message.append(n.getTimestamp()).append(": ").append(n.getMessage()).append("\n");
                    if (n.getMessage().toLowerCase().contains("rejected") || n.getMessage().toLowerCase().contains("approved")) {
                        new RegisterProjectWindow();
                        break;
                    }
                }
                JTextArea textArea = new JTextArea(message.toString(), 10, 30);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        updateProfileBtn.addActionListener(e -> {
            JTextField usernameField = new JTextField(Session.getUsername(), 20);
            JTextField emailField = new JTextField(20);
            Object[] message = {
                "Username:", usernameField,
                "Email:", emailField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String result = controller.updateProfile(usernameField.getText().trim(), emailField.getText().trim());
                new Notification(result);
            }
        });

        logoutBtn.addActionListener(e -> {
            controller.logNotification("Student logged out", "general");
            Session.clearSession();
            new Home();
            dispose();
        });

        setVisible(true);
    }
}