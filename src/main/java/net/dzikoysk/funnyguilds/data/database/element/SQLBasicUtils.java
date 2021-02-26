package net.dzikoysk.funnyguilds.data.database.element;

import org.panda_lang.utilities.commons.text.Joiner;

import java.util.HashMap;

public class SQLBasicUtils {

    private SQLBasicUtils() {}

    public static SQLNamedStatement getInsert(SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO ");
        sb.append(table.getNameGraveAccent());
        sb.append(" (");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyGraveAccent));
        sb.append(") VALUES (");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), sqlElement -> "?"));
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyValuesAssignment));

        return new SQLNamedStatement(sb.toString(), table.getMapElementsKey(1));
    }

    public static SQLNamedStatement getSelect(SQLTable table, String... sqlElements) {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(Joiner.on(", ").join(sqlElements, sqlElement -> table.getSQLElement(sqlElement).getKeyGraveAccent()));
        sb.append(" FROM ");
        sb.append(table.getNameGraveAccent());

        return new SQLNamedStatement(sb.toString(), new HashMap<>());
    }

    public static SQLNamedStatement getSelectAll(SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM ");
        sb.append(table.getNameGraveAccent());

        return new SQLNamedStatement(sb.toString(), new HashMap<>());
    }

    public static SQLNamedStatement getUpdate(SQLTable table, SQLElement element) {
        HashMap<String, Integer> keyMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE ");
        sb.append(table.getNameGraveAccent());
        sb.append(" SET ");
        sb.append(element.getKeyGraveAccent());
        sb.append(" = ?");
        sb.append(" WHERE ");
        sb.append(table.getPrimaryKey().getKeyGraveAccent());
        sb.append(" = ?");

        keyMap.put(element.getKey(), 1);
        keyMap.put(table.getPrimaryKey().getKey(), 2);

        return new SQLNamedStatement(sb.toString(), keyMap);
    }

    public static SQLNamedStatement getCreate(SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(table.getNameGraveAccent());
        sb.append(" (");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), sqlElement -> {
            StringBuilder element = new StringBuilder();

            element.append(sqlElement.getKeyGraveAccent());
            element.append(" ");
            element.append(sqlElement.getType());

            if (sqlElement.isNotNull()) {
                element.append(" NOT NULL");
            }

            return element.toString();
        }));

        sb.append(", PRIMARY KEY (");
        sb.append(table.getPrimaryKey().getKey());
        sb.append("));");

        return new SQLNamedStatement(sb.toString(), new HashMap<>());
    }

    public static SQLNamedStatement getDelete(SQLTable table) {
        HashMap<String, Integer> keyMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append(table.getNameGraveAccent());
        sb.append(" WHERE ");
        sb.append(table.getPrimaryKey().getKeyGraveAccent());
        sb.append(" = ?");

        keyMap.put(table.getPrimaryKey().getKey(), 1);

        return new SQLNamedStatement(sb.toString(), keyMap);
    }

    public static SQLNamedStatement getAlter(SQLTable table, SQLElement column) {
        StringBuilder sb = new StringBuilder();
        int index = table.getIndexElement(column.getKey());

        sb.append("ALTER TABLE ");
        sb.append(table.getNameGraveAccent());
        sb.append(" ADD COLUMN ");
        sb.append(column.getKeyGraveAccent());
        sb.append(" ");
        sb.append(column.getType());
        sb.append(index == 0 ? " FIRST" : " AFTER " + table.getSqlElements().get(index - 1).getKeyGraveAccent());
        sb.append(";");

        return new SQLNamedStatement(sb.toString(),  new HashMap<>());
    }
}
