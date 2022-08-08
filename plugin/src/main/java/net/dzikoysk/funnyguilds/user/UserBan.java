package net.dzikoysk.funnyguilds.user;

import java.time.Instant;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class UserBan {

    private final String reason;
    private final Instant time;

    public UserBan(String reason, Instant time) {
        this.reason = reason;
        this.time = time;
    }

    public boolean isBanned() {
        return this.time.isAfter(Instant.now());
    }

    public String getReason() {
        if (this.reason != null) {
            return ChatUtils.colored(this.reason);
        }

        return "";
    }

    public Instant getTime() {
        return this.time;
    }

}
