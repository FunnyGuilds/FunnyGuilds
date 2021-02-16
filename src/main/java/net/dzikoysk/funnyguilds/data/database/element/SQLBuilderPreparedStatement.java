package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.database.Database;
import org.panda_lang.utilities.commons.text.Joiner;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLBuilderPreparedStatement {

    private final ArrayList<SQLValue> sqlValues = new ArrayList<>();
    private final SQLTable table;

    public SQLBuilderPreparedStatement(SQLTable table) {
        this.table = table;
    }

    public void setPlaceholder(String key, String value) {
        if (!table.containKey(key)) {
            return;
        }

        sqlValues.add(new SQLValue(table.getSQLElement(key), value));
    }

    public PreparedStatement build() {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO `?` (");
        sb.append(Joiner.on(", ").join(sqlValues, "?"));
        sb.append(") VALUES (");
        sb.append(Joiner.on(", ").join(sqlValues, "?"));
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append(Joiner.on(", ").join(sqlValues, "? = ?"));

        try {
            PreparedStatement preparedStatement = Database.getInstance().getDataSource().getConnection().prepareStatement(sb.toString());
            int valueNumber = 1;

            if (preparedStatement == null) {
                FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement");
                return null;
            }

            preparedStatement.setString(1, table.getName());

            for (SQLValue sqlValue : sqlValues) {
                valueNumber++;

                preparedStatement.setString(valueNumber, sqlValue.getSqlElement().getKeyGraveAccent());
                preparedStatement.setString((valueNumber + sqlValues.size()), sqlValue.getValue());
                preparedStatement.setString((valueNumber + (sqlValues.size() * 2)), sqlValue.getSqlElement().getKeyGraveAccent());
                preparedStatement.setString((valueNumber + (sqlValues.size() * 3)), sqlValue.getValue());

                return preparedStatement;
            }
        } catch (SQLException sqlException) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not execute create preparedStatement", sqlException);
        }

        return null;
    }

    public void remove(String key) {
        sqlValues.removeIf(sqlValue -> sqlValue.getSqlElement().getKey().equalsIgnoreCase(key));
    }

    public SQLTable getTable() {
        return table;
    }
}
