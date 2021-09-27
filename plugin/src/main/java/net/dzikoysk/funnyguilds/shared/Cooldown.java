package net.dzikoysk.funnyguilds.shared;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public final class Cooldown<T> {

    private final Map<T, Long> cooldowns = new WeakHashMap<>(32);

    public boolean isOnCooldown(T key) {
        Long cooldown = cooldowns.get(key);

        if (cooldown == null) {
            return false;
        }

        if (cooldown > System.currentTimeMillis()) {
            return true;
        }

        cooldowns.remove(key);
        return false;
    }

    public void putOnCooldown(T key, TimeUnit unit, long duration) {
        this.cooldowns.put(key, System.currentTimeMillis() + unit.toMillis(duration));
    }

    public void putOnCooldown(T key, long cooldown) {
        this.cooldowns.put(key, System.currentTimeMillis() + cooldown);
    }

    public boolean cooldown(T key, TimeUnit unit, long duration) {
        return this.cooldown(key, unit.toMillis(duration));
    }

    public boolean cooldown(T key, long cooldown) {
        if (isOnCooldown(key)) {
            return true;
        }

        this.putOnCooldown(key, cooldown);
        return false;
    }

}
