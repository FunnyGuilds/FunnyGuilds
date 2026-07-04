package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

@FunnyComponent
public class ItemsGiveTypeCompleter implements Completer {

    public static final List<String> TYPES = List.of(
        "guild", "base", "join", "enlarge", "validity", "rankReset", "statsReset", "firstGuildReward"
    );

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(TYPES, prefix, limit, ArrayList::new, Function.identity());
    }

    @Override
    public String getName() {
        return "items-give-types";
    }
}
