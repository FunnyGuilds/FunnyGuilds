package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class KillsCommand {

    @FunnyCommand(
        name = "${admin.kills.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);
        when (args.length < 2, messages.adminNoKillsGiven);

        int kills;
        try {
            kills = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = UserValidation.requireUserByName(args[0]);
        Rank userRank = user.getRank();

        int change = kills - userRank.getDeaths();
        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new KillsChangeEvent(AdminUtils.getCause(admin), userRank, admin, change))) {
            return;
        }

        user.getRank().setKills(kills);
        sender.sendMessage(messages.adminKillsChanged.replace("{PLAYER}", user.getName()).replace("{KILLS}", Integer.toString(kills)));
    }

}
