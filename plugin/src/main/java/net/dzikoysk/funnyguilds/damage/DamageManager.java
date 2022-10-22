package net.dzikoysk.funnyguilds.damage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageManager {

    private final Map<UUID, DamageState> damageHistories = new HashMap<>();

    public DamageState getDamageState(UUID user) {
        return this.damageHistories.computeIfAbsent(user, uuid -> new DamageState(user));
    }

}
