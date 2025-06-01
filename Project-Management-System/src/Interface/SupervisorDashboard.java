package Interface;

import javax.swing.*;
import java.awt.*;

import Application.Controller;
import Application.Session;

public class SupervisorDashboard extends JFrame {
    private Controller controller;

    public SupervisorDashboard() {
        setTitle("Supervisor Dashboard - Project Management System");
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

        JLabel titleLabel = new JLabel("Supervisor Dashboard", SwingConstants.CENTER);
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

        JButton viewProjectsBtn = new JButton("View Assigned Projects");
        viewProjectsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewProjectsBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(viewProjectsBtn, gbc);

        JButton viewSubmissionsBtn = new JButton("View Student Submissions");
        viewSubmissionsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewSubmissionsBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(viewSubmissionsBtn, gbc);

        JButton reviewRequestsBtn = new JButton("Review Registration Requests");
        reviewRequestsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reviewRequestsBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(reviewRequestsBtn, gbc);

        JButton updateProfileBtn = new JButton("Update Profile");
        updateProfileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        updateProfileBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(updateProfileBtn, gbc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setPreferredSize(new Dimension(200, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(logoutBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);

        viewProjectsBtn.addActionListener(e -> new ViewAssignedProjectsWindow());

        viewSubmissionsBtn.addActionListener(e -> new ViewStudentSubmissionsWindow());

        reviewRequestsBtn.addActionListener(e -> new ReviewRegistrationRequestsWindow());

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
            controller.logNotification("Supervisor logged out", "general");
            Session.clearSession();
            new Home();
            dispose();
        });

        setVisible(true);
    }
}