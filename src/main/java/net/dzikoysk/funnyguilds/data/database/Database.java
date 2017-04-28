package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;

import java.sql.*;

public class Database {

    private static Database instance;

    private final HikariDataSource dataSource;

    public Database() {
        instance = this;

        this.dataSource = new HikariDataSource();
        Settings c = Settings.getInstance();

        this.dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.dataSource.setMaximumPoolSize(c.poolSize);

        this.dataSource.setJdbcUrl("jdbc:mysql://" + c.mysqlHostname + ":" + c.mysqlPort + "/" + c.mysqlDatabase);
        this.dataSource.setUsername(c.mysqlUser);
        if (c.mysqlPassword != null && !c.mysqlPassword.isEmpty()) {
            this.dataSource.setPassword(c.mysqlPassword);
        }

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    }

    /*public Connection openConnection() {
        try {
            if (checkConnection()) {
                return connection;
            }
            Class.forName("com.mysql.jdbc.Driver");
            String s = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
            connection = DriverManager.getConnection(s, this.user, this.password);
            return connection;
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void firstConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/?user=" + this.user + "&password=" + this.password;
            Connection conn = DriverManager.getConnection(url);
            Statement s = conn.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + this.database + "`");
            conn.close();
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return connection != null;
    }

    public boolean closeConnection() {
        if (connection == null) {
            return false;
        }
        try {
            connection.close();
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return true;
    }*/

    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement statement = this.dataSource.getConnection().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            return result;
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public int executeUpdate(String query) {
        try {
            PreparedStatement statement = this.dataSource.getConnection().prepareStatement(query);
            if (statement == null) {
                return 0;
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("42S21")) {
                return 4221;
            }
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static Database getInstance() {
        if (instance == null) {
            return new Database();
        }
        return instance;
    }
}
