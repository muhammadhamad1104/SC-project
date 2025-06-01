package Application;

import java.util.ArrayList;

import Database.Database;

public class SupervisorProjectDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("getAssignedProjects")) {
            if (!Session.isLoggedIn() || !Session.getRole().equals("supervisor")) {
                return new ArrayList<Project>();
            }
            return new ArrayList<>(database.getAssignedProjects(Session.getUserId()));
        }
        return "Invalid operation";
    }
}