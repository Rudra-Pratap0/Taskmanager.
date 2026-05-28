import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // 1. User Register (Signup)
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Note: production me hashing use karni chahiye, par simplicity ke liye plain text store kar rahe hain
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Agar username duplicate hoga (UNIQUE constraint fail), toh SQL exception aayegi.
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    // 2. User Authentication (Login)
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    return new User(id, dbUsername, dbPassword);
                }
            }
        } catch (SQLException e) {
            System.out.println("Login database query failed!");
            e.printStackTrace();
        }
        return null; // Username/Password mismatch ya exception
    }
}
