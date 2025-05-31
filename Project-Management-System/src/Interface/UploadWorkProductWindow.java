package Interface;

import javax.swing.*;

import Application.Controller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadWorkProductWindow extends JFrame {
    private Controller controller;
    private List<File> uploadedFiles;
    private JPanel fileListPanel;

    public UploadWorkProductWindow() {
        controller = new Controller();
        uploadedFiles = new ArrayList<>();
        setTitle("Upload Work Product - Project Management System");
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
        JLabel titleLabel = new JLabel("Upload Work Product", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        mainPanel.add(titleLabel, gbc);

        // File List Panel
        fileListPanel = new JPanel(new GridBagLayout());
        fileListPanel.setBackground(new Color(245, 245, 245));
        fileListPanel.setBorder(BorderFactory.createTitledBorder("Uploaded Files"));
        GridBagConstraints fileGbc = new GridBagConstraints();
        fileGbc.insets = new Insets(10, 10, 10, 10);
        fileGbc.fill = GridBagConstraints.HORIZONTAL;
        fileGbc.weightx = 1.0;
        fileGbc.weighty = 0.0;

        JScrollPane scrollPane = new JScrollPane(fileListPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton chooseFileBtn = new JButton("Choose File");
        chooseFileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chooseFileBtn.setPreferredSize(new Dimension(120, 40));
        JButton uploadBtn = new JButton("Upload");
        uploadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        uploadBtn.setPreferredSize(new Dimension(120, 40));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cancelBtn.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(chooseFileBtn);
        buttonPanel.add(uploadBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Actions
        chooseFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                uploadedFiles.add(file);
                addFileToList(file);
                new Notification("Selected and added: " + file.getName());
            }
        });

        uploadBtn.addActionListener(e -> {
            if (uploadedFiles.isEmpty()) {
                new Notification("No files selected to upload.");
            } else {
                String result = controller.uploadWorkProduct(uploadedFiles);
                new Notification(result);
                dispose();
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void addFileToList(File file) {
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBackground(new Color(245, 245, 245));
        filePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel fileNameLabel = new JLabel(file.getName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton viewBtn = new JButton("View");
        viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewBtn.setPreferredSize(new Dimension(120, 40));
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deleteBtn.setPreferredSize(new Dimension(120, 40));

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBackground(new Color(245, 245, 245));
        buttonGroup.add(viewBtn);
        buttonGroup.add(deleteBtn);

        filePanel.add(fileNameLabel, BorderLayout.CENTER);
        filePanel.add(buttonGroup, BorderLayout.EAST);

        viewBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(file);
                new Notification("Opening file: " + file.getName());
            } catch (Exception ex) {
                new Notification("Cannot open file: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            uploadedFiles.remove(file);
            fileListPanel.remove(filePanel);
            fileListPanel.revalidate();
            fileListPanel.repaint();
            new Notification("File removed: " + file.getName());
        });

        GridBagConstraints fileGbc = new GridBagConstraints();
        fileGbc.insets = new Insets(5, 5, 5, 5);
        fileGbc.fill = GridBagConstraints.HORIZONTAL;
        fileGbc.gridx = 0;
        fileGbc.gridy = fileListPanel.getComponentCount();
        fileGbc.weightx = 1.0;
        fileListPanel.add(filePanel, fileGbc);
        fileListPanel.revalidate();
        fileListPanel.repaint();
    }
}