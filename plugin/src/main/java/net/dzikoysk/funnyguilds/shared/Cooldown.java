package net.dzikoysk.funnyguilds.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.WeakHashMap;

public final class Cooldown<T> {

    private final Map<T, Instant> cooldowns = new WeakHashMap<>(32);

    public boolean isOnCooldown(T key) {
        Instant cooldown = this.cooldowns.get(key);
        if (cooldown == null) {
            return false;
        }

        if (cooldown.isAfter(Instant.now())) {
            return true;
        }

        this.cooldowns.remove(key);
        return false;
    }

    public void putOnCooldown(T key, Duration duration) {
        this.cooldowns.put(key, Instant.now().plus(duration));
    }

    public boolean cooldown(T key, Duration cooldown) {
        if (this.isOnCooldown(key)) {
            return true;
        }

        this.putOnCooldown(key, cooldown);
        return false;
    }

}
