package net.dzikoysk.funnyguilds.data.database.element;

import net.dzikoysk.funnyguilds.basic.Basic;

public class SQLElement<T extends Basic> {

    private final String key;
    private final SQLType type;
    private final int size;
    private final boolean notNull;
    private final SQLSave<T> sqlSave;

    protected SQLElement(String key, SQLType type, int size, boolean notNull, SQLSave<T> sqlSave) {
        this.key = key;
        this.type = type;
        this.size = size;
        this.notNull = notNull;
        this.sqlSave = sqlSave;
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

    public Object getToSave(T data) {
        return sqlSave.save(data);
    }

}
