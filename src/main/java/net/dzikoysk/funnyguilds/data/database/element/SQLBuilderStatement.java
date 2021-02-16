package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLBuilderStatement {

    private final Map<String, String> placeholders = new HashMap<>();
    private final Map<String, Integer> keyMapIndex;
    private final String sql;

    public SQLBuilderStatement(String sql, Map<String, Integer> keyMap) {
        this.sql = sql;
        this.keyMapIndex = keyMap;
    }

    public void set(String key, String value) {
        if (!keyMapIndex.containsKey(key)) {
            return;
        }

        placeholders.put(key, value);
    }

    public void set(String key, int value) {
        if (!keyMapIndex.containsKey(key)) {
            return;
        }

        placeholders.put(key, String.valueOf(value));
    }

    public void remove(String key) {
        placeholders.remove(key);
    }

    public PreparedStatement build() {

        try {
            PreparedStatement preparedStatement = Database.getInstance().getDataSource().getConnection().prepareStatement(sql);

            if (preparedStatement == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement");
                return null;
            }

            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                preparedStatement.setString(keyMapIndex.get(placeholder.getKey()), placeholder.getValue());
            }

            return preparedStatement;
        }
        catch (SQLException sqlException) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement", sqlException);
        }

        return null;
    }
}
