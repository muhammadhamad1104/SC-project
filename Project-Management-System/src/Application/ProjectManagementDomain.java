package Application;

import java.util.List;

import Database.Database;

public class ProjectManagementDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "searchProjects":
                return searchProjects((String) args[0]);
            case "getAllProjects":
                return database.getAllProjects();
            case "addProject":
                return addProject((String) args[0], (String) args[1], (String) args[2]);
            case "updateProject":
                return updateProject((Project) args[0], (String) args[1], (String) args[2], (String) args[3]);
            case "removeProject":
                return removeProject((Project) args[0]);
            default:
                return "Invalid operation";
        }
    }

    private List<Project> searchProjects(String query) {
        if (query == null || query.trim().isEmpty()) {
            return database.getAllProjects();
        }
        return database.searchProjects(query);
    }

    private String addProject(String title, String description, String supervisor) {
        if (title == null || title.trim().isEmpty()) {
            return "Project title cannot be empty.";
        }
        if (title.length() > 100) {
            return "Project title is too long (max 100 characters).";
        }
        if (description != null && description.length() > 1000) {
            return "Project description is too long (max 1000 characters).";
        }
        if (supervisor == null || supervisor.trim().isEmpty()) {
            return "Supervisor is required.";
        }
        if (database.addProject(title, description, supervisor)) {
            database.logNotification(Session.getUserId(), "Project", "Added project: " + title);
            return "Project added successfully!";
        }
        return "Failed to add project.";
    }

    private String updateProject(Project project, String title, String description, String supervisor) {
        if (project == null || title == null || title.trim().isEmpty()) {
            return "Invalid project or title.";
        }
        if (title.length() > 100) {
            return "Project title is too long (max 100 characters).";
        }
        if (description != null && description.length() > 1000) {
            return "Project description is too long (max 1000 characters).";
        }
        if (supervisor == null || supervisor.trim().isEmpty()) {
            return "Supervisor is required.";
        }
        if (database.updateProject(project.getId(), title, description, supervisor)) {
            database.logNotification(Session.getUserId(), "Project", "Updated project ID: " + project.getId());
            return "Project updated successfully!";
        }
        return "Failed to update project.";
    }

    private String removeProject(Project project) {
        if (project == null) {
            return "Invalid project.";
        }
        if (database.removeProject(project.getId())) {
            database.logNotification(Session.getUserId(), "Project", "Removed project ID: " + project.getId());
            return "Project removed successfully!";
        }
        return "Failed to remove project.";
    }
}