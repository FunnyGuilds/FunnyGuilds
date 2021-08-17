package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;

import java.util.ArrayList;
import java.util.List;

@FunnyComponent
final class GuildsCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(GuildUtils.getGuilds(), prefix, limit, ArrayList::new, Guild::getTag);
    }

    @Override
    public String getName() {
        return "guilds";
    }

}
