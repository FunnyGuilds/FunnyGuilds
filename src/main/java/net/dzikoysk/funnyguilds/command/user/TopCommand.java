package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@FunnyComponent
public final class TopCommand {

    @FunnyCommand(
        name = "${user.top.name}",
        description = "${user.top.description}",
        aliases = "${user.top.aliases}",
        permission = "funnyguilds.top",
        acceptsExceeded = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, CommandSender sender) {
        User user = sender instanceof Player
                ? plugin.getUserManager().getUser((Player) sender)
                : null;

        for (String messageLine : messages.topList) {
            String parsedRank = RankUtils.parseRank(user, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
