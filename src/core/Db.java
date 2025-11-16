package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Db {

    static {
        System.out.println("Loaded Db from: " + Db.class.getResource("Db.class"));
    }

    public Db() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private static final String URL =
        "jdbc:mysql://ec2-3-110-124-58.ap-south-1.compute.amazonaws.com:3306/skilo_dev"
        + "?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "superadmin";
    private static final String PASS = "Arwa$1234";

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public ResultSet runQuery(String sql) throws Exception {
        Connection conn = getConnection();
        Statement st = conn.createStatement();
        return st.executeQuery(sql);
    }

    public int runUpdate(String sql) throws Exception {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            return st.executeUpdate(sql);
        }
    }
}
