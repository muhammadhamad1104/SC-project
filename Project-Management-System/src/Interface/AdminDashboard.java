package Interface;
import javax.swing.*;

import Application.Controller;
import Application.Project;

import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JPanel projectListPanel;
    private Controller controller;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Project Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        controller = new Controller();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(245, 245, 245));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchBtn.setPreferredSize(new Dimension(120, 40));
        JButton addBtn = new JButton("Add Project");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addBtn.setPreferredSize(new Dimension(120, 40));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(addBtn);
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        projectListPanel = new JPanel();
        projectListPanel.setLayout(new BoxLayout(projectListPanel, BoxLayout.Y_AXIS));
        projectListPanel.setBackground(new Color(245, 245, 245));
        projectListPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        JScrollPane scrollPane = new JScrollPane(projectListPanel);
        add(scrollPane, BorderLayout.CENTER);

        refreshProjectList(projectListPanel, controller.getAllProjects());

        addBtn.addActionListener(e -> new AddProject(projectListPanel, controller, null));

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            List<Project> filteredProjects = controller.processSearchProjects(query);
            refreshProjectList(projectListPanel, filteredProjects);
        });

        logoutBtn.addActionListener(e -> {
            new Home();
            dispose();
            setVisible(false);
        });

        setVisible(true);
    }

    public void refreshProjectList(JPanel panel, List<Project> projectList) {
        panel.removeAll();
        for (Project p : projectList) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            card.setPreferredSize(new Dimension(600, 100));
            card.setBackground(new Color(245, 245, 245));

            JTextArea info = new JTextArea(
                "Title: " + p.getTitle() +
                "\nDescription: " + p.getDescription() +
                "\nSupervisor: " + p.getSupervisor());
            info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            info.setEditable(false);
            info.setBackground(new Color(245, 245, 245));

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(new Color(245, 245, 245));
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            editBtn.setPreferredSize(new Dimension(120, 40));
            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            removeBtn.setPreferredSize(new Dimension(120, 40));
            btnPanel.add(editBtn);
            btnPanel.add(removeBtn);

            card.add(info, BorderLayout.CENTER);
            card.add(btnPanel, BorderLayout.EAST);

            panel.add(card);

            editBtn.addActionListener(e -> new AddProject(projectListPanel, controller, p));
            removeBtn.addActionListener(e -> {
                String result = controller.processRemoveProject(p);
                JOptionPane.showMessageDialog(this, result, "Remove Project", JOptionPane.INFORMATION_MESSAGE);
                refreshProjectList(panel, controller.getAllProjects());
            });
        }
        panel.revalidate();
        panel.repaint();
    }
}