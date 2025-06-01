package Application;

import java.sql.*;

import Database.Database;

public class UserAuthenticationDomain {
    private Database database = new Database();
    private static final String EMAIL_REGEX = "^[^@]+@[^@]+\\.[^@]+$";
    private static final String[] SPAM_DOMAINS = {"spam.com", "junkmail.com", "fake.com"};

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "login":
                return authenticateUser((String) args[0], (String) args[1], (String) args[2]);
            case "signup":
                return createUser((String) args[0], (String) args[1], (String) args[2], (String) args[3]);
            default:
                return "Invalid operation";
        }
    }

    private String authenticateUser(String email, String password, String role) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (role == null || role.trim().isEmpty()) {
            return "Role is required.";
        }
        int userId = database.authenticateUser(email, password, role);
        if (userId != -1) {
            String username = getUsernameByEmail(email);
            Session.setSession(userId, role.toLowerCase(), username);
            database.logNotification(userId, "User logged in as", role);
            return "Login successful!";
        }
        return "Invalid credentials or role.";
    }

    private String createUser(String username, String password, String email, String role) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (username.length() > 50) {
            return "Username is too long (max 50 characters).";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        if (!email.matches(EMAIL_REGEX)) {
            return "Invalid email format.";
        }
        for (String spamDomain : SPAM_DOMAINS) {
            if (email.toLowerCase().endsWith("@" + spamDomain)) {
                return "Email domain is not allowed.";
            }
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (role == null || !role.toLowerCase().matches("admin|supervisor|student")) {
            return "Invalid role.";
        }
        if (database.createUser(username, email, password, role)) {
            database.logNotification(getUserIdByEmail(email), "Account created for", username);
            return "Account created successfully!";
        }
        return "Failed to create account. Username or email may already exist.";
    }

    private String getUsernameByEmail(String email) {
        String sql = "SELECT username FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }

    private int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
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