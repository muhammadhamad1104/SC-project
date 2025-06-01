package Application;

import Database.Database;

public class StudentProjectDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("getRegisteredProject")) {
            if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
                return null;
            }
            return database.getRegisteredProject(Session.getUserId());
        }
        return "Invalid operation";
    }
}