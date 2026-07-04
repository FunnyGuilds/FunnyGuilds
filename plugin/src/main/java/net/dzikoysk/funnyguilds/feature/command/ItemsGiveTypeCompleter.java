package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.admin.ItemsAdminCommand.ItemsGiveType;

@FunnyComponent
public class ItemsGiveTypeCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(
            ItemsGiveType.ALL_COMMAND_ARGS, prefix, limit, ArrayList::new, Function.identity()
        );
    }

    @Override
    public String getName() {
        return "items-give-types";
    }
}
