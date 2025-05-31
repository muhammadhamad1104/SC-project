package Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    // User Operations
    public String processLogin(String email, String password, String role) {
        Object domainInput = new UserAuthenticationDomain().process(email, password, role, "login");
        return (String) domainInput;
    }

    public String processSignUp(String username, String password, String email, String role) {
        Object domainInput = new UserAuthenticationDomain().process(username, password, email, role, "signup");
        return (String) domainInput;
    }

    public String updateProfile(String username, String email) {
        Object domainInput = new UserProfileDomain().process(username, email, "updateProfile");
        return (String) domainInput;
    }

    // Project Operations (Admin)
    public List<Project> processSearchProjects(String query) {
        Object domainInput = new ProjectManagementDomain().process(query, "searchProjects");
        return (List<Project>) domainInput;
    }

    public List<Project> getAllProjects() {
        Object domainInput = new ProjectManagementDomain().process("getAllProjects");
        return (List<Project>) domainInput;
    }

    public String processAddProject(String title, String description, String supervisor) {
        Object domainInput = new ProjectManagementDomain().process(title, description, supervisor, "addProject");
        return (String) domainInput;
    }

    public String processUpdateProject(Project project, String title, String description, String supervisor) {
        Object domainInput = new ProjectManagementDomain().process(project, title, description, supervisor, "updateProject");
        return (String) domainInput;
    }

    public String processRemoveProject(Project project) {
        Object domainInput = new ProjectManagementDomain().process(project, "removeProject");
        return (String) domainInput;
    }

    // Project Assignment
    public String assignProjectToSupervisor(Project project, String supervisor) {
        Object domainInput = new ProjectAssignmentDomain().process(project, supervisor, "assignProject");
        return (String) domainInput;
    }

    // Supervisor Operations
    public ArrayList<Project> getAssignedProjects() {
        Object domainInput = new SupervisorProjectDomain().process("getAssignedProjects");
        return (ArrayList<Project>) domainInput;
    }

    public ArrayList<StudentSubmission> getStudentSubmissions() {
        Object domainInput = new SubmissionManagementDomain().process("getStudentSubmissions");
        return (ArrayList<StudentSubmission>) domainInput;
    }

    public String submitFeedback(StudentSubmission submission, String feedback) {
        Object domainInput = new FeedbackDomain().process(submission, feedback, "submitFeedback");
        return (String) domainInput;
    }

    // Student Operations
    public ArrayList<Project> getAvailableProjects() {
        Object domainInput = new ProjectAssignmentDomain().process("getAvailableProjects");
        return (ArrayList<Project>) domainInput;
    }

    public String registerProject(Project project) {
        Object domainInput = new StudentRegistrationDomain().process(project, "registerProject");
        return (String) domainInput;
    }

    public Project getRegisteredProject() {
        Object domainInput = new StudentProjectDomain().process("getRegisteredProject");
        return (Project) domainInput;
    }

    public String uploadWorkProduct(List<File> files) {
        Object domainInput = new SubmissionManagementDomain().process(files, "uploadWorkProduct");
        return (String) domainInput;
    }

    public String getFeedback() {
        Object domainInput = new FeedbackDomain().process("getFeedback");
        return (String) domainInput;
    }

    // Notification Operations
    public String logNotification(String message) {
        Object domainInput = new NotificationDomain().process(message, "logNotification");
        return (String) domainInput;
    }
}