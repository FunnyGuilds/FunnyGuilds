package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;

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
        PluginConfiguration.MySQL c = FunnyGuilds.getInstance().getPluginConfiguration().mysql;

        int poolSize = c.poolSize;
        if (poolSize <= 0) {
            poolSize = Runtime.getRuntime().availableProcessors() * 2 + 1; // (core_count * 2) + spindle [pattern from PostgreSQL wiki]
        }

        this.dataSource.setMaximumPoolSize(poolSize);
        this.dataSource.setConnectionTimeout(c.connectionTimeout);

        this.dataSource.setJdbcUrl("jdbc:mysql://" + c.hostname + ":" + c.port + "/" + c.database + "?useSSL=" + c.useSSL);
        this.dataSource.setUsername(c.user);
        if (c.password != null && ! c.password.isEmpty()) {
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
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute query", ex);
        }
    }

    public int executeUpdate(String query) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (statement == null) {
                return 0;
            }

            return statement.executeUpdate();
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute update", ex);
        }
        return 0;
    }

    public boolean columnExist(String table, String column) {
        StringBuilder query = new StringBuilder();

        query.append("show columns from `");
        query.append(table);
        query.append("` where field = \"");
        query.append(column);
        query.append("\"");

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {
            if (statement == null) {
                return false;
            }

            return statement.executeQuery().next();
        }
        catch (Exception ex) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not check column", ex);
            return false;
        }
    }

    public void shutdown() {
        this.dataSource.close();
    }
}
