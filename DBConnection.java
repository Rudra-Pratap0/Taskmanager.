import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    // Database file path. SQLite locally program directory mein create ho jayega.
    private static final String URL = "jdbc:sqlite:tasks.db";

    public static Connection getConnection() throws SQLException {
        try {
            // Driver load karenge
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        }
        // Connection build karenge aur return karenge
        return DriverManager.getConnection(URL);
    }

    // Database tables ko initialize/create aur migrate karne ke liye method
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      "username TEXT UNIQUE NOT NULL," +
                                      "password TEXT NOT NULL" +
                                      ");";
            stmt.execute(createUsersTable);
            System.out.println("Users table checked/created.");

            // 2. Create tasks table (with user_id column and foreign key reference)
            String createTasksTable = "CREATE TABLE IF NOT EXISTS tasks (" +
                                      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      "title TEXT NOT NULL," +
                                      "description TEXT," +
                                      "deadline TEXT," +
                                      "is_completed INTEGER DEFAULT 0," +
                                      "user_id INTEGER," +
                                      "FOREIGN KEY(user_id) REFERENCES users(id)" +
                                      ");";
            stmt.execute(createTasksTable);
            System.out.println("Tasks table checked/created.");

            // 3. Schema Migration: Agar tasks table pehle se thi par user_id nahi tha, toh user_id column add karenge
            try {
                stmt.execute("ALTER TABLE tasks ADD COLUMN user_id INTEGER;");
                System.out.println("Migration: Added user_id column to tasks table.");
            } catch (SQLException e) {
                // Agar user_id column pehle se exist karta hai, toh SQLite duplicate column error dega.
                // Hum use catch karke ignore kar denge kyuki iska matlab migration pehle hi ho chuka hai.
                System.out.println("Migration check: user_id column already exists in tasks table.");
            }

        } catch (SQLException e) {
            System.out.println("Database initialization/migration failed!");
            e.printStackTrace();
        }
    }
}
