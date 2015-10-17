package net.dzikoysk.funnyguilds.ndb;

import java.sql.Connection;
import java.sql.SQLException;
import net.dzikoysk.funnyguilds.ndb.query.ReadQuery;
import net.dzikoysk.funnyguilds.ndb.query.WriteQuery;

public interface IDatabase {

    public void closeConnection() throws SQLException;

    public Connection getConnection() throws SQLException;

    public String getDriver();

    public String getName();

    public void openConnection() throws SQLException;

    public void read(ReadQuery query);

    public void write(WriteQuery query);
}
