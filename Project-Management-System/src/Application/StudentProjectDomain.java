package Application;

import Database.Database;

public class StudentProjectDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("getRegisteredProject")) {
            int studentId = 1; // Assume from session
            return database.getRegisteredProject(studentId);
        }
        return "Invalid operation";
    }
}