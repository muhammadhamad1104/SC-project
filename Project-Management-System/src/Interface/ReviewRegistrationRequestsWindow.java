package Interface;

import javax.swing.*;
import java.awt.*;

import Application.Controller;
import Database.Database;

public class ReviewRegistrationRequestsWindow extends JFrame {
    private Controller controller;

    public ReviewRegistrationRequestsWindow() {
        controller = new Controller();
        setTitle("Review Registration Requests - Project Management System");
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

        JLabel titleLabel = new JLabel("Registration Requests", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        JPanel requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints reqGbc = new GridBagConstraints();
        reqGbc.insets = new Insets(10, 10, 10, 10);
        reqGbc.fill = GridBagConstraints.BOTH;
        reqGbc.weightx = 1.0;
        reqGbc.weighty = 0.0;

        java.util.List<Database.RegistrationRequest> requests = controller.getPendingRegistrationRequests();
        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No pending registration requests.", SwingConstants.CENTER);
            noRequestsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            reqGbc.gridx = 0;
            reqGbc.gridy = 0;
            requestPanel.add(noRequestsLabel, reqGbc);
        } else {
            for (int i = 0; i < requests.size(); i++) {
                Database.RegistrationRequest request = requests.get(i);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createTitledBorder(request.getStudentName() + " - " + request.getProjectTitle()));
                panel.setBackground(new Color(245, 245, 245));

                JTextArea details = new JTextArea("Student: " + request.getStudentName() +
                        "\nProject: " + request.getProjectTitle());
                details.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                details.setEditable(false);
                details.setBackground(new Color(245, 245, 245));
                panel.add(details, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(new Color(245, 245, 245));
                JButton approveBtn = new JButton("Approve");
                approveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                approveBtn.setPreferredSize(new Dimension(100, 30));
                JButton rejectBtn = new JButton("Reject");
                rejectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                rejectBtn.setPreferredSize(new Dimension(100, 30));
                buttonPanel.add(approveBtn);
                buttonPanel.add(rejectBtn);
                panel.add(buttonPanel, BorderLayout.EAST);

                approveBtn.addActionListener(e -> {
                    String result = controller.approveRegistrationRequest(request);
                    new Notification(result);
                    if (result.toLowerCase().contains("success")) {
                        dispose();
                        new ReviewRegistrationRequestsWindow();
                    }
                });

                rejectBtn.addActionListener(e -> {
                    String result = controller.rejectRegistrationRequest(request);
                    new Notification(result);
                    if (result.toLowerCase().contains("success")) {
                        dispose();
                        new ReviewRegistrationRequestsWindow();
                    }
                });

                reqGbc.gridx = 0;
                reqGbc.gridy = i;
                requestPanel.add(panel, reqGbc);
            }
        }

        JScrollPane scrollPane = new JScrollPane(requestPanel);
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