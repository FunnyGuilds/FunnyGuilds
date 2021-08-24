package net.dzikoysk.funnyguilds.feature.command;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FunnyComponent
final class MembersCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return UserManager.getInstance().getUser(context.getCommandSender().getName())
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
