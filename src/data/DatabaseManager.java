package data;

import data.MetValue;
import data.WayType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class talks to the database and returns the data if required.
 */
public class DatabaseManager {
    private Connection c;

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
     * Inserts a met value into the database.
     *
     * @param met     The met value of the activity
     * @param speedA   The beginning of the speed range in miles per hour.
     * @param speedB   The end of the speed range in miles per hour.
     * @param activity The name of the activity, only use activities which can be done while following a path.
     * @see <a href="https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories"> for more info</a>
     */
    public void insertMet(double met, double speedA, double speedB, String activity) {
        try {
            String sql = "INSERT INTO metvalues (metvalue, speeda, speedb,  activity) VALUES (?, ?, ?, ?);";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setDouble(1, met);
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
     * Fetches all met values currently from the metvalues table.
     *
     * @return A list of data.MetValue objects.
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
     * Fetches all the walkable types of way from the waytype table.
     *
     * @return A list of data.WayType objects.
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
