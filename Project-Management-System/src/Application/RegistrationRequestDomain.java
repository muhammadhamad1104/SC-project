package Application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Database.Database;

public class RegistrationRequestDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "approveRequest":
                return approveRequest((Database.RegistrationRequest) args[0]);
            case "rejectRequest":
                return rejectRequest((Database.RegistrationRequest) args[0]);
            case "getPendingRequests":
                return getPendingRequests();
            default:
                return "Invalid operation";
        }
    }

    private String approveRequest(Database.RegistrationRequest request) {
        if (!Session.isLoggedIn() || !Session.getRole().equals("supervisor")) {
            return "Please log in as a supervisor.";
        }
        if (request == null) {
            return "Invalid request.";
        }
        if (database.approveRegistrationRequest(request.getId(), request.getProjectId())) {
            String studentIdSql = "SELECT student_id FROM registration_requests WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(studentIdSql)) {
                pstmt.setInt(1, request.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    database.logNotification(
                            studentId,
                            "Your registration request for project " + request.getProjectTitle() + " has been approved.",
                            "general"
                    );
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return "Failed to notify student.";
            }
            database.logNotification(Session.getUserId(), "Approved registration request for " + request.getStudentName(), "general");
            return "Registration request approved successfully!";
        }
        return "Failed to approve request. Project may already be assigned.";
    }

    private String rejectRequest(Database.RegistrationRequest request) {
        if (!Session.isLoggedIn() || !Session.getRole().equals("supervisor")) {
            return "Please log in as a supervisor.";
        }
        if (request == null) {
            return "Invalid request.";
        }
        if (database.rejectRegistrationRequest(request.getId())) {
            String studentIdSql = "SELECT student_id FROM registration_requests WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(studentIdSql)) {
                pstmt.setInt(1, request.getId());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    database.logNotification(
                            studentId,
                            "Your registration request for project " + request.getProjectTitle() + " has been rejected. Please register for another project.",
                            "general"
                    );
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return "Failed to notify student.";
            }
            database.logNotification(Session.getUserId(), "Rejected registration request for " + request.getStudentName(), "general");
            return "Registration request rejected successfully!";
        }
        return "Failed to reject request.";
    }

    private List<Database.RegistrationRequest> getPendingRequests() {
        if (!Session.isLoggedIn() || !Session.getRole().equals("supervisor")) {
            return new ArrayList<>();
        }
        return database.getPendingRegistrationRequests(Session.getUserId());
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