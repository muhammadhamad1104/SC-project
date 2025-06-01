package Application;

public class Session {
    private static int userId = -1;
    private static String role = "";
    private static String username = "";

    public static void setSession(int id, String role, String username) {
        userId = id;
        Session.role = role;
        Session.username = username;
    }

    public static void clearSession() {
        userId = -1;
        role = "";
        username = "";
    }

    public static int getUserId() {
        return userId;
    }

    public static String getRole() {
        return role;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isLoggedIn() {
        return userId != -1;
    }
}