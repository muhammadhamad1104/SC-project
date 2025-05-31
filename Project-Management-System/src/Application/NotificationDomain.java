package Application;

import Database.Database;

public class NotificationDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("logNotification")) {
            database.logNotification((String) args[0]);
            return "Notification logged.";
        }
        return "Invalid operation";
    }
}