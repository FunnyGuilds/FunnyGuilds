package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TopCommand {

    @FunnyCommand(
        name = "${user.top.name}",
        description = "${user.top.description}",
        aliases = "${user.top.aliases}",
        permission = "funnyguilds.top",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        User user = sender instanceof Player
                ? User.get((Player) sender)
                : null;

        for (String messageLine : FunnyGuilds.getInstance().getMessageConfiguration().topList) {
            String parsedRank = RankUtils.parseRank(user, messageLine);
            sender.sendMessage(parsedRank == null ? messageLine : parsedRank);
        }
    }

}
