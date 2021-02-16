package net.dzikoysk.funnyguilds.data.database.element;

import org.panda_lang.utilities.commons.text.Joiner;

import java.util.HashMap;

public class SQLUtils {

    private SQLUtils() {}

    public static SQLBuilderStatement getBuilderInsert(SQLTable table) {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO `").append(table.getName()).append("` (");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyGraveAccent));
        sb.append(") VALUES (");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), value -> "?"));
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append(Joiner.on(", ").join(table.getSqlElements(), SQLElement::getKeyValuesAssignment));

        System.out.println(sb.toString());

        return new SQLBuilderStatement(sb.toString(), table.getMapElementsKey(1));
    }

    public static SQLBuilderStatement getBuilderUpdate(SQLTable table, SQLElement element) {
        HashMap<String, Integer> keyMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE `");
        sb.append(table.getName());
        sb.append("` SET ");
        sb.append(element.getKeyGraveAccent());
        sb.append(" = ?");
        sb.append(" WHERE ");
        sb.append(table.getPrimaryKey().getKeyGraveAccent());
        sb.append(" = ?");

        keyMap.put(element.getKey(), 1);
        keyMap.put(table.getPrimaryKey().getKey(), 2);

        return new SQLBuilderStatement(sb.toString(), keyMap);
    }

}
