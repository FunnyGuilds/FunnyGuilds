package net.dzikoysk.funnyguilds.feature.command;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;

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
                Stream.concat(
                        Bukkit.getServer().getOnlinePlayers().stream()
                                .flatMap(player -> this.userManager.findByPlayer(player).stream())
                                .filter(user -> !user.hasGuild())
                                .filter(user -> !user.isVanished())
                                .map(User::getName),
                        Stream.of(this.configuration.inviteCommandAllArgument)
                )
                .toList(),
                prefix, limit, ArrayList::new, it -> it
        );

    }
}
