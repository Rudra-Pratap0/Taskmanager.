import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public TaskDAO() {
        // DBConnection me likha database creation trigger karenge
        DBConnection.initializeDatabase();
    }

    // 1. Add Task (userId ke sath save hoga)
    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, deadline, is_completed, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline());
            pstmt.setInt(4, task.isCompleted() ? 1 : 0);
            pstmt.setInt(5, task.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. View/Get All Tasks (Filtered by logged-in userId)
    public List<Task> getAllTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String deadline = rs.getString("deadline");
                    boolean isCompleted = rs.getInt("is_completed") == 1;
                    
                    Task task = new Task(id, title, description, deadline, isCompleted, userId);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // 3. Delete Task
    public boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Mark Task as Completed
    public boolean markTaskAsCompleted(int id) {
        String sql = "UPDATE tasks SET is_completed = 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
