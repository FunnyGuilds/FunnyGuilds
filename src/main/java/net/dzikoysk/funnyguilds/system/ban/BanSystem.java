package net.dzikoysk.funnyguilds.system.ban;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;

public class BanSystem {

    private static BanSystem instance;

    private BanSystem() {
        instance = this;
    }

    public static BanSystem getInstance() {
        if (instance == null) {
            new BanSystem();
        }
        
        return instance;
    }

    public void run() {
        for (Guild guild : GuildUtils.getGuilds()) {
            guild.isBanned();
        }
    }
}
