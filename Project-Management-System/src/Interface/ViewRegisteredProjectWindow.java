package Interface;

import javax.swing.*;
import java.awt.*;

import Application.Controller;
import Application.Project;

public class ViewRegisteredProjectWindow extends JFrame {
    private Controller controller;

    public ViewRegisteredProjectWindow() {
        controller = new Controller();
        setTitle("View Registered Project - Project Management System");
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

        JLabel titleLabel = new JLabel("Registered Project", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        Project project = controller.getRegisteredProject();
        JPanel projectPanel = new JPanel(new GridBagLayout());
        projectPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints projGbc = new GridBagConstraints();
        projGbc.insets = new Insets(10, 10, 10, 10);
        projGbc.fill = GridBagConstraints.BOTH;
        projGbc.weightx = 1.0;
        projGbc.weighty = 0.0;

        if (project == null) {
            JLabel noProjectLabel = new JLabel("No project registered.", SwingConstants.CENTER);
            noProjectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            projGbc.gridx = 0;
            projGbc.gridy = 0;
            projectPanel.add(noProjectLabel, projGbc);
        } else {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder(project.getTitle()));
            panel.setBackground(new Color(245, 245, 245));

            JTextArea details = new JTextArea("Description: " + (project.getDescription() != null ? project.getDescription() : "No description") +
                    "\nSupervisor: " + (project.getSupervisor() != null ? project.getSupervisor() : "Unassigned"));
            details.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            details.setEditable(false);
            details.setBackground(new Color(245, 245, 245));
            panel.add(details, BorderLayout.CENTER);

            projGbc.gridx = 0;
            projGbc.gridy = 0;
            projectPanel.add(panel, projGbc);
        }

        JScrollPane scrollPane = new JScrollPane(projectPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(backBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}