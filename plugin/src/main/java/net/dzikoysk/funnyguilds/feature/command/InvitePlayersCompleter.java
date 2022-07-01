package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.List;
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
        return CommandUtils.collectCompletions(
                PandaStream.of(Bukkit.getServer().getOnlinePlayers())
                        .mapOpt(this.userManager::findByPlayer)
                        .filterNot(User::hasGuild)
                        .filterNot(User::isVanished)
                        .map(User::getName)
                        .concat(this.configuration.inviteCommandAllArgument)
                        .toList(),
                prefix, limit, ArrayList::new, it -> it
        );

    }
}
