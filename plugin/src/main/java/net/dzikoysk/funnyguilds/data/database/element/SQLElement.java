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
        return this.key;
    }

    public String getKeyGraveAccent() {
        return "`" + this.key + "`";
    }

    public String getKeyValuesAssignment() {
        return this.getKeyGraveAccent() + "=VALUES(" + this.getKeyGraveAccent() + ")";
    }

    public String getType() {
        return this.size != -1 ? this.type.getName() + "(" + this.size + ")" : this.type.getName();
    }

    public SQLType getSQLType() {
        return this.type;
    }

    public boolean isNotNull() {
        return this.notNull;
    }

}
