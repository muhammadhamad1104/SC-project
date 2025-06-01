package Application;

import Database.Database;

public class FeedbackDomain {
    private Database database = new Database();

    public Object process(Object... args) {
        String operation = (String) args[args.length - 1];
        switch (operation) {
            case "submitFeedback":
                return submitFeedback((StudentSubmission) args[0], (String) args[1]);
            case "getFeedback":
                return getFeedback();
            default:
                return "Invalid operation";
        }
    }

    private String submitFeedback(StudentSubmission submission, String feedback) {
        if (feedback == null || feedback.trim().isEmpty()) {
            return "Feedback cannot be empty.";
        }
        if (feedback.length() > 1000) {
            return "Feedback is too long (max 1000 characters).";
        }
        if (database.updateFeedback(submission.getId(), feedback)) {
            database.logNotification(Session.getUserId(), "Feedback submitted for submission ID:", String.valueOf(submission.getId()));
            return "Feedback submitted successfully!";
        }
        return "Failed to submit feedback.";
    }

    private String getFeedback() {
        if (!Session.isLoggedIn() || !Session.getRole().equals("student")) {
            return "Please log in as a student.";
        }
        Project project = database.getRegisteredProject(Session.getUserId());
        if (project == null) {
            return "No registered project found.";
        }
        return database.getFeedback(Session.getUserId(), project.getId());
    }
}