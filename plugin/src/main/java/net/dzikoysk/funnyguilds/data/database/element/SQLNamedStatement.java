package net.dzikoysk.funnyguilds.data.database.element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;
import panda.std.function.ThrowingConsumer;

public class SQLNamedStatement {

    private final Map<String, Object> placeholders = new HashMap<>();
    private final Map<String, Integer> keyMapIndex;
    private final String sql;

    public SQLNamedStatement(String sql, Map<String, Integer> keyMap) {
        this.sql = sql;
        this.keyMapIndex = new HashMap<>(keyMap);
    }

    public void set(String key, Object value) {
        key = key.toLowerCase(Locale.ROOT);
        if (!keyMapIndex.containsKey(key)) {
            return;
        }

        placeholders.put(key, value);
    }

    public void executeUpdate() {
        try (Connection con = Database.getConnection()) {
            try (PreparedStatement statement = setPlaceholders(con.prepareStatement(sql))) {
                statement.executeUpdate();
            }
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getPluginLogger().error("Could not execute update", sqlException);
        }
    }

    public void executeUpdate(boolean ignoreFails) {
        try (Connection con = Database.getConnection()) {
            try (PreparedStatement statement = setPlaceholders(con.prepareStatement(sql))) {
                statement.executeUpdate();
            }
        }
        catch (SQLException sqlException) {
            if (ignoreFails) {
                FunnyGuilds.getPluginLogger().debug("Could not execute update (ignoreFails)");
                return;
            }

            FunnyGuilds.getPluginLogger().error("Could not execute update", sqlException);
        }
    }

    public void executeQuery(ThrowingConsumer<ResultSet, SQLException> consumer) {
        try (Connection con = Database.getConnection()) {
            try (PreparedStatement statement = setPlaceholders(con.prepareStatement(sql))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    consumer.accept(resultSet);
                }
            }
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getPluginLogger().error("Could not execute query", sqlException);
        }
    }

    private PreparedStatement setPlaceholders(PreparedStatement preparedStatement) throws SQLException {
        for (Map.Entry<String, Object> placeholder : placeholders.entrySet()) {
            preparedStatement.setObject(keyMapIndex.get(placeholder.getKey().toLowerCase(Locale.ROOT)), placeholder.getValue());
        }

        return preparedStatement;
    }

}
