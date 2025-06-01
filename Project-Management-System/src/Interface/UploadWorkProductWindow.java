package Interface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Application.Controller;

public class UploadWorkProductWindow extends JFrame {
    private Controller controller;

    public UploadWorkProductWindow() {
        controller = new Controller();
        setTitle("Upload Work Product - Project Management System");
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

        JLabel titleLabel = new JLabel("Upload Work Product", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        JTextArea selectedFilesArea = new JTextArea(5, 30);
        selectedFilesArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        selectedFilesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(selectedFilesArea);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton selectFilesBtn = new JButton("Select Files");
        selectFilesBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        selectFilesBtn.setPreferredSize(new Dimension(120, 40));
        JButton uploadBtn = new JButton("Upload");
        uploadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        uploadBtn.setPreferredSize(new Dimension(120, 40));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(selectFilesBtn);
        buttonPanel.add(uploadBtn);
        buttonPanel.add(backBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        List<File> selectedFiles = new ArrayList<>();
        selectFilesBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFiles.clear();
                selectedFiles.addAll(List.of(fileChooser.getSelectedFiles()));
                StringBuilder fileNames = new StringBuilder();
                for (File file : selectedFiles) {
                    fileNames.append(file.getName()).append("\n");
                }
                selectedFilesArea.setText(fileNames.toString());
            }
        });

        uploadBtn.addActionListener(e -> {
            if (selectedFiles.isEmpty()) {
                new Notification("Please select at least one file to upload.");
                return;
            }
            String result = controller.uploadWorkProduct(selectedFiles);
            new Notification(result);
            if (result.toLowerCase().contains("success")) {
                dispose();
            }
        });

        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}