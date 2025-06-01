package Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.Database;

public class StudentRegistrationDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("registerProject")) {
            return registerProject((Project) args[0]);
        }
        return "Invalid operation";
    }

    private String registerProject(Project project) {
        if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
            return "Please log in as a student.";
        }
        if (project == null) {
            return "Invalid project.";
        }
        if (database.getRegisteredProject(Session.getUserId()) != null) {
            return "You have already registered a project.";
        }
        if (database.createRegistrationRequest(Session.getUserId(), project.getId())) {
            String supervisorIdSql = "SELECT supervisor_id FROM projects WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(supervisorIdSql)) {
                pstmt.setInt(1, project.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int supervisorId = rs.getInt("supervisor_id");
                    database.logNotification(
                            supervisorId,
                            "Student " + Session.getUsername() + " requested to register for project: " + project.getTitle(),
                            "registration_request"
                    );
                    database.logNotification(
                            Session.getUserId(),
                            "Registration request for project " + project.getTitle() + " sent to supervisor.",
                            "general"
                    );
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return "Failed to send registration request.";
            }
            return "Registration request sent to supervisor for approval.";
        }
        return "Failed to send registration request. You may already have a pending request.";
    }

    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/ProjectManagementSystem?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return null;
        }
    }
}