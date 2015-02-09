package ch.flokus.suby.service;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DBSource {
    static ConnectionSource c = null;

    private DBSource() {
    }

    private static void connect() {
        String home = System.getProperty("user.home");
        home += "/.suby/";
        File subyhome = new File(home);
        if (!subyhome.exists()) {
            System.out.println("creating directory: " + subyhome);
            boolean result = false;
            try {
                subyhome.mkdir();
                result = true;
            } catch (SecurityException se) {
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

        String databaseUrl = "jdbc:sqlite:" + home + "suby.db";
        try {
            c = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {

        }
    }

    public static ConnectionSource getConnection() {
        if (c == null) {
            connect();
        }
        return c;
    }
}
