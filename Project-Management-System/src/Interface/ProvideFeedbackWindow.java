package Interface;

import javax.swing.*;

import Application.Controller;
import Application.StudentSubmission;

import java.awt.*;

public class ProvideFeedbackWindow extends JFrame {
    private Controller controller;
    private StudentSubmission submission;

    public ProvideFeedbackWindow(StudentSubmission submission) {
        this.submission = submission;
        controller = new Controller();
        setTitle("Provide Feedback - " + submission.getStudentName());
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

        JLabel titleLabel = new JLabel("Provide Feedback", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        JLabel feedbackLabel = new JLabel("Feedback for " + submission.getStudentName() + ":");
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(feedbackLabel, gbc);

        JTextArea feedbackArea = new JTextArea(10, 30);
        feedbackArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        submitBtn.setPreferredSize(new Dimension(120, 40));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> {
            String feedback = feedbackArea.getText().trim();
            if (!feedback.isEmpty()) {
                String result = controller.submitFeedback(submission, feedback);
                new Notification(result);
                dispose();
            } else {
                new Notification("Please enter some feedback first.");
            }
        });

        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}