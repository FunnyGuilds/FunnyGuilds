package net.dzikoysk.funnyguilds;

public interface Entity {

    enum EntityType {
        GUILD,
        OFFLINE_USER,
        RANK,
        REGION,
        USER
    }

    EntityType getType();

    String getName();

}
