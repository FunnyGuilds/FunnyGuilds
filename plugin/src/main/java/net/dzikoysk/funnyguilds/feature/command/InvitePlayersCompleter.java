package net.dzikoysk.funnyguilds.feature.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import panda.std.stream.PandaStream;

public class InvitePlayersCompleter implements Completer {

    private final PluginConfiguration configuration;
    private final UserManager userManager;

    public InvitePlayersCompleter(PluginConfiguration configuration, UserManager userManager) {
        this.configuration = configuration;
        this.userManager = userManager;
    }


    @Override
    public String getName() {
        return "invite-players";
    }

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        String[] args = context.getArguments();

        if (args.length != 1) {
            return Collections.emptyList();
        }

        return CommandUtils.collectCompletions(
                PandaStream.of(Bukkit.getServer().getOnlinePlayers())
                        .mapOpt(this.userManager::findByPlayer)
                        .filterNot(User::hasGuild)
                        .filterNot(User::isVanished)
                        .map(User::getName)
                        .concat(configuration.inviteCommandAllArgument)
                        .toList(),
                prefix, limit, ArrayList::new, it -> it
        );

    }
}
