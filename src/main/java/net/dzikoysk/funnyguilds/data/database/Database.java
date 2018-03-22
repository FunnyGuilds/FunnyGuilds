package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.FunnyLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;

public class Database {

    private static Database instance;

    private final HikariDataSource dataSource;

    public Database() {
        instance = this;

        this.dataSource = new HikariDataSource();
        PluginConfig.MySQL c = Settings.getConfig().mysql;

        int cores = Runtime.getRuntime().availableProcessors();
        this.dataSource.setMaximumPoolSize((cores * 2) + 1); // (core_count * 2) + spindle [pattern from PostgreSQL wiki]

        this.dataSource.setConnectionTimeout(c.connectionTimeout);

        this.dataSource.setJdbcUrl("jdbc:mysql://" + c.hostname + ":" + c.port + "/" + c.database);
        this.dataSource.setUsername(c.user);
        if (c.password != null && !c.password.isEmpty()) {
            this.dataSource.setPassword(c.password);
        }

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);
    }

    public static Database getInstance() {
        if (instance == null) {
            return new Database();
        }
        
        return instance;
    }

    public void executeQuery(String query, Consumer<ResultSet> action) {
        try (Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery()) {

            action.accept(result);
        } catch (Exception e) {
            if (FunnyLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
    }

    public int executeUpdate(String query) {
        try (Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            if (statement == null) {
                return 0;
            }

            return statement.executeUpdate();
        } catch (Exception e) {
            if (FunnyLogger.exception(e.getCause())) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
}
