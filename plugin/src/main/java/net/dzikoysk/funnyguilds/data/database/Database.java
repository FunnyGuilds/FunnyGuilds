package net.dzikoysk.funnyguilds.data.database;

import com.zaxxer.hikari.HikariDataSource;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.MysqlConfiguration;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private final HikariDataSource dataSource;

    public Database() throws ClassNotFoundException {
        Class.forName(FunnyGuilds.getInstance().getPluginConfiguration().dataModel.getJDBCClassName());
        this.dataSource = new HikariDataSource();
        MysqlConfiguration c = FunnyGuilds.getInstance().getPluginConfiguration().mysql;

        int poolSize = c.poolSize;
        if (poolSize <= 0) {
            poolSize = Runtime.getRuntime().availableProcessors() * 2 + 1; // (core_count * 2) + spindle [pattern from PostgreSQL wiki]
        }

        String characterEncoding = c.characterEncoding == null || c.characterEncoding.isEmpty()
                ? ""
                : "&characterEncoding=" + c.characterEncoding;

        this.dataSource.setMaximumPoolSize(poolSize);
        this.dataSource.setConnectionTimeout(c.connectionTimeout);

        this.dataSource.setJdbcUrl("jdbc:" + FunnyGuilds.getInstance().getPluginConfiguration().dataModel.name().toLowerCase() + "://" + c.hostname + ":" + c.port + "/" + c.database + "?useSSL=" + c.useSSL + characterEncoding);
        this.dataSource.setUsername(c.user);

        if (!FunnyStringUtils.isEmpty(c.password)) {
            this.dataSource.setPassword(c.password);
        }

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void shutdown() {
        this.dataSource.close();
    }

}
