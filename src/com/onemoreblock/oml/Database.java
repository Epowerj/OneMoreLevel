package com.onemoreblock.oml;

import java.sql.*;

/**
 * Created by epowerj on 9/9/14.
 */
public class Database {
    String ip = "85.10.205.173";
    String user = "epowerj";
    String password = "health20";

    Connection connection = null;
    Statement db = null;

    void load() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/", user, password);
            db = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }

    void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String name, String designer) {
        try {
            db.executeUpdate("insert into levels values('" + name + "', '" + designer + "', 0)");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void remove(String name) {
        try {
            db.executeUpdate("delete from levels where name = '" + name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean levelExists(String name) {
        ResultSet rs;

        try {
            rs = db.executeQuery("select name from levels where name = '" + name + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDesigner(String name, String creator) {
        ResultSet rs;

        try {
            rs = db.executeQuery("select creator from levels where name = '" + name + "'");
            while (rs.next()) {
                String des = rs.getString("creator");
                if (des.equals(creator)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
