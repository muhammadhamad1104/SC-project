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
            conn = DriverManager.getConnection(url,user,password);
            // Initialize tables if not exist
            initializeDatabase(conn);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    private void initializeDatabase(Connection conn) throws SQLException {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL)";
        String projectsTable = "CREATE TABLE IF NOT EXISTS projects (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "supervisor TEXT," +
                "is_available BOOLEAN DEFAULT true)";
        String submissionsTable = "CREATE TABLE IF NOT EXISTS submissions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_name TEXT NOT NULL," +
                "project_title TEXT NOT NULL," +
                "file_path TEXT NOT NULL," +
                "feedback TEXT)";
        String registrationsTable = "CREATE TABLE IF NOT EXISTS registrations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id INTEGER," +
                "project_id INTEGER," +
                "FOREIGN KEY(student_id) REFERENCES users(id)," +
                "FOREIGN KEY(project_id) REFERENCES projects(id))";
        String notificationsTable = "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "message TEXT NOT NULL," +
                "timestamp TEXT NOT NULL)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(projectsTable);
            stmt.execute(submissionsTable);
            stmt.execute(registrationsTable);
            stmt.execute(notificationsTable);
        }
    }

    // User Operations
    public boolean authenticateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean createUser(String username, String email, String password, String role) {
        String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateUserProfile(String username, String email) {
        String sql = "UPDATE users SET username = ? WHERE email = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Project Operations
    public boolean addProject(String title, String description, String supervisor) {
        String sql = "INSERT INTO projects (title, description, supervisor) VALUES (?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, supervisor);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateProject(int projectId, String title, String description, String supervisor) {
        String sql = "UPDATE projects SET title = ?, description = ?, supervisor = ? WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, supervisor);
            pstmt.setInt(4, projectId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
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
            return false;
        }
    }

    public List<Project> searchProjects(String query) {
        String sql = "SELECT * FROM projects WHERE title LIKE ? OR description LIKE ?";
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
        String sql = "SELECT * FROM projects";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
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
        String sql = "SELECT * FROM projects WHERE is_available = true";
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

    public List<Project> getAssignedProjects(String supervisor) {
        String sql = "SELECT * FROM projects WHERE supervisor = ?";
        List<Project> projects = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supervisor);
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
    public boolean addSubmission(String studentName, String projectTitle, String filePath) {
        String sql = "INSERT INTO submissions (studentName, project_title, file_path) VALUES (?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, projectTitle);
            pstmt.setString(3, filePath);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<StudentSubmission> getSubmissions() {
        String sql = "SELECT * FROM submissions";
        List<StudentSubmission> submissions = new ArrayList<>();
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                submissions.add(new StudentSubmission(
                        rs.getString("student_name"),
                        rs.getString("project_title"),
                        new File(rs.getString("file_path")))
                );
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return submissions;
    }

    // Feedback Management
    public boolean updateFeedback(int submissionId, String feedback) {
        String sql = "UPDATE submissions SET feedback = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, feedback);
            pstmt.setInt(2, submissionId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public String getFeedback(int submissionId) {
        String sql = "SELECT feedback FROM submissions WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, submissionId);
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

    // Registration
    public boolean registerProject(int studentId, int projectId) {
        String sql = "INSERT INTO registrations (student_id, project_id) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, projectId);
            pstmt.executeUpdate();
            // Update project availability
            String updateSql = "UPDATE projects SET is_available = false WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, projectId);
                updateStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Project getRegisteredProject(int studentId) {
        String sql = "SELECT p.* FROM projects p JOIN registrations r ON p.id = r.project_id WHERE r.student_id = ?";
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
    public void logNotification(String message) {
        String sql = "INSERT INTO notifications (message, timestamp) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message);
            pstmt.setString(2, new java.sql.Timestamp(System.currentTimeMillis()).toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}