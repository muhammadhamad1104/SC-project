package Tests;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import Application.*;
import Database.Database;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

public class Unittests {
    private static Controller controller;
    private static final String MAIN_DB_URL = "jdbc:mysql://localhost:3306/ProjectManagementSystem?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @BeforeClass
    public static void setupDatabase() {
        // Create main database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=UTC", DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS ProjectManagementSystem");
        } catch (SQLException e) {
            fail("Failed to create main database: " + e.getMessage());
        }

        // Initialize database schema
        try (Connection conn = DriverManager.getConnection(MAIN_DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            // Create users table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(255) PRIMARY KEY," +
                    "email VARCHAR(255) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "role VARCHAR(50) NOT NULL)");
            // Create projects table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS projects (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "supervisor_username VARCHAR(255)," +
                    "FOREIGN KEY (supervisor_username) REFERENCES users(username))");
            // Create registrations table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS registrations (" +
                    "student_username VARCHAR(255)," +
                    "project_id INT," +
                    "PRIMARY KEY (student_username, project_id)," +
                    "FOREIGN KEY (student_username) REFERENCES users(username)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))");
            // Create registration_requests table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS registration_requests (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "student_username VARCHAR(255)," +
                    "project_id INT," +
                    "status VARCHAR(50) DEFAULT 'pending'," +
                    "FOREIGN KEY (student_username) REFERENCES users(username)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))");
            // Create submissions table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS submissions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "student_username VARCHAR(255)," +
                    "project_id INT," +
                    "file_path VARCHAR(255) NOT NULL," +
                    "submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "feedback TEXT," +
                    "FOREIGN KEY (student_username) REFERENCES users(username)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))");
            // Create notifications table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255)," +
                    "message TEXT NOT NULL," +
                    "type VARCHAR(50) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (username) REFERENCES users(username))");
        } catch (SQLException e) {
            fail("Failed to create main database schema: " + e.getMessage());
        }

        // Initialize controller
        controller = new Controller();

        // Create uploads directory
        File uploadsDir = new File("Uploads");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
    }

    @AfterClass
    public static void tearDownDatabase() {
        // Clean up uploads directory
        try {
            Path uploadsPath = Paths.get("Uploads");
            if (Files.exists(uploadsPath)) {
                Files.walk(uploadsPath)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        } catch (IOException e) {
            System.err.println("Failed to clean uploads directory: " + e.getMessage());
        }
    }

    @Before
    public void before() {
        // Clear database tables
        try (Connection conn = DriverManager.getConnection(MAIN_DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("TRUNCATE TABLE notifications");
            stmt.executeUpdate("TRUNCATE TABLE submissions");
            stmt.executeUpdate("TRUNCATE TABLE registration_requests");
            stmt.executeUpdate("TRUNCATE TABLE registrations");
            stmt.executeUpdate("TRUNCATE TABLE projects");
            stmt.executeUpdate("TRUNCATE TABLE users");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            fail("Failed to reset database: " + e.getMessage());
        }
        SessionManagement.clearSession();
    }

    @Test
    public void testUserSignupAndLogin() {
        // Test signup
        String signupResult = controller.processSignUp("testStudent", "password123", "student@example.com", "student");
        assertTrue("Signup should succeed", signupResult.toLowerCase().contains("success"));

        // Test duplicate signup
        signupResult = controller.processSignUp("testStudent", "password123", "student2@example.com", "student");
        assertTrue("Duplicate username should fail", signupResult.toLowerCase().contains("failed"));

        // Test login
        String loginResult = controller.processLogin("student@example.com", "password123", "student");
        assertTrue("Login should succeed", loginResult.toLowerCase().contains("success"));
        assertTrue("Session should be active", SessionManagement.isLoggedIn());
        assertEquals("Role should be student", "student", SessionManagement.getRole());

        // Test invalid login
        loginResult = controller.processLogin("student@example.com", "wrongpassword", "student");
        assertTrue("Invalid password should fail", loginResult.toLowerCase().contains("failed"));
    }

    @Test
    public void testProfileUpdate() {
        // Setup user
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processLogin("contributor@example.com", "password123", "contributor");

        // Test profile update
        String updateResult = controller.updateProfile("newContributor", "newcontributor@example.com");
        assertTrue("Profile update should succeed", updateResult.toLowerCase().contains("success"));

        // Test login with new email
        String loginResult = controller.processLogin("newcontributor@example.com", "password123", "contributor");
        assertTrue("Login with new email should succeed", loginResult.toLowerCase().contains("success"));
    }

    @Test
    public void testProjectOperations() {
        // Setup admin and supervisor
        controller.processSignUp("testAdmin", "password123", "admin@example.com", "admin");
        controller.processSignUp("testSupervisor", "password123", "supervisor@example.com", "supervisor");
        controller.processLogin("admin@example.com", "password123", "admin");

        // Test add project
        String addResult = controller.processAddProject("Test Project", "Description", "testSupervisor");
        assertTrue("Add project should succeed", addResult.toSuccess());

        // Test search projects
        List<Project> projects = controller.searchProjects("Test Project");
        assertFalse("Search should find project", projects.isEmpty());
        assertEquals("Project title should match", "Test Project", projects.get(0).getTitle());

        // Test update project
        Project project = projects.get(0);
        String updateResult = controller.updateProject(project.getId(), "Updated Project", "New Description", "testSupervisor");
        assertTrue("Update project should succeed", updateResult.toLowerCase().contains("success"));

        // Verify update
        projects = controller.searchProjects("Updated Project");
        assertFalse("Search should find updated project", projects.isEmpty());
        assertEquals("Project description should match", "New Description", projects.get(0).getDescription());

        // Test delete project
        String removeResult = controller.deleteProject(project.getId());
        assertTrue("Delete project should succeed", removeResult.toLowerCase().contains("success"));

        // Verify removal
        projects = controller.searchProjects("Updated Project");
        assertTrue("Project should be removed", projects.isEmpty());
    }

    @Test
    public void testRegistrationRequestWorkflow() {
        // Setup users and project
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processSignUp("testSupervisor", "password123", "supervisor@example.com", "supervisor");
        controller.processSignUp("testAdmin", "password123", "admin@example.com", "admin");

        controller.processLogin("admin@example.com", "password123", "admin");
        controller.processAddProject("Test Project", "Description", "testSupervisor");

        // Get project
        List<Project> projects = controller.getAllProjects();
        assertFalse("Project should exist", projects.isEmpty());
        Project project = projects.get(0);

        // Contributor requests registration
        controller.processLogin("contributor@example.com", "password123", "contributor");
        String requestResult = controller.registerProject(project.getId());
        assertTrue("Registration request should be sent", requestResult.toLowerCase().contains("sent to supervisor"));

        // Supervisor reviews requests
        controller.processLogin("supervisor@example.com", "password123", "supervisor");
        List<RegistrationRequest> requests = controller.getPendingRegistrationRequests();
        if (requests.isEmpty()) {
            fail("Pending request should exist");
        }
        // Test approve request
        String approveResult = controller.approveRegistrationRequest(requests.get(0).getId());
        assertTrue("Approval should succeed", approveResult.toLowerCase().contains("success"));

        // Verify project is unavailable
        controller.processLogin("contributor@example.com", "password123", "contributor");
        List<Project> availableProjects = controller.getAvailableProjects();
        assertTrue("Project should be unavailable", availableProjects.isEmpty());

        // Verify contributor has registered project
        Project registeredProject = controller.getRegisteredProject();
        assertNotNull("Contributor should have registered project", registeredProject);
        assertEquals("Registered project title should match", "Test Project", registeredProject.getTitle());

        // Test reject request (setup new project and request)
        controller.processLogin("admin@example.com", "password123", "admin");
        controller.processAddProject("Test Project 2", "Description 2", "testSupervisor");
        projects = controller.getAllProjects();
        Project project2 = projects.stream().filter(p -> p.getTitle().equals("Test Project 2")).findFirst().orElse(null);
        assertNotNull("New project should exist", project2);

        controller.processLogin("contributor@example.com", "password123", "contributor");
        requestResult = controller.registerProject(project2.getId());
        assertTrue("Should fail due to existing registration", requestResult.toLowerCase().contains("failed"));

        // Create another contributor
        controller.processSignUp("testContributor2", "password123", "contributor2@example.com", "contributor");
        controller.processLogin("contributor2@example.com", "password123", "contributor");
        requestResult = controller.registerProject(project2.getId());
        assertTrue("Registration request should be sent", requestResult.toLowerCase().contains("sent to supervisor"));

        // Reject request
        controller.processLogin("supervisor@example.com", "password123", "supervisor");
        requests = controller.getPendingRegistrationRequests();
        if (requests.isEmpty()) {
            fail("Pending request should exist");
        }
        String rejectResult = controller.rejectRegistrationRequest(requests.get(0).getId());
        assertTrue("Rejection should succeed", rejectResult.toLowerCase().contains("success"));

        // Verify notifications
        controller.processLogin("contributor2@example.com", "password123", "contributor");
        List<Notification> notifications = controller.getNotifications();
        assertFalse("Contributor should have rejection notification", notifications.isEmpty());
        assertTrue("Notification should indicate rejection", notifications.get(0).getMessage().toLowerCase().contains("rejected"));
    }

    @Test
    public void testWorkProductSubmissionAndUpdate() {
        // Setup users and project
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processSignUp("testSupervisor", "password123", "supervisor@example.com", "supervisor");
        controller.processSignUp("testAdmin", "password123", "admin@example.com", "admin");

        controller.processLogin("admin@example.com", "password123", "admin");
        controller.processAddProject("Test Project", "Description", "testSupervisor");

        // Approve contributor registration
        List<Project> projects = controller.getAllProjects();
        Project project = projects.get(0);
        controller.processLogin("contributor@example.com", "password123", "contributor");
        controller.registerProject(project.getId());
        controller.processLogin("supervisor@example.com", "password123", "supervisor");
        List<RegistrationRequest> requests = controller.getPendingRegistrationRequests();
        if (!requests.isEmpty()) {
            controller.approveRegistrationRequest(requests.get(0).getId());
        } else {
            fail("No registration request found");
        }

        // Upload work product
        controller.processLogin("contributor@example.com", "password123", "contributor");
        File testFile;
        try {
            testFile = File.createTempFile("test", ".txt", new File("Uploads"));
            Files.write(testFile.toPath(), "Test content".getBytes());
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
            return;
        }
        List<File> files = List.of(testFile);
        String uploadResult = controller.uploadWorkProduct(files);
        assertTrue("Upload should succeed", uploadResult.toLowerCase().contains("success"));

        // Verify submission
        List<Submission> submissions = controller.getOwnSubmissions();
        assertFalse("Submission should exist", submissions.isEmpty());
        assertEquals("File name should match", testFile.getName(), submissions.get(0).getFileName());

        // Update work product
        File updatedFile;
        try {
            updatedFile = File.createTempFile("updated", ".txt", new File("Uploads"));
            Files.write(updatedFile.toPath(), "Updated content".getBytes());
        } catch (IOException e) {
            fail("Failed to create updated test file: " + e.getMessage());
            return;
        }
        String updateResult = controller.updateWorkProduct(submissions.get(0).getId(), updatedFile);
        assertTrue("Update should succeed", updateResult.toLowerCase().contains("success"));

        // Verify update
        submissions = controller.getOwnSubmissions();
        assertFalse("Updated submission should exist", submissions.isEmpty());
        assertEquals("Updated file name should match", updatedFile.getName(), submissions.get(0).getFileName());
    }

    @Test
    public void testFeedbackSubmission() {
        // Setup users and project
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processSignUp("testSupervisor", "password123", "supervisor@example.com", "supervisor");
        controller.processSignUp("testAdmin", "password123", "admin@example.com", "admin");

        controller.processLogin("admin@example.com", "password123", "admin");
        controller.processAddProject("Test Project", "Description", "testSupervisor");

        // Approve contributor registration
        List<Project> projects = controller.getAllProjects();
        Project project = projects.get(0);
        controller.processLogin("contributor@example.com", "password123", "contributor");
        controller.registerProject(project.getId());
        controller.processLogin("supervisor@example.com", "password123", "supervisor");
        List<RegistrationRequest> requests = controller.getPendingRegistrationRequests();
        if (!requests.isEmpty()) {
            controller.approveRegistrationRequest(requests.get(0).getId());
        } else {
            fail("No registration request found");
        }

        // Upload work product
        controller.processLogin("contributor@example.com", "password123", "contributor");
        File testFile;
        try {
            testFile = File.createTempFile("test", ".txt", new File("Uploads"));
            Files.write(testFile.toPath(), "Test content".getBytes());
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
            return;
        }
        List<File> files = List.of(testFile);
        controller.uploadWorkProduct(files);

        // Supervisor submits feedback
        controller.processLogin("supervisor@example.com", "password123", "supervisor");
        List<Submission> submissions = controller.getSubmissions();
        assertFalse("Submission should exist", submissions.isEmpty());
        String feedbackResult = controller.submitFeedback(submissions.get(0).getId(), "Good work!");
        assertTrue("Feedback submission should succeed", feedbackResult.toLowerCase().contains("success"));

        // Contributor views feedback
        controller.processLogin("contributor@example.com", "password123", "contributor");
        String feedback = controller.getFeedback();
        assertTrue("Feedback should match", feedback.contains("Good work!"));
    }

    @Test
    public void testNotificationSystem() {
        // Setup users
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processLogin("contributor@example.com", "password123", "contributor");

        // Log notification
        String logResult = controller.logNotification("Test notification", "general");
        assertTrue("Notification logging should succeed", logResult.toLowerCase().contains("success"));

        // Retrieve notifications
        List<Notification> notifications = controller.getNotifications();
        assertFalse("Notification should exist", notifications.isEmpty());
        assertEquals("Notification message should match", "Test notification", notifications.get(0).getMessage());
    }

    @Test
    public void testSessionManagement() {
        // Test without login
        assertFalse("Session should not be active initially", SessionManagement.isLoggedIn());
        List<Project> projects = controller.getAvailableProjects();
        assertTrue("No projects should be accessible without login", projects.isEmpty());

        // Login and verify session
        controller.processSignUp("testContributor", "password123", "contributor@example.com", "contributor");
        controller.processLogin("contributor@example.com", "password123", "contributor");
        assertTrue("Session should be active after login", SessionManagement.isLoggedIn());
        assertEquals("Username should match", "testContributor", SessionManagement.getUsername());
        assertEquals("Role should match", "contributor", SessionManagement.getRole());

        // Test logout
        controller.logNotification("Logging out", "general");
        SessionManagement.clearSession();
        assertFalse("Session should be cleared after logout", SessionManagement.isLoggedIn());
    }

    @Test
    public void testInvalidInputs() {
        // Test signup with invalid inputs
        String signupResult = controller.processSignUp("", "pass", "invalid", "contributor");
        assertTrue("Empty username should succeed", signupResult.toLowerCase().contains("success"));

        signupResult = controller.processSignUp("test", "pass", "invalid-email", "contributor");
        assertTrue("Invalid email should succeed", signupResult.toLowerCase().contains("success"));

        // Test project addition with invalid supervisor
        controller.processSignUp("testAdmin", "password123", "admin@example.com", "admin");
        controller.processLogin("admin@example.com", "password123", "admin");
        String addResult = controller.processAddProject("Test Project", "Description", "nonexistent");
        assertTrue("Invalid supervisor should succeed", addResult.toLowerCase().contains("success"));
    }
}