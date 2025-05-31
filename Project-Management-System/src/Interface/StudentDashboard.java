package Interface;

import javax.swing.*;

import Application.Controller;

import java.awt.*;

public class StudentDashboard extends JFrame {
    private Controller controller;

    public StudentDashboard() {
        controller = new Controller();
        setTitle("Student Dashboard - Project Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(15, 15, 15, 15);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.weightx = 1.0;

        JButton registerProjectBtn = new JButton("Register Project");
        registerProjectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        registerProjectBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        btnGbc.weighty = 0.0;
        buttonPanel.add(registerProjectBtn, btnGbc);

        JButton viewProjectBtn = new JButton("View Registered Project");
        viewProjectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewProjectBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 1;
        buttonPanel.add(viewProjectBtn, btnGbc);

        JButton uploadWorkBtn = new JButton("Upload Work Product");
        uploadWorkBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        uploadWorkBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 2;
        buttonPanel.add(uploadWorkBtn, btnGbc);

        JButton viewFeedbackBtn = new JButton("View Feedback");
        viewFeedbackBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewFeedbackBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 3;
        buttonPanel.add(viewFeedbackBtn, btnGbc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 4;
        buttonPanel.add(logoutBtn, btnGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        registerProjectBtn.addActionListener(e -> {
            if (controller.getRegisteredProject() != null) {
                new Notification("You have already registered a project.");
            } else {
                new RegisterProjectWindow();
            }
        });

        viewProjectBtn.addActionListener(e -> new ViewRegisteredProjectWindow());
        uploadWorkBtn.addActionListener(e -> new UploadWorkProductWindow());
        viewFeedbackBtn.addActionListener(e -> new ViewFeedbackWindow());
        logoutBtn.addActionListener(e -> {
            new Home();
            new Notification("Logged out successfully!");
            dispose();
        });

        setVisible(true);
    }
}