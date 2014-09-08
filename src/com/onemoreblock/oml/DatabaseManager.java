package com.onemoreblock.oml;

import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {
    static Statement db;

    public boolean loadDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = null;
            connection = DriverManager.getConnection("jdbc:sqlite:levels.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            db = statement;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean closeDB() {
        try {
            db.getConnection().close();
        } catch (SQLException e) {
            Vix.getLog().info("[Vix]Saving failed!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveLevel(Level level) {
        try {
            db.executeUpdate("insert into levels values('" + level.name
                    + "', '" + level.designer + "', 0)");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteLevel(String name) {
        try {
            db.executeUpdate("delete from levels where name = '" + name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean publishLevel(String name) {
        try {
            db.executeUpdate("UPDATE levels SET edit = 1 WHERE name = '" + name
                    + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getDesigner(String name) {
        String designer = null;
        try {
            ResultSet rs = db
                    .executeQuery("select designer from levels where name = '"
                            + name + "'");
            designer = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return designer;
    }

    public int getNumofDrafts(Player player) {
        // TODO
        return 0;
    }

    public boolean levelExists(String name) {
        ResultSet rs;

        try {
            rs = db.executeQuery("select name from levels where name = '"
                    + name + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDesigner(String name, String designer) {
        // TODO co-op
        ResultSet rs;
        try {
            rs = db.executeQuery("select designer from levels where name = '"
                    + name + "'");
            while (rs.next()) {
                String des = rs.getString("designer");
                if (des.equals(designer)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isLevelPublished(String name) {
        ResultSet rs;
        if (name == "world") {
            return false;
        }
        try {
            rs = db.executeQuery("select published from levels where name = '"
                    + name + "'");
            while (rs.next()) {
                Boolean e = rs.getBoolean("published");
                if (e) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Statement getDB() {
        return db;
    }
}
