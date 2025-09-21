import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckTaskDatabase {

    private static final String DB_URL = "jdbc:mysql://36.50.135.62:3306/nro_1";
    private static final String DB_USER = "test";
    private static final String DB_PASS = "123456";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Connected to database successfully!");

            // 1. Check Task 32
            System.out.println("\n=== TASK 32 INFO ===");
            checkTaskMain(conn, 32);
            checkTaskSub(conn, 32);
            checkTaskRequirements(conn, 32, 0);

            // 2. Check Task 3
            System.out.println("\n=== TASK 3 INFO ===");
            checkTaskMain(conn, 3);
            checkTaskSub(conn, 3);
            checkTaskRequirements(conn, 3, 16);

            // 3. Check Berrus NPC (ID=55)
            System.out.println("\n=== BERRUS NPC (ID=55) REQUIREMENTS ===");
            checkNpcRequirements(conn, 55);

            conn.close();

        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }

    private static void checkTaskMain(Connection conn, int taskId) throws SQLException {
        String sql = "SELECT * FROM task_main_template WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, taskId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Task " + taskId + ": " + rs.getString("NAME") + " - " + rs.getString("detail"));
        } else {
            System.out.println("❌ Task " + taskId + " not found!");
        }
        rs.close();
        stmt.close();
    }

    private static void checkTaskSub(Connection conn, int taskMainId) throws SQLException {
        String sql = "SELECT * FROM task_sub_template WHERE task_main_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, taskMainId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("Sub tasks for Task " + taskMainId + ":");
        while (rs.next()) {
            System.out.println("  - Sub " + rs.getInt("id") + ": " + rs.getString("NAME") +
                    " (max_count: " + rs.getInt("max_count") + ", map: " + rs.getInt("map") + ")");
        }
        rs.close();
        stmt.close();
    }

    private static void checkTaskRequirements(Connection conn, int taskMainId, int taskSubId) throws SQLException {
        String sql = "SELECT * FROM task_requirements WHERE task_main_id = ? AND task_sub_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, taskMainId);
        stmt.setInt(2, taskSubId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("Requirements for Task " + taskMainId + "_" + taskSubId + ":");
        while (rs.next()) {
            System.out.println("  - Type: " + rs.getString("requirement_type") +
                    ", Target: " + rs.getInt("target_id") +
                    ", Count: " + rs.getInt("target_count") +
                    ", Map: " + rs.getString("map_restriction"));
        }
        rs.close();
        stmt.close();
    }

    private static void checkNpcRequirements(Connection conn, int npcId) throws SQLException {
        String sql = "SELECT * FROM task_requirements WHERE requirement_type = 'TALK_NPC' AND target_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, npcId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("All TALK_NPC requirements for NPC " + npcId + ":");
        while (rs.next()) {
            System.out.println("  - Task " + rs.getInt("task_main_id") + "_" + rs.getInt("task_sub_id") +
                    ", Map: " + rs.getString("map_restriction"));
        }
        rs.close();
        stmt.close();
    }
}
