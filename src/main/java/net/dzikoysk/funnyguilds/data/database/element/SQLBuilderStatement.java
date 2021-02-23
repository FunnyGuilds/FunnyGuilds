package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;
import org.diorite.utils.collections.maps.CaseInsensitiveMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLBuilderStatement {

    private final Map<String, Object> placeholders = new HashMap<>();
    private final CaseInsensitiveMap<Integer> keyMapIndex;
    private final String sql;

    public SQLBuilderStatement(String sql, Map<String, Integer> keyMap) {
        this.sql = sql;
        this.keyMapIndex = new CaseInsensitiveMap<>(keyMap);
    }

    public void set(String key, Object value) {
        if (!keyMapIndex.containsKey(key)) {
            return;
        }

        placeholders.put(key, value);
    }

    public void remove(String key) {
        placeholders.remove(key);
    }

    public void executeUpdate() {

        try (Connection connection = Database.getInstance().getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            if (preparedStatement == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement");
                return;
            }

            for (Map.Entry<String, Object> placeholder : placeholders.entrySet()) {
                preparedStatement.setObject(keyMapIndex.get(placeholder.getKey()), placeholder.getValue());
            }

            preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement", sqlException);
        }
    }
}
