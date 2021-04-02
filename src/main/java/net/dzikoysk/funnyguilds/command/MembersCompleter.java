package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.function.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FunnyComponent
final class MembersCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return Option.when(context.getCommandSender() instanceof Player, User.get((OfflinePlayer) context.getCommandSender()))
                .filter(User::hasGuild)
                .map(User::getGuild)
                .map(guild -> CommandUtils.collectCompletions(guild.getMembers(), prefix, limit, ArrayList::new, User::getName))
                .orElseGet(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "members";
    }

}
