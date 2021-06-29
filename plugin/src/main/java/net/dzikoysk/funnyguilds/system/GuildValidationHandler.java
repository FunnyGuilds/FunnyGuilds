package net.dzikoysk.funnyguilds.system;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.system.validity.ValidityUtils;

public class GuildValidationHandler implements Runnable {

    private int banGuildsCounter;
    private int validateGuildsCounter;

    @Override
    public void run() {
        if (++ validateGuildsCounter >= 10) {
            this.validateGuildLifetime();
        }

        if (++ banGuildsCounter >= 7) {
            this.validateGuildBans();
        }
    }

    private void validateGuildLifetime() {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.isValid()) {
                continue;
            }

            if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, null, guild))) {
                continue;
            }

            ValidityUtils.broadcast(guild);
            GuildUtils.deleteGuild(guild);
        }

        this.validateGuildsCounter = 0;
    }

    private void validateGuildBans() {
        for (Guild guild : GuildUtils.getGuilds()) {
            if (guild.getBan() > System.currentTimeMillis()) {
                continue;
            }

            guild.setBan(0);
            guild.markChanged();
        }

        this.banGuildsCounter = 0;
    }

}
