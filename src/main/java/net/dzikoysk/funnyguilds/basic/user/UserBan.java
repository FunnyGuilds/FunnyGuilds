package net.dzikoysk.funnyguilds.basic.user;

import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import org.panda_lang.panda.utilities.commons.objects.StringUtils;

public class UserBan {

    private final String reason;
    private final long banTime;

    public UserBan(String reason, long banTime) {
        this.reason = reason;
        this.banTime = banTime;
    }

    public boolean isBanned() {
        return this.banTime != 0;
    }

    public String getReason() {
        if (this.reason != null) {
            return ChatUtils.colored(this.reason);
        }

        return StringUtils.EMPTY;
    }

    public long getBanTime() {
        return banTime;
    }

}
