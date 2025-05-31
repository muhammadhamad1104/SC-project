package Application;

import java.util.List;

import Database.Database;

public class ProjectManagementDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "searchProjects":
                return database.searchProjects((String) args[0]);
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

    private String addProject(String title, String description, String supervisor) {
        if (database.addProject(title, description, supervisor)) {
            return "Project added successfully!";
        }
        return "Failed to add project.";
    }

    private String updateProject(Project project, String title, String description, String supervisor) {
        if (database.updateProject(project.getId(), title, description, supervisor)) {
            return "Project updated successfully!";
        }
        return "Failed to update project.";
    }

    private String removeProject(Project project) {
        if (database.removeProject(project.getId())) {
            return "Project removed successfully!";
        }
        return "Failed to remove project.";
    }
}