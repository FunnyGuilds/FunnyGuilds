package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class PointsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.points.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when (args.length < 1, this.messageConfig.generalNoNickGiven);
        when (args.length < 2, this.messageConfig.adminNoPointsGiven);

        int points;
        try {
            points = Integer.parseInt(args[1]);
        } catch (NumberFormatException numberFormatException) {
            sender.sendMessage(this.messageConfig.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User user = UserValidation.requireUserByName(args[0]);

        UserRank userRank = user.getRank();
        int change = points - userRank.getPoints();

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new PointsChangeEvent(AdminUtils.getCause(admin), userRank, admin, change))) {
            return;
        }

        user.getRank().setPoints(points);

        String message = this.messageConfig.adminPointsChanged.replace("{PLAYER}", user.getName());
        message = message.replace("{POINTS-FORMAT}", IntegerRange.inRangeToString(points, this.pluginConfig.pointsFormat));
        message = message.replace("{POINTS}", String.valueOf(points));

        sender.sendMessage(message);
    }

}
