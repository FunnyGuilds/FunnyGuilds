package net.dzikoysk.funnyguilds.ndb.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.ndb.Database;
import net.dzikoysk.funnyguilds.ndb.query.ReadQuery;
import net.dzikoysk.funnyguilds.ndb.query.WriteQuery;

public class SQLiteDatabase extends Database {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String NAME = "SQLite";

    private Connection connection;
    private final File file;

    public SQLiteDatabase(File file) {
        super("jdbc:sqlite:" + file.getPath(), null, null);

        this.file = file;
    }

    @Override
    public void closeConnection() throws SQLException {
        throw new UnsupportedOperationException("Cannot close SQLite");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
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
        if (this.connection != null)
            throw new RuntimeException("Cannot redefine the driver");

        try {
            Class.forName(this.getDriver());
            this.connection = DriverManager.getConnection(this.getConnectionURL());
        } catch (ClassNotFoundException ex) {
            FunnyGuilds.exception("Could not connect to the database file: " + ex.getLocalizedMessage(), ex.getStackTrace());
        }
    }

    @Override
    public void read(ReadQuery query) {
        PreparedStatement statement = null;
        try {
            statement = this.getConnection().prepareStatement(query.getQuery());
            ResultSet result = query.prepare(statement).executeQuery();
            query.getCallback().callback(result);

            if (result != null)
                result.close();
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

    public File getDatabaseFile() {
        return this.file;
    }
}
