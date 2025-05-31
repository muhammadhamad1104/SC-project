package Interface;

import javax.swing.*;
import java.awt.*;

public class Notification extends JFrame {
    public Notification(String message) {
        setTitle("Notification");
        setSize(400, 300); // Larger window for spacious layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        formPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Message Label
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5; // Center vertically with some weight
        formPanel.add(messageLabel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.5; // Push button to bottom but keep centered
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    
}