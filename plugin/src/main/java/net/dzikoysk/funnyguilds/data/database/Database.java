package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

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

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().getDataSource().getConnection();
    }

    public void shutdown() {
        this.dataSource.close();
    }
}
