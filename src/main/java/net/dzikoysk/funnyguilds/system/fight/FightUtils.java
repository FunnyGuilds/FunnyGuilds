package net.dzikoysk.funnyguilds.system.fight;

import net.dzikoysk.funnyguilds.basic.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FightUtils {

    private static final Map<UUID, Long> FIGHT_CACHE_MAP = new HashMap<>();

    public static void attacked(User victim) {
        FIGHT_CACHE_MAP.put(victim.getUUID(), System.currentTimeMillis());
    }

    public boolean check(User user) {
        return FIGHT_CACHE_MAP.getOrDefault(user.getUUID(), 0L) > System.currentTimeMillis();
    }
    
    private FightUtils() {}
}
