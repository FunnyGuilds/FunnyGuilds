package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.PointsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import panda.std.Option;
import panda.utilities.StringUtils;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class PointsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.points.name}",
            permission = "funnyguilds.admin",
            completer = "online-players:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoNickGiven);
        when(args.length < 2, messages.adminNoPointsGiven);

        int points = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            throw new ValidationException(StringUtils.replace(messages.adminErrorInNumber, "{ERROR}", args[1]));
        });

        User user = UserValidation.requireUserByName(args[0]);
        UserRank userRank = user.getRank();

        User admin = AdminUtils.getAdminUser(sender);
        int change = points - userRank.getPoints();

        PointsChangeEvent pointsChangeEvent = new PointsChangeEvent(AdminUtils.getCause(admin), admin, user, change);
        if (!SimpleEventHandler.handle(pointsChangeEvent)) {
            return;
        }

        int finalPoints = user.getRank().getPoints() + pointsChangeEvent.getPointsChange();
        user.getRank().setPoints(finalPoints);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", user.getName())
                .register("{POINTS-FORMAT}", NumberRange.inRangeToString(finalPoints, config.pointsFormat))
                .register("{POINTS}", finalPoints);

        sendMessage(sender, formatter.format(messages.adminPointsChanged));
    }

}
