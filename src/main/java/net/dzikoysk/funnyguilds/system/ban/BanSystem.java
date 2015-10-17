package net.dzikoysk.funnyguilds.system.ban;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;

public class BanSystem {

    private static BanSystem instance;

    public BanSystem() {
        instance = this;
    }

    public static BanSystem getInstance() {
        if (instance == null)
            new BanSystem();
        return instance;
    }

    public void run() {
        for (Guild guild : GuildUtils.getGuilds()) {
            guild.isBanned();
        }
    }
}
