package Interface;

import javax.swing.*;

import Application.Controller;
import Application.StudentSubmission;

import java.awt.*;
import java.util.List;

public class ViewStudentSubmissionsWindow extends JFrame {
    private Controller controller;

    public ViewStudentSubmissionsWindow() {
        controller = new Controller();
        setTitle("Student Submissions - Project Management System");
        setSize(800, 600);
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
        JLabel titleLabel = new JLabel("Student Submissions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        // Submissions Panel
        JPanel submissionPanel = new JPanel(new GridBagLayout());
        submissionPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.insets = new Insets(10, 10, 10, 10);
        subGbc.fill = GridBagConstraints.BOTH;
        subGbc.weightx = 1.0;
        subGbc.weighty = 0.0;

        List<StudentSubmission> submissions = controller.getStudentSubmissions();
        for (int i = 0; i < submissions.size(); i++) {
            StudentSubmission submission = submissions.get(i);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder(submission.getStudentName()));
            panel.setBackground(new Color(245, 245, 245));

            JTextArea details = new JTextArea(
                    "Project: " + submission.getProjectTitle() +
                    "\nWork Product: " + submission.getWorkProduct().getName() +
                    "\nFeedback: " + (submission.getFeedback().isEmpty() ? "None" : submission.getFeedback())
            );
            details.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            details.setEditable(false);
            details.setBackground(new Color(245, 245, 245));
            panel.add(details, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(new Color(245, 245, 245));
            JButton viewBtn = new JButton("View");
            viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            viewBtn.setPreferredSize(new Dimension(120, 40));
            JButton feedbackBtn = new JButton("Give Feedback");
            feedbackBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            feedbackBtn.setPreferredSize(new Dimension(120, 40));

            viewBtn.addActionListener(e -> {
                try {
                    Desktop.getDesktop().open(submission.getWorkProduct());
                    new Notification("Opening file: " + submission.getWorkProduct().getName());
                } catch (Exception ex) {
                    new Notification("Cannot open file: " + ex.getMessage());
                }
            });

            feedbackBtn.addActionListener(e -> new ProvideFeedbackWindow(submission));

            buttonPanel.add(viewBtn);
            buttonPanel.add(feedbackBtn);
            panel.add(buttonPanel, BorderLayout.EAST);

            subGbc.gridx = 0;
            subGbc.gridy = i;
            submissionPanel.add(panel, subGbc);
        }

        JScrollPane scrollPane = new JScrollPane(submissionPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Back Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> {
            new SupervisorDashboard();
            dispose();
        });
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