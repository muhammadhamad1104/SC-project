package Application;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import Database.Database;

public class SubmissionManagementDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "getStudentSubmissions":
                return getStudentSubmissions();
            case "uploadWorkProduct":
                return uploadWorkProduct((List<File>) args[0]);
            case "getStudentOwnSubmissions":
                return getStudentOwnSubmissions();
            case "updateWorkProduct":
                return updateWorkProduct((StudentSubmission) args[0], (File) args[1]);
            default:
                return "Invalid operation";
        }
    }

    private ArrayList<StudentSubmission> getStudentSubmissions() {
        if (!Session.isLoggedIn() || !Session.getRole().equals("supervisor")) {
            return new ArrayList<>();
        }
        return new ArrayList<>(database.getSubmissions(Session.getUserId()));
    }

    private ArrayList<StudentSubmission> getStudentOwnSubmissions() {
        if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
            return new ArrayList<>();
        }
        return new ArrayList<>(database.getStudentSubmissions(Session.getUserId()));
    }

    private String uploadWorkProduct(List<File> files) {
        if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
            return "Please log in as a student.";
        }
        if (files == null || files.isEmpty()) {
            return "No files selected.";
        }
        Project project = database.getRegisteredProject(Session.getUserId());
        if (project == null) {
            return "No registered project to upload work for.";
        }
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        for (File file : files) {
            try {
                Path destPath = new File(uploadDir, file.getName()).toPath();
                Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                if (!database.addSubmission(Session.getUserId(), project.getId(), destPath.toString())) {
                    return "Failed to upload file: " + file.getName();
                }
                database.logNotification(Session.getUserId(), "Uploaded file: " + file.getName() + " for project ID: " + project.getId(), "general");
            } catch (Exception e) {
                return "Error uploading file: " + e.getMessage();
            }
        }
        return "Files uploaded successfully!";
    }

    private String updateWorkProduct(StudentSubmission submission, File file) {
        if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
            return "Please log in as a student.";
        }
        if (submission == null || file == null) {
            return "Invalid submission or file.";
        }
        File uploadDir = new File("Uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        try {
            Path destPath = new File(uploadDir, file.getName()).toPath();
            Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            if (database.updateSubmission(submission.getId(), destPath.toString())) {
                database.logNotification(Session.getUserId(), "Updated work product: " + file.getName() + " for project: " + submission.getProjectTitle(), "general");
                return "Work product updated successfully!";
            }
            return "Failed to update work product.";
        } catch (Exception e) {
            return "Error updating file: " + e.getMessage();
        }
    }
}