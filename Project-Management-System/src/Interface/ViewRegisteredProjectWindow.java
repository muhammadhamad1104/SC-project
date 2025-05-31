package Interface;

import javax.swing.*;

import Application.Controller;
import Application.Project;

import java.awt.*;

public class ViewRegisteredProjectWindow extends JFrame {
    private Controller controller;

    public ViewRegisteredProjectWindow() {
        controller = new Controller();
        setTitle("Registered Project - Project Management System");
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

        // Title
        JLabel titleLabel = new JLabel("Registered Project", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        // Project Details
        Project project = controller.getRegisteredProject();
        if (project != null) {
            JPanel projectPanel = new JPanel(new BorderLayout());
            projectPanel.setBorder(BorderFactory.createTitledBorder(project.getTitle()));
            projectPanel.setBackground(new Color(245, 245, 245));

            JTextArea info = new JTextArea(
                    "Title: " + project.getTitle() +
                    "\nDescription: " + project.getDescription() +
                    "\nSupervisor: " + project.getSupervisor()
            );
            info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            info.setEditable(false);
            info.setBackground(new Color(245, 245, 245));
            projectPanel.add(info, BorderLayout.CENTER);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            mainPanel.add(projectPanel, gbc);
        } else {
            JLabel noProjectLabel = new JLabel("You have not registered for any project.", SwingConstants.CENTER);
            noProjectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 1.0;
            mainPanel.add(noProjectLabel, gbc);
        }

        // Back Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> dispose());
        bottomPanel.add(backBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(bottomPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}