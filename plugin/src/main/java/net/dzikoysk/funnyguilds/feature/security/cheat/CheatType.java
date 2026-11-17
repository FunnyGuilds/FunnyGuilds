package net.dzikoysk.funnyguilds.feature.security.cheat;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;
import net.kyori.adventure.text.Component;

public enum CheatType {

    REACH("Reach", config -> config.securitySystemReach),
    FREE_CAM("FreeCam", config -> config.securitySystemFreeCam);

    private final String name;
    private final Function<MessageConfiguration, Component> noteSupplier;

    CheatType(String name, Function<MessageConfiguration, Component> noteSupplier) {
        this.name = name;
        this.noteSupplier = noteSupplier;
    }

    public String getName() {
        return this.name;
    }

    public Function<MessageConfiguration, Component> getNoteSupplier() {
        return this.noteSupplier;
    }

}
