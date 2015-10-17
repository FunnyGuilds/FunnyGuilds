package net.dzikoysk.funnyguilds.system.validity;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;

public class ValiditySystem {

    private static ValiditySystem instance;

    public ValiditySystem() {
        instance = this;
    }

    public static ValiditySystem getInstance() {
        if (instance == null)
            new ValiditySystem();
        return instance;
    }

    public void run() {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (!guild.isValid()) {
                ValidityUtils.broadcast(guild);
                GuildUtils.deleteGuild(guild);
            }
        }
    }
}
