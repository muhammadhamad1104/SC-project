package Database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Application.Project;
import Application.StudentSubmission;

public class Database {
    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/ProjectManagementSystem?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            initializeDatabase(conn);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }
    private void initializeDatabase(Connection conn) throws SQLException {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "email VARCHAR(100) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "role ENUM('admin', 'supervisor', 'student') NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "INDEX idx_email (email)," +
                "INDEX idx_username (username)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String projectsTable = "CREATE TABLE IF NOT EXISTS projects (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "title VARCHAR(100) NOT NULL," +
                "description TEXT," +
                "supervisor_id INT," +
                "is_available BOOLEAN DEFAULT TRUE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (supervisor_id) REFERENCES users(id) ON DELETE SET NULL," +
                "INDEX idx_title (title)," +
                "INDEX idx_supervisor_id (supervisor_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String registrationsTable = "CREATE TABLE IF NOT EXISTS registrations (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "student_id INT NOT NULL," +
                "project_id INT NOT NULL," +
                "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE," +
                "UNIQUE (student_id, project_id)," +
                "INDEX idx_student_id (student_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String registrationRequestsTable = "CREATE TABLE IF NOT EXISTS registration_requests (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "student_id INT NOT NULL," +
                "project_id INT NOT NULL," +
                "status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending'," +
                "requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NULL," +
                "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE," +
                "UNIQUE (student_id, project_id)," +
                "INDEX idx_student_id (student_id)," +
                "INDEX idx_project_id (project_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String submissionsTable = "CREATE TABLE IF NOT EXISTS submissions (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "student_id INT NOT NULL," +
                "project_id INT NOT NULL," +
                "file_path VARCHAR(255) NOT NULL," +
                "feedback TEXT," +
                "submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "feedback_updated_at TIMESTAMP NULL," +
                "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE," +
                "INDEX idx_student_id (student_id)," +
                "INDEX idx_project_id (project_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        String notificationsTable = "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT," +
                "message TEXT NOT NULL," +
                "status ENUM('general', 'registration_request') DEFAULT 'general'," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL," +
                "INDEX idx_user_id (user_id)," +
                "INDEX idx_timestamp (timestamp)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(projectsTable);
            stmt.execute(registrationsTable);
            stmt.execute(registrationRequestsTable);
            stmt.execute(submissionsTable);
            stmt.execute(notificationsTable);
        }
    }

    // User Operations
    public int authenticateUser(String email, String password, String role) {
        String sql = "SELECT id FROM users WHERE email = ? AND password = ? AND role = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    public boolean createUser(String username, String email, String password, String role) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, role.toLowerCase());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateUserProfile(int userId, String username, String email) {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setInt(3, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<String> getSupervisors() {
        String sql = "SELECT username FROM users WHERE role = 'supervisor'";
        List<String> supervisors = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                supervisors.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return supervisors;
    }

    // Project Operations
    public boolean addProject(String title, String description, String supervisorUsername) {
        String sql = "INSERT INTO projects (title, description, supervisor_id) " +
                "SELECT ?, ?, id FROM users WHERE username = ? AND role = 'supervisor'";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, supervisorUsername);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateProject(int projectId, String title, String description, String supervisorUsername) {
        String sql = "UPDATE projects SET title = ?, description = ?, supervisor_id = " +
                "(SELECT id FROM users WHERE username = ? AND role = 'supervisor') WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, supervisorUsername);
            pstmt.setInt(4, projectId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean removeProject(int projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<Project> searchProjects(String query) {
        String sql = "SELECT p.id, p.title, p.description, u.username AS supervisor " +
                "FROM projects p LEFT JOIN users u ON p.supervisor_id = u.id " +
                "WHERE p.title LIKE ? OR p.description LIKE ?";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("supervisor"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return projects;
    }

    public List<Project> getAllProjects() {
        String sql = "SELECT p.id, p.title, p.description, u.username AS supervisor " +
                "FROM projects p LEFT JOIN users u ON p.supervisor_id = u.id";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("supervisor"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return projects;
    }

    // Project Assignment
    public List<Project> getAvailableProjects() {
        String sql = "SELECT p.id, p.title, p.description, u.username AS supervisor " +
                "FROM projects p LEFT JOIN users u ON p.supervisor_id = u.id WHERE p.is_available = TRUE";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("supervisor"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return projects;
    }

    public List<Project> getAssignedProjects(int supervisorId) {
        String sql = "SELECT p.id, p.title, p.description, u.username AS supervisor " +
                "FROM projects p LEFT JOIN users u ON p.supervisor_id = u.id WHERE p.supervisor_id = ?";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supervisorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("supervisor"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return projects;
    }

    // Submission Management
    public boolean addSubmission(int studentId, int projectId, String filePath) {
        String sql = "INSERT INTO submissions (student_id, project_id, file_path) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, projectId);
            pstmt.setString(3, filePath);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateSubmission(int submissionId, String filePath) {
        String sql = "UPDATE submissions SET file_path = ?, submitted_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, filePath);
            pstmt.setInt(2, submissionId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<StudentSubmission> getSubmissions(int supervisorId) {
        String sql = "SELECT s.id, s.student_id, s.project_id, u.username AS student_name, p.title AS project_title, s.file_path, s.feedback " +
                "FROM submissions s JOIN users u ON s.student_id = u.id " +
                "JOIN projects p ON s.project_id = p.id WHERE p.supervisor_id = ?";
        List<StudentSubmission> submissions = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supervisorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                StudentSubmission submission = new StudentSubmission(
                        rs.getString("student_name"),
                        rs.getString("project_title"),
                        new File(rs.getString("file_path"))
                );
                submission.setId(rs.getInt("id"));
                submission.setFeedback(rs.getString("feedback"));
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return submissions;
    }

    public List<StudentSubmission> getStudentSubmissions(int studentId) {
        String sql = "SELECT s.id, s.student_id, s.project_id, u.username AS student_name, p.title AS project_title, s.file_path, s.feedback " +
                "FROM submissions s JOIN users u ON s.student_id = u.id " +
                "JOIN projects p ON s.project_id = p.id WHERE s.student_id = ?";
        List<StudentSubmission> submissions = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                StudentSubmission submission = new StudentSubmission(
                        rs.getString("student_name"),
                        rs.getString("project_title"),
                        new File(rs.getString("file_path"))
                );
                submission.setId(rs.getInt("id"));
                submission.setFeedback(rs.getString("feedback"));
                submissions.add(submission);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return submissions;
    }

    // Feedback Management
    public boolean updateFeedback(int submissionId, String feedback) {
        String sql = "UPDATE submissions SET feedback = ?, feedback_updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, feedback);
            pstmt.setInt(2, submissionId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public String getFeedback(int studentId, int projectId) {
        String sql = "SELECT feedback FROM submissions WHERE student_id = ? AND project_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String feedback = rs.getString("feedback");
                return feedback != null ? feedback : "No feedback provided.";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "No feedback available.";
    }

    // Registration Request Management
    public boolean createRegistrationRequest(int studentId, int projectId) {
        String checkSql = "SELECT id FROM registration_requests WHERE student_id = ? AND status = 'pending'";
        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, studentId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Already has a pending request
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        String sql = "INSERT INTO registration_requests (student_id, project_id) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, projectId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean approveRegistrationRequest(int requestId, int projectId) {
        String checkSql = "SELECT id FROM registrations WHERE project_id = ?";
        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, projectId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Project already assigned
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }

        String sql = "UPDATE registration_requests SET status = 'approved', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                String insertSql = "INSERT INTO registrations (student_id, project_id) " +
                        "SELECT student_id, project_id FROM registration_requests WHERE id = ?";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, requestId);
                    insertStmt.executeUpdate();
                }
                String updateSql = "UPDATE projects SET is_available = FALSE WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, projectId);
                    updateStmt.executeUpdate();
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean rejectRegistrationRequest(int requestId) {
        String sql = "UPDATE registration_requests SET status = 'rejected', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<RegistrationRequest> getPendingRegistrationRequests(int supervisorId) {
        String sql = "SELECT rr.id, rr.student_id, rr.project_id, u.username AS student_name, p.title AS project_title " +
                "FROM registration_requests rr " +
                "JOIN users u ON rr.student_id = u.id " +
                "JOIN projects p ON rr.project_id = p.id " +
                "WHERE p.supervisor_id = ? AND rr.status = 'pending'";
        List<RegistrationRequest> requests = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supervisorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(new RegistrationRequest(
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("project_title"),
                        rs.getInt("project_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return requests;
    }

    // Registration
    public Project getRegisteredProject(int studentId) {
        String sql = "SELECT p.id, p.title, p.description, u.username AS supervisor " +
                "FROM projects p JOIN registrations r ON p.id = r.project_id " +
                "LEFT JOIN users u ON p.supervisor_id = u.id WHERE r.student_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Project(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("supervisor"),
                        rs.getInt("id")
                );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Notification
    public void logNotification(int userId, String message, String status) {
        String sql = "INSERT INTO notifications (user_id, message, status) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, message);
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<Notification> getNotifications(int userId) {
        String sql = "SELECT id, message, status, timestamp FROM notifications WHERE user_id = ? ORDER BY timestamp DESC";
        List<Notification> notifications = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return notifications;
    }

    // New helper class for registration requests
public static class RegistrationRequest {
    private int id;
    private String studentName;
    private String projectTitle;
    private int projectId;

    public RegistrationRequest(int id, String studentName, String projectTitle, int projectId) {
        this.id = id;
        this.studentName = studentName;
        this.projectTitle = projectTitle;
        this.projectId = projectId;
    }

    public int getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getProjectTitle() { return projectTitle; }
    public int getProjectId() { return projectId; }
}

// New helper class for notifications
public class Notification {
    private int id;
    private String message;
    private String status;
    private Timestamp timestamp;

    public Notification(int id, String message, String status, Timestamp timestamp) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public Timestamp getTimestamp() { return timestamp; }
}
}

