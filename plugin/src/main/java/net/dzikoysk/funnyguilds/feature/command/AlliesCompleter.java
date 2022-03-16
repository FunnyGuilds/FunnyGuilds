package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;

@FunnyComponent
final class AlliesCompleter implements Completer {

    private final UserManager userManager;

    AlliesCompleter(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return this.userManager.findByName(context.getCommandSender().getName())
                .filter(User::hasGuild)
                .flatMap(User::getGuild)
                .map(guild -> CommandUtils.collectCompletions(guild.getAllies(), prefix, limit, ArrayList::new, Guild::getName))
                .orElseGet(Collections.emptyList());
    }

    @Override
    public String getName() {
        return "allies";
    }

}
