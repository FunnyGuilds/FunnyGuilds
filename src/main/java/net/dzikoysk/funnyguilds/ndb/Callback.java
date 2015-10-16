package net.dzikoysk.funnyguilds.ndb;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Callback {

    public void callback(ResultSet result) throws SQLException;
}
