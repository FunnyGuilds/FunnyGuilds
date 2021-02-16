package net.dzikoysk.funnyguilds.data.database.element;

public class SQLValue {

    private final SQLElement sqlElement;
    private final String value;

    public SQLValue(SQLElement sqlElement, String value) {
        this.sqlElement = sqlElement;
        this.value = value;
    }

    public SQLElement getSqlElement() {
        return sqlElement;
    }

    public String getKeyGraveAccent() {
        return sqlElement.getKeyGraveAccent();
    }

    public String getKeyAssignment() {
        return sqlElement.getKeyGraveAccent() + " = ?";
    }

    public String getValue() {
        return value;
    }
}
