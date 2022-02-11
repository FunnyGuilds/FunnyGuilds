package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;

public class GuildValidationHandler implements Runnable {

    private final FunnyGuilds plugin;
    private final GuildManager guildManager;

    private int banGuildsCounter;
    private int validateGuildsCounter;

    public GuildValidationHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.guildManager = plugin.getGuildManager();
    }

    @Override
    public void run() {
        if (++validateGuildsCounter >= 10) {
            this.validateGuildLifetime();
        }

        if (++banGuildsCounter >= 7) {
            this.validateGuildBans();
        }
    }

    private void validateGuildLifetime() {
        for (Guild guild : this.guildManager.getGuilds()) {
            if (guild.isValid()) {
                continue;
            }

            if (!SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, null, guild))) {
                continue;
            }

            ValidityUtils.broadcast(guild);
            this.guildManager.deleteGuild(this.plugin, guild);
        }

        this.validateGuildsCounter = 0;
    }

    private void validateGuildBans() {
        for (Guild guild : this.guildManager.getGuilds()) {
            if (guild.getBan() > System.currentTimeMillis()) {
                continue;
            }

            guild.setBan(0);
            guild.markChanged();
        }

        this.banGuildsCounter = 0;
    }

}
