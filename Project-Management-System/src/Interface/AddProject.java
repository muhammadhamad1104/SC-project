package Interface;

import javax.swing.*;

import Application.*;

import java.awt.*;
import java.util.List;

public class AddProject extends JFrame {
    public AddProject(JPanel projectListPanel, Controller controller, Project editProject) {
        setTitle(editProject == null ? "Add Project" : "Edit Project");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel(editProject == null ? "Add Project" : "Edit Project", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        formPanel.add(titleLabel, gbc);

        // Title Field
        JLabel titleFieldLabel = new JLabel("Title:");
        titleFieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        formPanel.add(titleFieldLabel, gbc);

        JTextField titleField = new JTextField(20);
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        formPanel.add(descriptionLabel, gbc);

        JTextArea descriptionArea = new JTextArea(10, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(descriptionArea,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gbc.gridx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollPane, gbc);

        // Supervisor
        JLabel supervisorLabel = new JLabel("Supervisor:");
        supervisorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(supervisorLabel, gbc);

        Database.Database db = new Database.Database();
        List<String> supervisors = db.getSupervisors();
        JComboBox<String> supervisorBox = new JComboBox<>(
            supervisors.isEmpty() ? new String[]{"No supervisors available"} : supervisors.toArray(new String[0])
        );
        supervisorBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(supervisorBox, gbc);

        if (editProject != null) {
            titleField.setText(editProject.getTitle());
            descriptionArea.setText(editProject.getDescription());
            supervisorBox.setSelectedItem(editProject.getSupervisor());
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton addBtn = new JButton(editProject == null ? "Add" : "Update");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addBtn.setPreferredSize(new Dimension(80, 40));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cancelBtn.setPreferredSize(new Dimension(80, 40));
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String supervisor = (String) supervisorBox.getSelectedItem();
            if (supervisor.equals("No supervisors available")) {
                new Notification("No supervisors available to assign.");
                return;
            }
            String result;
            if (editProject == null) {
                result = controller.processAddProject(title, description, supervisor);
            } else {
                result = controller.processUpdateProject(editProject, title, description, supervisor);
            }
            new Notification(result);
            new AdminDashboard().refreshProjectList(projectListPanel, controller.getAllProjects());
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}