package Application;

public class Project {
    int id;
    private String title;
    private String description;
    private String supervisor;

    public Project(String title, String description, String supervisor) {
        this.title = title;
        this.description = description;
        this.supervisor = supervisor;
    }
    public Project(String title, String description, String supervisor, int id) {
    this.title = title;
    this.description = description;
    this.supervisor = supervisor;
    this.id = id;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSupervisor() { return supervisor; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public int getId() {
        return id;
    }
}