package Application;

import Database.Database;

public class UserAuthenticationDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "login":
                return authenticateUser((String) args[0], (String) args[1], (String) args[2]);
            case "signup":
                return createUser((String) args[0], (String) args[1], (String) args[2], (String) args[3]);
            default:
                return "Invalid operation";
        }
    }

    private String authenticateUser(String email, String password, String role) {
        if (database.authenticateUser(email, password)) {
            return "Login successful!";
        }
        return "Invalid credentials.";
    }

    private String createUser(String username, String email, String password, String role) {
        if (database.createUser(username, email, password, role)) {
            return "Account created successfully!";
        }
        return "Failed to create account.";
    }
}