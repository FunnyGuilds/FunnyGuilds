package net.dzikoysk.funnyguilds.data.database.element;

public class SQLElement {

    private final String key;
    private final SQLType type;
    private final int size;
    private final boolean notNull;

    protected SQLElement(String key, SQLType type, int size, boolean notNull) {
        this.key = key;
        this.type = type;
        this.size = size;
        this.notNull = notNull;
    }

    public String getKey() {
        return key;
    }

    public String getKeyGraveAccent() {
        return "`" + key + "`";
    }

    public String getKeyValuesAssignment() {
        return getKeyGraveAccent() + "=VALUES(" + getKeyGraveAccent() + ")";
    }

    public String getType() {
        return size != - 1 ? type.getName() + "(" + size + ")" : type.getName();
    }

    public SQLType getSQLType() {
        return type;
    }

    public boolean isNotNull() {
        return notNull;
    }
}
