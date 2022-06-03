package net.dzikoysk.funnyguilds.data.database.element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
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

        if (!this.keyMapIndex.containsKey(key)) {
            return;
        }

        this.placeholders.put(key, value);
    }

    public void executeUpdate() {
        this.executeUpdate(false);
    }

    public void executeUpdate(boolean ignoreFails) {
        try (Connection connection = FunnyGuilds.getInstance().getDatabase().getConnection()) {
            if (connection == null) {
                throw new SQLException("Connection is null");
            }

            try (PreparedStatement statement = this.setPlaceholders(connection.prepareStatement(this.sql))) {
                statement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            if (ignoreFails) {
                FunnyGuilds.getPluginLogger().debug("Could not execute update (ignoreFails)");
                return;
            }

            FunnyGuilds.getPluginLogger().error("Could not execute update", exception);
        }
    }

    public void executeQuery(ThrowingConsumer<ResultSet, SQLException> consumer) {
        this.executeQuery(consumer, false);
    }

    public void executeQuery(ThrowingConsumer<ResultSet, SQLException> consumer, boolean ignoreFails) {
        try (Connection connection = FunnyGuilds.getInstance().getDatabase().getConnection()) {
            if (connection == null) {
                throw new SQLException("Connection is null");
            }

            try (PreparedStatement statement = this.setPlaceholders(connection.prepareStatement(this.sql))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    consumer.accept(resultSet);
                }
            }
        }
        catch (SQLException exception) {
            if (ignoreFails) {
                FunnyGuilds.getPluginLogger().debug("Could not execute query (ignoreFails)");
                return;
            }

            FunnyGuilds.getPluginLogger().error("Could not execute query", exception);
        }
    }

    private PreparedStatement setPlaceholders(PreparedStatement preparedStatement) {
        this.placeholders.forEach((key, value) -> {
            try {
                preparedStatement.setObject(this.keyMapIndex.get(key.toLowerCase(Locale.ROOT)), value);
            }
            catch (SQLException exception) {
                FunnyGuilds.getPluginLogger().error("Could not prepare query", exception);
            }
        });

        return preparedStatement;
    }

}
