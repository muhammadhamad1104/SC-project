package Interface;

import javax.swing.*;

import Application.Controller;
import Application.Project;

import java.awt.*;
import java.util.List;

public class RegisterProjectWindow extends JFrame {
    private Controller controller;

    public RegisterProjectWindow() {
        controller = new Controller();
        setTitle("Register Project - Project Management System");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Register Project", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        Project registeredProject = controller.getRegisteredProject();
        if (registeredProject != null) {
            new Notification("You have already registered a project.");
            dispose();
            return;
        }

        JPanel projectPanel = new JPanel(new GridBagLayout());
        projectPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints projGbc = new GridBagConstraints();
        projGbc.insets = new Insets(10, 10, 10, 10);
        projGbc.fill = GridBagConstraints.BOTH;
        projGbc.weightx = 1.0;
        projGbc.weighty = 0.0;

        List<Project> projects = controller.getAvailableProjects();
        if (projects.isEmpty()) {
            JLabel noProjectsLabel = new JLabel("No projects available.", SwingConstants.CENTER);
            noProjectsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            projGbc.gridx = 0;
            projGbc.gridy = 0;
            projectPanel.add(noProjectsLabel, projGbc);
        } else {
            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createTitledBorder(project.getTitle()));
                panel.setBackground(new Color(245, 245, 245));

                JTextArea details = new JTextArea("Description: " + (project.getDescription() != null ? project.getDescription() : "No description") +
                        "\nSupervisor: " + (project.getSupervisor() != null ? project.getSupervisor() : "Unassigned"));
                details.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                details.setEditable(false);
                details.setBackground(new Color(245, 245, 245));
                panel.add(details, BorderLayout.CENTER);

                JButton registerBtn = new JButton("Request Registration");
                registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                registerBtn.setPreferredSize(new Dimension(150, 40));
                registerBtn.addActionListener(e -> {
                    String result = controller.registerProject(project);
                    new Notification(result);
                    dispose();
                });
                panel.add(registerBtn, BorderLayout.EAST);

                projGbc.gridx = 0;
                projGbc.gridy = i;
                projectPanel.add(panel, projGbc);
            }
        }

        JScrollPane scrollPane = new JScrollPane(projectPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));
        bottomPanel.add(backBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bottomPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        backBtn.addActionListener(e -> dispose());
        setVisible(true);
    }
}