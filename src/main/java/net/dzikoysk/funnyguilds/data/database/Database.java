package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    /*
     * https://github.com/brettwooldridge/HikariCP#popular-datasource-class-names
     */

    private static Database instance;

    private final HikariDataSource dataSource;

    public Database() {
        instance = this;

        this.dataSource = new HikariDataSource();
        PluginConfig.MySQL c = Settings.getConfig().mysql;

        //this.dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        this.dataSource.setMaximumPoolSize(c.poolSize);

        this.dataSource.setJdbcUrl("jdbc:mysql://" + c.hostname + ":" + c.port + "/" + c.database);
        this.dataSource.setUsername(c.user);
        if (c.password != null && !c.password.isEmpty()) {
            this.dataSource.setPassword(c.password);
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

    public static Database getInstance() {
        if (instance == null) {
            return new Database();
        }
        return instance;
    }

    public ResultSet executeQuery(String query) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public int executeUpdate(String query) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            if (statement == null) {
                return 0;
            }
            int result = statement.executeUpdate();
            statement.close();
            connection.close();
            return result;
        } catch (Exception e) {
            if (FunnyGuilds.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
