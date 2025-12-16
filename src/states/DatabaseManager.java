package states;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseManager {

    // creates a connection to the database
    private static final String URL = "jdbc:sqlite:res/monarchs-database.db";

    public static void main(String[] args) {
        createNewTables();            
        populateDefaultAchievements(); 
    }

    public static void populateDefaultAchievements() {
        String countSql = "SELECT COUNT(*) FROM achievements";
        
        // the SQL to insert data
        String insertSql = "INSERT INTO achievements(name, description) VALUES(?, ?)";

        // default data to insert
        String[][] defaults = {
            {"Secure an heir", "Secure an heir from a specific card."},
            {"Conqueror", "Gain the conquest bonus card."},
            {"First Monarch", "Complete your first reign."},
            {"Reign for 10 years.", "Reign for 10 years without dying."},
            {"Reign for 20 years.", "Reign for 20 years without dying."},
            {"Reign for 30 years.", "Reign for 30 years without dying."},
            {"Reign for 40 years.", "Reign for 40 years without dying."},
            {"Reign for 50 years.", "Reign for 50 years without dying."},
            {"Collector", "Unlock 4 bonus cards at once."},
            {"Visit from the Count", "Survive the visit from the count special event."}
        };

        try (Connection conn = DriverManager.getConnection(URL)) {
            
            // 1. check if table is already populated
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(countSql);
            rs.next();
            int rowCount = rs.getInt(1);
            
            if (rowCount > 0) {
                // data already exists, so we stop here to avoid duplicates
                System.out.println("Achievements already loaded. Skipping population.");
                return;
            }

            // 2. if empty, insert the data using a Batch (more efficient)
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                
                // loop through our array and add them to the batch
                for (String[] achievement : defaults) {
                    pstmt.setString(1, achievement[0]); // name
                    pstmt.setString(2, achievement[1]); // description
                    pstmt.addBatch();
                }
                
                // execute all inserts at once
                pstmt.executeBatch();
                System.out.println("Default achievements inserted successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Error populating achievements: " + e.getMessage());
        }
    }

    public static void createNewTables() {
        
        // 1. SQL for the 'progress' table
        String sqlProgress = "CREATE TABLE IF NOT EXISTS progress ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "monarchName TEXT NOT NULL, "
                + "reignLength INTEGER NOT NULL, "
                + "causeOfDeath TEXT NOT NULL, "
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" 
                + ");";

        // 2. SQL for the 'achievements' table
        // Note: SQLite uses INTEGER for booleans (0 = Locked, 1 = Unlocked)
        String sqlAchievements = "CREATE TABLE IF NOT EXISTS achievements ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "unlocked INTEGER NOT NULL DEFAULT 0, "
                + "timestamp DATETIME"
                + ");";

        // 3. execute the SQL
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // create 'progress' table
            stmt.execute(sqlProgress);
            
            // create 'achievements' table
            stmt.execute(sqlAchievements);
            
            System.out.println("Tables initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}