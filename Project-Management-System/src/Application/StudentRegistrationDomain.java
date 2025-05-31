package Application;

import Database.Database;

public class StudentRegistrationDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("registerProject")) {
            return registerProject((Project) args[0]);
        }
        return "Invalid operation";
    }

    private String registerProject(Project project) {
        int studentId = 1; // Assume from session
        if (database.registerProject(studentId, project.getId())) {
            return "Successfully registered for: " + project.getTitle();
        }
        return "Failed to register project.";
    }
}