package Application;

import Database.Database;

public class UserProfileDomain {
    private Database database = new Database();
    private static final String EMAIL_REGEX = "^[^@]+@[^@]+\\.[^@]+$";
    private static final String[] SPAM_DOMAINS = {"spam.com", "junkmail.com", "fake.com"};

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("updateProfile")) {
            return updateProfile((String) args[0], (String) args[1]);
        }
        return "Invalid operation";
    }

    private String updateProfile(String username, String email) {
        if (!Session.isLoggedIn()) {
            return "Please log in to update profile.";
        }
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (username.length() > 50) {
            return "Username is too long (max 50 characters).";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        if (!email.matches(EMAIL_REGEX)) {
            return "Invalid email format.";
        }
        for (String spamDomain : SPAM_DOMAINS) {
            if (email.toLowerCase().endsWith("@" + spamDomain)) {
                return "Email domain is not allowed.";
            }
        }
        if (database.updateUserProfile(Session.getUserId(), username, email)) {
            Session.setSession(Session.getUserId(), Session.getRole(), username);
            database.logNotification(Session.getUserId(), "Profile updated for", username);
            return "Profile updated successfully!";
        }
        return "Failed to update profile. Username or email may already exist.";
    }
}