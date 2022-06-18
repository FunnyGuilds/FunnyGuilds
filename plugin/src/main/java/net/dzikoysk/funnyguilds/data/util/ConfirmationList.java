package net.dzikoysk.funnyguilds.data.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ConfirmationList {

    private static final Set<UUID> CONFIRMATION_LIST = new HashSet<>();

    private ConfirmationList() {
    }

    public static void add(UUID uuid) {
        CONFIRMATION_LIST.add(uuid);
    }

    public static void remove(UUID uuid) {
        CONFIRMATION_LIST.remove(uuid);
    }

    public static boolean contains(UUID uuid) {
        return CONFIRMATION_LIST.contains(uuid);
    }

}
