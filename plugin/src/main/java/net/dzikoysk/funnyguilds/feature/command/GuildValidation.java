package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.apache.commons.lang3.StringUtils;

public final class GuildValidation {

    private GuildValidation() {}

    public static Guild requireGuildByTag(String tag) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();
        MessageConfiguration messageConfiguration = plugin.getMessageConfiguration();

        GuildManager guildManager = plugin.getGuildManager();

        if (!pluginConfiguration.guildTagKeepCase) {
            tag = pluginConfiguration.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }

        String finalTag = tag;
        return guildManager.findByTag(tag).orThrow(() -> {
            throw new ValidationException(StringUtils.replace(messageConfiguration.generalGuildNotExists, "{TAG}", finalTag));
        });
    }

}
