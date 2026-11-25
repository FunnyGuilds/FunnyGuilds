package net.dzikoysk.funnyguilds.data.database.element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;
import panda.std.function.ThrowingConsumer;

public class SQLNamedStatement {

    private final Map<String, Object> placeholders;
    private final Map<String, Integer> keyMapIndex;
    private final String sql;

    public SQLNamedStatement(String sql, Map<String, Integer> keyMap) {
        this(sql, Map.copyOf(keyMap), new HashMap<>());
    }

    private SQLNamedStatement(String sql, Map<String, Integer> keyMap, Map<String, Object> placeholders) {
        this.sql = sql;
        this.keyMapIndex = keyMap;
        this.placeholders = placeholders;
    }

    public BooleanSupplier snapshotUpdate() {
        SQLNamedStatement snapshot = new SQLNamedStatement(this.sql, this.keyMapIndex,
                Collections.unmodifiableMap(new HashMap<>(this.placeholders)));
        return () -> snapshot.tryExecuteUpdate(false);
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

    public void executeUpdate(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(this.sql)) {
            this.setPlaceholders(statement);
            statement.executeUpdate();
        }
    }

    public void executeUpdate(boolean ignoreFails) {
        this.tryExecuteUpdate(ignoreFails);
    }

    private boolean tryExecuteUpdate(boolean ignoreFails) {
        Optional<Database> database = FunnyGuilds.getInstance().getDatabase();
        if (database.isEmpty()) {
            FunnyGuilds.getPluginLogger().error("Could not execute update (database is null)");
            return false;
        }
        try (Connection connection = database.orElseThrow().getConnection()) {
            if (connection == null) {
                throw new SQLException("Connection is null");
            }

            this.executeUpdate(connection);
        }
        catch (Exception exception) {
            if (ignoreFails) {
                FunnyGuilds.getPluginLogger().debug("Could not execute update (ignoreFails)");
            }
            else {
                FunnyGuilds.getPluginLogger().error("Could not execute update", exception);
            }
            return false;
        }
        return true;
    }

    public void executeQuery(ThrowingConsumer<ResultSet, SQLException> consumer) {
        this.executeQuery(consumer, false);
    }

    public void executeQueryOrThrow(Connection connection, ThrowingConsumer<ResultSet, SQLException> consumer) throws SQLException {
        try (PreparedStatement statement = this.setPlaceholders(connection.prepareStatement(this.sql));
                ResultSet resultSet = statement.executeQuery()) {
            consumer.accept(resultSet);
        }
    }

    public void executeQuery(ThrowingConsumer<ResultSet, SQLException> consumer, boolean ignoreFails) {
        Optional<Database> database = FunnyGuilds.getInstance().getDatabase();
        if (database.isEmpty()) {
            FunnyGuilds.getPluginLogger().error("Could not execute query (database is null)");
            return;
        }
        try (Connection connection = database.orElseThrow().getConnection()) {
            if (connection == null) {
                throw new SQLException("Connection is null");
            }

            try (PreparedStatement statement = this.setPlaceholders(connection.prepareStatement(this.sql))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    consumer.accept(resultSet);
                }
            }
        }
        catch (Exception exception) {
            if (ignoreFails) {
                FunnyGuilds.getPluginLogger().debug("Could not execute query (ignoreFails)");
                return;
            }

            FunnyGuilds.getPluginLogger().error("Could not execute query", exception);
        }
    }

    private PreparedStatement setPlaceholders(PreparedStatement preparedStatement) throws RuntimeException {
        this.placeholders.forEach((key, value) -> {
            try {
                preparedStatement.setObject(this.keyMapIndex.get(key.toLowerCase(Locale.ROOT)), value);
            }
            catch (SQLException exception) {
                FunnyGuilds.getPluginLogger().error("Could not prepare query", exception);
                throw new RuntimeException("Query preparation failed");
            }
        });

        return preparedStatement;
    }

}
