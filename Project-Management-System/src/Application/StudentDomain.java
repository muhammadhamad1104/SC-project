package Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentDomain {
    private Project registeredProject = null;
    private List<StudentSubmission> submissions = new ArrayList<>();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "getAvailableProjects":
                return getAvailableProjects();
            case "registerProject":
                return registerProject((Project) args[0]);
            case "getRegisteredProject":
                return getRegisteredProject();
            case "uploadWorkProduct":
                return uploadWorkProduct((List<File>) args[0]);
            case "getFeedback":
                return getFeedback();
            default:
                return "Student operation not supported!";
        }
    }

    private ArrayList<Project> getAvailableProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(new Project("AI Assistant", "Build an AI chatbot", "Dr. Smith"));
        projects.add(new Project("Web Crawler", "Develop a crawling bot", "Prof. Alan"));
        projects.add(new Project("Smart Home", "IoT-based automation", "Dr. Jane"));
        return projects;
    }

    private String registerProject(Project project) {
        if (registeredProject != null) {
            return "You have already registered a project.";
        }
        registeredProject = project;
        return "Successfully registered for: " + project.getTitle();
    }

    private Project getRegisteredProject() {
        return registeredProject;
    }

    private String uploadWorkProduct(List<File> files) {
        if (registeredProject == null) {
            return "No registered project to upload work for.";
        }
        for (File file : files) {
            submissions.add(new StudentSubmission("Student", registeredProject.getTitle(), file));
        }
        return "Files uploaded successfully.";
    }

    private String getFeedback() {
        if (registeredProject == null || submissions.isEmpty()) {
            return "No feedback has been provided yet.";
        }
        // Fetch feedback from latest submission
        StudentSubmission submission = submissions.get(submissions.size() - 1);
        String feedback = submission.getFeedback();
        return feedback.isEmpty() ? "No feedback has been provided yet." :
                "Supervisor's Feedback:\n\n" + feedback;
    }
}