package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;

@FunnyComponent
final class GuildsCompleter implements Completer {

    private final GuildManager guildManager;

    GuildsCompleter(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(this.guildManager.getGuilds(), prefix, limit, ArrayList::new, Guild::getTag);
    }

    @Override
    public String getName() {
        return "guilds";
    }

}
