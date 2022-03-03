package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.NumberRange;
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
        when(args.length < 1, messages.generalNoNickGiven);
        when(args.length < 2, messages.adminNoPointsGiven);

        int points;
        try {
            points = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException numberFormatException) {
            sendMessage(sender, (messages.adminErrorInNumber.replace("{ERROR}", args[1])));
            return;
        }

        User user = UserValidation.requireUserByName(args[0]);
        UserRank userRank = user.getRank();

        User admin = AdminUtils.getAdminUser(sender);
        int change = points - userRank.getPoints();

        PointsChangeEvent pointsChangeEvent = new PointsChangeEvent(AdminUtils.getCause(admin), admin, user, change);
        if (!SimpleEventHandler.handle(pointsChangeEvent)) {
            return;
        }
        change = pointsChangeEvent.getPointsChange();

        int finalPoints = user.getRank().getPoints() + change;
        user.getRank().setPoints(finalPoints);

        String message = messages.adminPointsChanged.replace("{PLAYER}", user.getName());
        message = message.replace("{POINTS-FORMAT}", NumberRange.inRangeToString(finalPoints, config.pointsFormat));
        message = message.replace("{POINTS}", String.valueOf(finalPoints));

        sendMessage(sender, (message));
    }

}
