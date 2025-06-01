package Interface;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import Application.Controller;
import Application.StudentSubmission;

public class ViewUpdateWorkProductWindow extends JFrame {
    private Controller controller;

    public ViewUpdateWorkProductWindow() {
        controller = new Controller();
        setTitle("View/Update Work Product - Project Management System");
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

        JLabel titleLabel = new JLabel("Your Work Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        JPanel submissionPanel = new JPanel(new GridBagLayout());
        submissionPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.insets = new Insets(10, 10, 10, 10);
        subGbc.fill = GridBagConstraints.BOTH;
        subGbc.weightx = 1.0;
        subGbc.weighty = 0.0;

        java.util.List<StudentSubmission> submissions = controller.getStudentOwnSubmissions();
        if (submissions.isEmpty()) {
            JLabel noSubmissionsLabel = new JLabel("No work products uploaded.", SwingConstants.CENTER);
            noSubmissionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            subGbc.gridx = 0;
            subGbc.gridy = 0;
            submissionPanel.add(noSubmissionsLabel, subGbc);
        } else {
            for (int i = 0; i < submissions.size(); i++) {
                StudentSubmission submission = submissions.get(i);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createTitledBorder(submission.getProjectTitle()));
                panel.setBackground(new Color(245, 245, 245));

                JTextArea details = new JTextArea("File: " + submission.getWorkProduct().getName() +
                        "\nFeedback: " + (submission.getFeedback() != null ? submission.getFeedback() : "No feedback"));
                details.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                details.setEditable(false);
                details.setBackground(new Color(245, 245, 245));
                panel.add(details, BorderLayout.CENTER);

                JButton updateBtn = new JButton("Update");
                updateBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                updateBtn.setPreferredSize(new Dimension(120, 40));
                updateBtn.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File newFile = fileChooser.getSelectedFile();
                        String updateResult = controller.updateWorkProduct(submission, newFile);
                        new Notification(updateResult);
                        if (updateResult.toLowerCase().contains("success")) {
                            dispose();
                            new ViewUpdateWorkProductWindow();
                        }
                    }
                });
                panel.add(updateBtn, BorderLayout.EAST);

                subGbc.gridx = 0;
                subGbc.gridy = i;
                submissionPanel.add(panel, subGbc);
            }
        }

        JScrollPane scrollPane = new JScrollPane(submissionPanel);
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