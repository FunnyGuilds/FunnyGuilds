package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.annotation.Variable;

@Names(strategy = NameStrategy.IDENTITY)
public class MysqlConfiguration extends OkaeriConfig {

    @Variable("FG_MYSQL_HOSTNAME")
    public String hostname;
    @Variable("FG_MYSQL_PORT")
    public int port;
    @Variable("FG_MYSQL_DATABASE")
    public String database;
    @Variable("FG_MYSQL_USER")
    public String user;
    @Variable("FG_MYSQL_PASSWORD")
    public String password;

    @Variable("FG_MYSQL_POOL_SIZE")
    public int poolSize;
    @Variable("FG_MYSQL_CONNECTION_TIMEOUT")
    public int connectionTimeout;
    @Variable("FG_MYSQL_USE_SSL")
    public boolean useSSL;

    @Variable("FG_MYSQL_USERS_TABLE_NAME")
    public String usersTableName;
    @Variable("FG_MYSQL_GUILDS_TABLE_NAME")
    public String guildsTableName;
    @Variable("FG_MYSQL_REGIONS_TABLE_NAME")
    public String regionsTableName;

    public MysqlConfiguration() {
    }

    public MysqlConfiguration(String hostname, int port, String database, String user, String password, int poolSize, int connectionTimeout, boolean useSSL, String usersTableName, String guildsTableName, String regionsTableName) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.poolSize = poolSize;
        this.connectionTimeout = connectionTimeout;
        this.useSSL = useSSL;
        this.usersTableName = usersTableName;
        this.guildsTableName = guildsTableName;
        this.regionsTableName = regionsTableName;
    }

}
