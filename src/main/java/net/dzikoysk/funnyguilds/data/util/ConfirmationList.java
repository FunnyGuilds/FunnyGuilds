package net.dzikoysk.funnyguilds.data.util;

import java.util.ArrayList;
import java.util.UUID;

public class ConfirmationList {

    private final static ArrayList<UUID> confirm = new ArrayList<UUID>();

    public static void add(UUID uuid) {
        if (!confirm.contains(uuid)) {
            confirm.add(uuid);
        }
    }

    public static void remove(UUID uuid) {
        confirm.remove(uuid);
    }

    public static boolean contains(UUID uuid) {
        return confirm.contains(uuid);
    }

}
