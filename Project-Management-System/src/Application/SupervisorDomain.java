package Application;

import java.io.File;
import java.util.ArrayList;

public class SupervisorDomain {
    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "getAssignedProjects":
                return getAssignedProjects();
            case "getStudentSubmissions":
                return getStudentSubmissions();
            case "submitFeedback":
                return submitFeedback((StudentSubmission) args[0], (String) args[1]);
            default:
                return "Supervisor operation not supported!";
        }
    }

    private ArrayList<Project> getAssignedProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(new Project("AI Assistant", "Build an AI chatbot", "Dr. Smith"));
        projects.add(new Project("Web Crawler", "Develop a crawling bot", "Dr. Smith"));
        return projects;
    }

    private ArrayList<StudentSubmission> getStudentSubmissions() {
        ArrayList<StudentSubmission> submissions = new ArrayList<>();
        submissions.add(new StudentSubmission("John Doe", "AI Assistant", new File("workproduct1.pdf")));
        submissions.add(new StudentSubmission("Jane Smith", "Web Crawler", new File("workproduct2.pdf")));
        return submissions;
    }

    private String submitFeedback(StudentSubmission submission, String feedback) {
        // Simulate saving feedback (replace with SQLite later)
        submission.setFeedback(feedback);
        return "Feedback submitted successfully!";
    }
}