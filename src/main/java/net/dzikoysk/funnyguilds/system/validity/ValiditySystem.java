package net.dzikoysk.funnyguilds.system.validity;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;

public class ValiditySystem {

    private static ValiditySystem instance;

    public ValiditySystem() {
        instance = this;
    }

    public static ValiditySystem getInstance() {
        if (instance == null) {
            new ValiditySystem();
        }
        
        return instance;
    }

    public void run() {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (!guild.isValid()) {
                if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, null, guild))) {
                    continue;
                }
                
                ValidityUtils.broadcast(guild);
                GuildUtils.deleteGuild(guild);
            }
        }
    }
    
}
