package Application;

import Database.Database;

public class UserProfileDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        if (operation.equals("updateProfile")) {
            return updateProfile((String) args[0], (String) args[1]);
        }
        return "Invalid operation";
    }

    private String updateProfile(String username, String email) {
        if (database.updateUserProfile(username, email)) {
            return "Profile updated successfully!";
        }
        return "Failed to update profile.";
    }
}