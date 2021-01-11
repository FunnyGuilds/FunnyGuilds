package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PointsCommand {

    @FunnyCommand(
        name = "${admin.points.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.adminNoPointsGiven);
            return;
        }

        int points;

        try {
            points = Integer.parseInt(args[1]);
        } catch (NumberFormatException numberFormatException) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = User.get(args[0]);

        if (user == null) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        Rank userRank = user.getRank();
        int change = points - userRank.getPoints();

        User admin = (sender instanceof Player)
                ? User.get(sender.getName())
                : null;

        if (!SimpleEventHandler.handle(new PointsChangeEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, userRank, admin, change))) {
            return;
        }
        
        user.getRank().setPoints(points);
        RankManager.getInstance().update(user);

        String message = messages.adminPointsChanged.replace("{PLAYER}", user.getName());
        message = message.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
        message = message.replace("{POINTS}", String.valueOf(points));
        
        sender.sendMessage(message);
    }

}
