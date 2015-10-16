package net.dzikoysk.funnyguilds.ndb.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.ndb.Database;
import net.dzikoysk.funnyguilds.ndb.query.ReadQuery;
import net.dzikoysk.funnyguilds.ndb.query.WriteQuery;
import org.apache.commons.dbcp.BasicDataSource;

public class MySQLDatabase extends Database {

    public static final int DEFAULT_PORT = 3306;
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String NAME = "MySQL";

    private final String host, database;
    private final int port;
    private BasicDataSource dataSource;

    public MySQLDatabase(String host, String database, String username, String password) {
        this(host, DEFAULT_PORT, database, username, password);
    }

    public MySQLDatabase(String host, int port, String database, String username, String password) {
        super("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public void closeConnection() throws SQLException {
        this.dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public String getDriver() {
        return DRIVER;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void openConnection() throws SQLException {
        if (this.dataSource != null) {
            throw new RuntimeException("Cannot reopen the connection pool");
        }

        this.dataSource = new BasicDataSource();
        this.buildProperties();
    }

    @Override
    public void read(ReadQuery query) {
        PreparedStatement statement = null;
        try {
            statement = this.getConnection().prepareStatement(query.getQuery());
            ResultSet result = query.prepare(statement).executeQuery();
            query.getCallback().callback(result);

            if (result != null) {
                result.close();
            }
        } catch (SQLException ex) {
            FunnyGuilds.exception("Could not execute the SQL query: " + ex.getLocalizedMessage(), ex.getStackTrace());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    FunnyGuilds.exception("Could not close the SQL statement: " + ex.getLocalizedMessage(), ex.getStackTrace());
                }
            }
        }
    }

    @Override
    public void write(WriteQuery query) {
        PreparedStatement statement = null;
        try {
            statement = this.getConnection().prepareStatement(query.getQuery());
            query.prepare(statement).executeUpdate();
        } catch (SQLException ex) {
            FunnyGuilds.exception("Could not execute the SQL query: " + ex.getLocalizedMessage(), ex.getStackTrace());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    FunnyGuilds.exception("Could not close the SQL statement: " + ex.getLocalizedMessage(), ex.getStackTrace());
                }
            }
        }
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public BasicDataSource getDataSource() {
        return this.dataSource;
    }

    private void buildProperties() {
        this.dataSource.setDriverClassName(this.getDriver());
        this.dataSource.setUrl(this.getConnectionURL());

        if (this.getUsername() != null) {
            this.dataSource.setUsername(this.getUsername());
        }
        if (this.getPassword() != null) {
            this.dataSource.setPassword(this.getPassword());
        }
    }

}
