import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Connection c;

    /**
     * Opens a connection to the postgresql database, raises an error when the connection doesn't exist.
     */
    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/ipass",
                            "postgres", "postgres");
            c.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     *@see <a href="https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories"> for more info</a>
     * @param mets The mets value of the activity
     * @param speedA The beginning of the speed range in miles per hour.
     * @param speedB The end of the speed range in miles per hour.
     * @param activity The name of the activity, only use activities which can be done while following a path.
     */
    public void insertMets(double mets, double speedA, double speedB, String activity) {
        try {
            String sql = "INSERT INTO metvalues (metvalue, speeda, speedb,  activity) VALUES (?, ?, ?, ?);";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setDouble(1, mets);
            pst.setDouble(2, speedA);
            pst.setDouble(3, speedB);
            pst.setString(4, activity);
            pst.executeUpdate();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Fetches all met values currently in the database.
     * @return A list of MetValue objects.
     */
    public List<MetValue> getBoxOptions() {
        List<MetValue> metValues = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM metvalues;");
            while (rs.next()) {
                metValues.add(new MetValue(rs.getDouble("metvalue"), rs.getDouble("speeda"),
                        rs.getDouble("speedb"), rs.getString("activity")));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return metValues;
    }

    /**
     * Fetches all the walkable types of way out of the database.
     * @return A list of WayType objects.
     */
    public List<WayType> getWayTypes() {
        List<WayType> wayTypes = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM waytype;");
            while (rs.next()) {
                wayTypes.add(new WayType(rs.getString("main_type"), rs.getString("sub_type")));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return wayTypes;
    }
}
