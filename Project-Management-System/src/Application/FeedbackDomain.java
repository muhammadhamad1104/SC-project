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
        // Assume submission has an ID
        if (database.updateFeedback(submission.getId(), feedback)) {
            return "Feedback submitted successfully!";
        }
        return "Failed to submit feedback.";
    }

    private String getFeedback() {
        // Assume submission ID from student session
        int submissionId = 1; // Replace with dynamic ID
        return database.getFeedback(submissionId);
    }
}