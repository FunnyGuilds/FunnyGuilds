package net.dzikoysk.funnyguilds.feature.command;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;

public final class GuildValidation {

    private GuildValidation() {
    }

    public static Guild requireGuildByTag(String tag) {
        FunnyGuilds plugin = FunnyGuilds.getInstance();

        return plugin.getGuildManager().findByTag(tag, true).orThrow(() -> {
            return new InternalValidationException(config -> config.generalGuildNotExists, Replacement.string("{TAG}", tag));
        });
    }

}
