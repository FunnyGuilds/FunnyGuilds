package net.dzikoysk.funnyguilds.system.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum CheatType {

    FREECAM("FreeCam", SecurityType.GUILD),
    REACH("Reach", SecurityType.GUILD);

    private final String name;
    private final SecurityType securityType;

    CheatType(String name, SecurityType securityType) {
        this.name = name;
        this.securityType = securityType;
    }

    public String getName() {
        return name;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public static ArrayList<CheatType> getByType(SecurityType securityType) {
        return Arrays.stream(CheatType.values())
                .filter(type -> type.getSecurityType().equals(securityType))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

enum SecurityType {

    GUILD

}
