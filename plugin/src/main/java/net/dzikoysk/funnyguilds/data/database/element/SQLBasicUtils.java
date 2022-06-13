package net.dzikoysk.funnyguilds.data.database.element;

import java.util.HashMap;
import java.util.stream.Collectors;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public final class SQLBasicUtils {

    private SQLBasicUtils() {
    }

    public static SQLNamedStatement getInsert(SQLTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(table.getNameGraveAccent());
        query.append(" (");
        query.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyGraveAccent));
        query.append(") VALUES (");
        query.append(Joiner.on(", ").join(table.getSqlElements(), sqlElement -> "?"));
        query.append(") ON DUPLICATE KEY UPDATE ");
        query.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyValuesAssignment));

        return new SQLNamedStatement(query.toString(), table.getMapElementsKey());
    }

    public static SQLNamedStatement getSelect(SQLTable table, String... sqlElements) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        if (sqlElements.length == 0) {
            throw new IllegalArgumentException("Given sqlElements String array is empty");
        }

        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PandaStream.of(sqlElements)
                .mapOpt(table::getSQLElement)
                .map(SQLElement::getKeyGraveAccent)
                .collect(Collectors.joining(", "))
        );
        query.append(" FROM ");
        query.append(table.getNameGraveAccent());

        return new SQLNamedStatement(query.toString(), new HashMap<>());
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
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(table.getNameGraveAccent());
        query.append(" SET ");
        query.append(element.getKeyGraveAccent());
        query.append(" = ?");
        query.append(" WHERE ");
        query.append(table.getPrimaryKey().getKeyGraveAccent());
        query.append(" = ?");

        keyMap.put(element.getKey(), 1);
        keyMap.put(table.getPrimaryKey().getKey(), 2);

        return new SQLNamedStatement(query.toString(), keyMap);
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
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(table.getNameGraveAccent());
        query.append(" WHERE ");
        query.append(table.getPrimaryKey().getKeyGraveAccent());
        query.append(" = ?");

        keyMap.put(table.getPrimaryKey().getKey(), 1);
        return new SQLNamedStatement(query.toString(), keyMap);
    }

    public static SQLNamedStatement getAlter(SQLTable table, SQLElement column) {
        if (table == null) {
            throw new IllegalArgumentException("Given SQLTable is null");
        }

        if (column == null) {
            throw new IllegalArgumentException("Given SQLElement is null");
        }

        StringBuilder query = new StringBuilder();
        int index = table.getIndexElement(column.getKey());

        query.append("ALTER TABLE ");
        query.append(table.getNameGraveAccent());
        query.append(" ADD COLUMN ");
        query.append(column.getKeyGraveAccent());
        query.append(" ");
        query.append(column.getType());
        query.append(index == 0 ? " FIRST" : " AFTER " + table.getSqlElements().get(index - 1).getKeyGraveAccent());
        query.append(";");

        return new SQLNamedStatement(query.toString(), new HashMap<>());
    }

}
