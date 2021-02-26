package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;
import org.diorite.utils.collections.maps.CaseInsensitiveMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLNamedStatement {

    private final Map<String, Object> placeholders = new HashMap<>();
    private final CaseInsensitiveMap<Integer> keyMapIndex;
    private final String sql;

    public SQLNamedStatement(String sql, Map<String, Integer> keyMap) {
        this.sql = sql;
        this.keyMapIndex = new CaseInsensitiveMap<>(keyMap);
    }

    public void set(String key, Object value) {
        if (!keyMapIndex.containsKey(key)) {
            return;
        }

        placeholders.put(key, value);
    }

    public void executeUpdate() {
        try (Connection con = Database.getConnection()) {
            PreparedStatement statement = setPlaceholders(con.prepareStatement(sql));

            statement.executeUpdate();
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute update", sqlException);
        }
    }

    public void executeUpdate(boolean ignoreFails) {
        try (Connection con = Database.getConnection()) {
            PreparedStatement statement = setPlaceholders(con.prepareStatement(sql));

            statement.executeUpdate();
        }
        catch (SQLException sqlException) {
            if (ignoreFails) {
                FunnyGuilds.getInstance().getPluginLogger().debug("Could not execute update (ignoreFails)");
                return;
            }

            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute update", sqlException);
        }
    }

    public ResultSet executeQuery() {
        try (Connection con = Database.getConnection()) {
            PreparedStatement statement = setPlaceholders(con.prepareStatement(sql));

            return statement.executeQuery();
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute query", sqlException);
        }

        return null;
    }

    private PreparedStatement setPlaceholders(PreparedStatement preparedStatement) throws SQLException {
        for (Map.Entry<String, Object> placeholder : placeholders.entrySet()) {
            preparedStatement.setObject(keyMapIndex.get(placeholder.getKey()), placeholder.getValue());
        }

        return preparedStatement;
    }

}
