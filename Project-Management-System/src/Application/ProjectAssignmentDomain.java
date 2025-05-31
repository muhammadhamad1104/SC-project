package Application;

import java.util.List;

import Database.Database;

public class ProjectAssignmentDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "assignProject":
                return assignProject((Project) args[0], (String) args[1]);
            case "getAvailableProjects":
                return database.getAvailableProjects();
            default:
                return "Invalid operation";
        }
    }

    private String assignProject(Project project, String supervisor) {
        if (database.updateProject(project.getId(), project.getTitle(), project.getDescription(), supervisor)) {
            return "Project assigned successfully!";
        }
        return "Failed to assign project.";
    }
}