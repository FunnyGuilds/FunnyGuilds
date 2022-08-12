package net.dzikoysk.funnyguilds.feature.validity;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import panda.std.stream.PandaStream;

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
        if (++this.validateGuildsCounter >= 10) {
            this.validateGuildLifetime();
        }

        if (++this.banGuildsCounter >= 7) {
            this.validateGuildBans();
        }
    }

    private void validateGuildLifetime() {
        PandaStream.of(this.guildManager.getGuilds())
                .filterNot(Guild::isValid)
                .filter(guild -> SimpleEventHandler.handle(new GuildDeleteEvent(EventCause.SYSTEM, null, guild)))
                .forEach(guild -> {
                    ValidityUtils.broadcast(guild);
                    this.guildManager.deleteGuild(this.plugin, guild);
                });

        this.validateGuildsCounter = 0;
    }

    private void validateGuildBans() {
        PandaStream.of(this.guildManager.getGuilds())
                .filterNot(Guild::isBanned)
                .forEach(guild -> guild.setBan(null));

        this.banGuildsCounter = 0;
    }

}
