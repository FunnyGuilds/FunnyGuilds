package net.dzikoysk.funnyguilds.data.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConfirmationList {

    private static final List<UUID> CONFIRMATION_LIST = new ArrayList<>();

    private ConfirmationList() {
    }

    public static void add(UUID uuid) {
        if (!CONFIRMATION_LIST.contains(uuid)) {
            CONFIRMATION_LIST.add(uuid);
        }
    }

    public static void remove(UUID uuid) {
        CONFIRMATION_LIST.remove(uuid);
    }

    public static boolean contains(UUID uuid) {
        return CONFIRMATION_LIST.contains(uuid);
    }
}
