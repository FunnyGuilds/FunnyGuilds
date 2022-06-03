package net.dzikoysk.funnyguilds.data.database.element;

public enum SQLType {

    INT("int"),
    BIGINT("bigint"),
    VARCHAR("varchar"),
    TEXT("text"),
    BOOLEAN("boolean");

    private final String type;

    SQLType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }

}
