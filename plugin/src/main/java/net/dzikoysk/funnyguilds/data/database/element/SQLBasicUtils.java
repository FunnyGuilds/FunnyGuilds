package net.dzikoysk.funnyguilds.data.database.element;

import java.util.HashMap;
import panda.std.Option;
import panda.utilities.text.Joiner;

public final class SQLBasicUtils {

    private SQLBasicUtils() {
    }

    public static SQLNamedStatement getInsert(SQLTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        String query = "INSERT INTO " +
                table.getNameGraveAccent() +
                " (" +
                Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyGraveAccent) +
                ") VALUES (" +
                Joiner.on(", ").join(table.getSqlElements(), sqlElement -> "?") +
                ") ON DUPLICATE KEY UPDATE " +
                Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyValuesAssignment);

        return new SQLNamedStatement(query, table.getMapElementsKey());
    }

    public static SQLNamedStatement getSelect(SQLTable table, String... sqlElements) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        if (sqlElements.length == 0) {
            throw new IllegalArgumentException("Given String array is empty");
        }

        String query = "SELECT " +
                Joiner.on(", ").join(sqlElements, sqlElement -> {
                    Option<SQLElement> element = table.getSQLElement(sqlElement);
                    if (element.isEmpty()) {
                        return "";
                    }

                    return element.get().getKeyGraveAccent();
                }) +
                " FROM " +
                table.getNameGraveAccent();

        return new SQLNamedStatement(query, new HashMap<>());
    }

    public static SQLNamedStatement getSelectAll(SQLTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        String query = "SELECT * FROM " + table.getNameGraveAccent();
        return new SQLNamedStatement(query, new HashMap<>());
    }

    public static SQLNamedStatement getUpdate(SQLTable table, SQLElement element) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        if (element == null) {
            throw new IllegalArgumentException("Given SQLElement is null");
        }

        HashMap<String, Integer> keyMap = new HashMap<>();
        String query = "UPDATE " +
                table.getNameGraveAccent() +
                " SET " +
                element.getKeyGraveAccent() +
                " = ?" +
                " WHERE " +
                table.getPrimaryKey().getKeyGraveAccent() +
                " = ?";

        keyMap.put(element.getKey(), 1);
        keyMap.put(table.getPrimaryKey().getKey(), 2);

        return new SQLNamedStatement(query, keyMap);
    }

    public static SQLNamedStatement getCreate(SQLTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(table.getNameGraveAccent());
        queryBuilder.append(" (");
        queryBuilder.append(Joiner.on(", ").join(table.getSqlElements(), sqlElement -> {
            StringBuilder elementBuilder = new StringBuilder();

            elementBuilder.append(sqlElement.getKeyGraveAccent());
            elementBuilder.append(" ");
            elementBuilder.append(sqlElement.getType());

            if (sqlElement.isNotNull()) {
                elementBuilder.append(" NOT NULL");
            }

            return elementBuilder.toString();
        }));

        queryBuilder.append(", PRIMARY KEY (");
        queryBuilder.append(table.getPrimaryKey().getKey());
        queryBuilder.append("));");

        return new SQLNamedStatement(queryBuilder.toString(), new HashMap<>());
    }

    public static SQLNamedStatement getDelete(SQLTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        HashMap<String, Integer> keyMap = new HashMap<>();
        String query = "DELETE FROM " +
                table.getNameGraveAccent() +
                " WHERE " +
                table.getPrimaryKey().getKeyGraveAccent() +
                " = ?";

        keyMap.put(table.getPrimaryKey().getKey(), 1);
        return new SQLNamedStatement(query, keyMap);
    }

    public static SQLNamedStatement getAlter(SQLTable table, SQLElement column) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        if (column == null) {
            throw new IllegalArgumentException("Given SQLElement is null");
        }

        int index = table.getIndexElement(column.getKey());
        String query = "ALTER TABLE " +
                table.getNameGraveAccent() +
                " ADD COLUMN " +
                column.getKeyGraveAccent() +
                " " +
                column.getType() +
                (index == 0 ? " FIRST" : " AFTER " + table.getSqlElements().get(index - 1).getKeyGraveAccent()) +
                ";";

        return new SQLNamedStatement(query, new HashMap<>());
    }

}
