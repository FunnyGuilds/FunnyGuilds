package net.dzikoysk.funnyguilds.data.core;

public enum DataType {

    FLAT(0),
    DATABASE(1),
    DUO(2);

    private final int id;

    private DataType(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

}
