package Application;

import java.util.ArrayList;

import Database.Database;

public class SupervisorProjectDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("getAssignedProjects")) {
            // Assume supervisor email is available (e.g., from session)
            return database.getAssignedProjects("Dr. Smith");
        }
        return "Invalid operation";
    }
}