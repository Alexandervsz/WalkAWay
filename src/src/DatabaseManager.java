import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Connection c;

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

    public void insertMets(double mets, double speedA, double speedB, String activity) {
        try {
            String sql = "INSERT INTO metvalues (metvalue, speeda, speedb,  activity) VALUES (?, ?, ?, ?);";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setString(1, String.valueOf(mets));
            pst.setString(2, String.valueOf(speedA));
            pst.setString(3, String.valueOf(speedB));
            pst.setString(4, activity);
            pst.executeUpdate();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public List<MetValue> getBoxOptions() {
        List<MetValue> metValues = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM metvalues;");
            while (rs.next()) {
                metValues.add(new MetValue(rs.getString("metvalue"), rs.getString("speeda"),
                        rs.getString("speedb"), rs.getString("activity")));
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

    public List<NodeType> getNodeTypes() {
        List<NodeType> nodeTypes = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM nodetype;");
            while (rs.next()) {
                nodeTypes.add(new NodeType(rs.getString("main_type"), rs.getString("sub_type")));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return nodeTypes;
    }
}
