package Application;

import java.io.File;

public class StudentSubmission {
    private int id;
    private String studentName;
    private String projectTitle;
    private File workProduct;
    private String feedback;

    public StudentSubmission(String studentName, String projectTitle, File workProduct) {
        this.studentName = studentName;
        this.projectTitle = projectTitle;
        this.workProduct = workProduct;
        this.feedback = "";
    }

    public String getStudentName() { return studentName; }
    public String getProjectTitle() { return projectTitle; }
    public File getWorkProduct() { return workProduct; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}