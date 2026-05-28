public class Task {
    private int id;
    private String title;
    private String description;
    private String deadline; // Formatted as YYYY-MM-DD
    private boolean isCompleted;
    private int userId; // Task kis user ka hai, uski ID

    // Constructor: Jab naya task banana ho (Database me save karne se pehle, jisme ID nahi hoti)
    public Task(String title, String description, String deadline, int userId) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isCompleted = false; // Default: task pending rahega
        this.userId = userId;
    }

    // Constructor: Jab database se data fetch karenge (jisme ID, status aur user_id sab hote hain)
    public Task(int id, String title, String description, String deadline, boolean isCompleted, int userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
