package Interface;

import javax.swing.*;

import Application.Controller;
import Application.Project;

import java.awt.*;
import java.util.ArrayList;

public class SupervisorDashboard extends JFrame {

    public static ArrayList<Project> assignedProjects = new ArrayList<>();
    private Controller controller;

    public SupervisorDashboard() {
        setTitle("Supervisor Dashboard - Project Management System");
        setSize(800, 600); // Match AdminDashboard size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        controller = new Controller();

        // Initialize demo projects via Controller if empty
        if (assignedProjects.isEmpty()) {
            assignedProjects = controller.getAssignedProjects();
            if (assignedProjects.isEmpty()) {
                assignedProjects.add(new Project("AI Assistant", "Build an AI chatbot", "Dr. Smith"));
                assignedProjects.add(new Project("Web Crawler", "Develop a crawling bot", "Dr. Smith"));
            }
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Supervisor Dashboard", SwingConstants.CENTER);
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

        JButton viewProjectsBtn = new JButton("View Assigned Projects");
        viewProjectsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewProjectsBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        btnGbc.weighty = 0.0;
        buttonPanel.add(viewProjectsBtn, btnGbc);

        JButton viewSubmissionsBtn = new JButton("View Student Submissions");
        viewSubmissionsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewSubmissionsBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 1;
        buttonPanel.add(viewSubmissionsBtn, btnGbc);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        btnGbc.gridy = 2;
        buttonPanel.add(logoutBtn, btnGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0; // Center buttons vertically
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button Actions
        viewProjectsBtn.addActionListener(e -> {
            new ViewAssignedProjectsWindow();
            setVisible(false);
            dispose();
        });

        viewSubmissionsBtn.addActionListener(e -> {
            new ViewStudentSubmissionsWindow();
            setVisible(false);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new Home();
            new Notification("Logged out successfully!");
            dispose();
        });

        setVisible(true);
    }
}