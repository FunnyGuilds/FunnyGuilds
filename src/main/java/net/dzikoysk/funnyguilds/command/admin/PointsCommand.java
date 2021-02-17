package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.rank.RankManager;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class PointsCommand {

    @FunnyCommand(
        name = "${admin.points.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, PluginConfiguration config, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);
        when (args.length < 2, messages.adminNoPointsGiven);

        int points;
        try {
            points = Integer.parseInt(args[1]);
        } catch (NumberFormatException numberFormatException) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = UserValidation.requireUserByName(args[0]);

        Rank userRank = user.getRank();
        int change = points - userRank.getPoints();

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new PointsChangeEvent(AdminUtils.getCause(admin), userRank, admin, change))) {
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
