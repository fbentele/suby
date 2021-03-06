package ch.flokus.suby.service;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DBSource {
    static ConnectionSource c = null;

    private DBSource() {

    }

    private static void connect() throws ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
        }
        String home = System.getProperty("user.home");
        home += "/.suby/";
        File subyhome = new File(home);
        if (!subyhome.exists()) {
            System.out.println("creating directory: " + subyhome);
            try {
                if (subyhome.mkdir()) {
                    System.out.println("Directory for sqlite-database created");
                }
            } catch (SecurityException se) {
                System.out.println("Could not create directory for database: " + home);
            }
        }

        String databaseUrl = "jdbc:sqlite:" + home + "suby.db";
        try {
            c = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {
            System.out.println("Could not establish a database-connection");
        }
    }

    public static ConnectionSource getConnection() {
        if (c == null) {
            try {
                connect();
            } catch (ClassNotFoundException e) {
            }
        }
        return c;
    }
}
