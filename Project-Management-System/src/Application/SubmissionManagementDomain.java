package Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Database.Database;

public class SubmissionManagementDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "getStudentSubmissions":
                return database.getSubmissions();
            case "uploadWorkProduct":
                return uploadWorkProduct((List<File>) args[0]);
            default:
                return "Invalid operation";
        }
    }

    private String uploadWorkProduct(List<File> files) {
        // Assume student and project details from session
        String studentName = "Student";
        String projectTitle = "AI Project"; // Replace with dynamic data
        for (File file : files) {
            if (!database.addSubmission(studentName, projectTitle, file.getAbsolutePath())) {
                return "Failed to upload files.";
            }
        }
        return "Files uploaded successfully!";
    }
}