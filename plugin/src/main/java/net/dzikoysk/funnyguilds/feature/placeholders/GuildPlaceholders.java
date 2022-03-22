package net.dzikoysk.funnyguilds.feature.placeholders;

import net.dzikoysk.funnyguilds.Entity;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.GuildPlaceholder;
import net.dzikoysk.funnyguilds.guild.Guild;
import panda.utilities.text.Joiner;

public class GuildPlaceholders extends Placeholders<Guild, GuildPlaceholder> {

    private static final Placeholders<Guild, GuildPlaceholder> GUILD_PLACEHOLDERS = new GuildPlaceholders();

    static {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();

        GUILD_PLACEHOLDERS
                .register("name", new GuildPlaceholder(Guild::getName, () -> messages.gNameNoValue))
                .register("tag", new GuildPlaceholder(Guild::getTag, () -> messages.gTagNoValue))
                .register("owner", new GuildPlaceholder(Guild::getOwner, () -> messages.gOwnerNoValue))
                .register("deputies", new GuildPlaceholder((guild -> guild.getDeputies().isEmpty()
                        ? messages.gDeputiesNoValue
                        : Joiner.on(", ").join(Entity.names(guild.getDeputies()))), () -> messages.gDeputiesNoValue))
                .register("deputy", new GuildPlaceholder((guild -> guild.getDeputies().isEmpty()
                        ? messages.gDeputiesNoValue
                        : guild.getDeputies().iterator().next().getName()), () -> messages.gDeputiesNoValue))
                .register("members-online", new GuildPlaceholder((guild -> guild.getOnlineMembers().size()), () -> 0))
                .register("members-all", new GuildPlaceholder((guild -> guild.getMembers().size()), () -> 0))
                .register("allies", new GuildPlaceholder((guild -> guild.getAllies().size()), () -> 0))

        ;
    }

}
