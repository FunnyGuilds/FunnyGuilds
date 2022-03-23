package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.apache.commons.lang3.StringUtils;

public final class GuildValidation {

    private GuildValidation() {
    }

    public static Guild requireGuildByTag(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().findByTag(tag, true).orThrow(() -> {
            throw new ValidationException(StringUtils.replace(FunnyGuilds.getInstance().getMessageConfiguration().generalGuildNotExists, "{TAG}", tag));
        });
    }

}
