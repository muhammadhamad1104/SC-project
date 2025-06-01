package Application;

import Database.Database;

import java.util.ArrayList;
import java.util.List;

public class NotificationDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "logNotification":
                return logNotification((String) args[0], (String) args[1]);
            case "getNotifications":
                return getNotifications();
            default:
                return "Invalid operation";
        }
    }

    private String logNotification(String message, String status) {
        if (message == null || message.trim().isEmpty()) {
            return "Notification message cannot be empty.";
        }
        database.logNotification(Session.getUserId(), message, status);
        return "Notification logged.";
    }

    private List<Database.Notification> getNotifications() {
        if (!Session.isLoggedIn()) {
            return new ArrayList<>();
        }
        return database.getNotifications(Session.getUserId());
    }
}