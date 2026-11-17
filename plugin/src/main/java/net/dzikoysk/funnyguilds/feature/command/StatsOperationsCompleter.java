package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

@FunnyComponent
final class StatsOperationsCompleter implements Completer {

    private static final List<String> OPERATIONS = Arrays.asList("set", "add", "remove");

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(OPERATIONS, prefix, limit, ArrayList::new, Function.identity());
    }

    @Override
    public String getName() {
        return "stats-operations";
    }

}
