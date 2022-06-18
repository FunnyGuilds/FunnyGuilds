package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;

public final class GuildValidation {

    private GuildValidation() {
    }

    public static Guild requireGuildByTag(String tag) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();
        MessageConfiguration messages = plugin.getMessageConfiguration();

        return plugin.getGuildManager().findByTag(tag, true).orThrow(() -> {
            return new ValidationException(FunnyFormatter.format(messages.generalGuildNotExists, "{TAG}", tag));
        });
    }

}
