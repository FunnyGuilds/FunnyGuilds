package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.KillsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserRank;
import org.bukkit.command.CommandSender;
import panda.std.Option;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class KillsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.kills.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoNickGiven);
        when(args.length < 2, messages.adminNoKillsGiven);

        int kills = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            throw new ValidationException(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
        });

        User admin = AdminUtils.getAdminUser(sender);
        User user = UserValidation.requireUserByName(args[0]);

        UserRank userRank = user.getRank();
        int change = kills - userRank.getKills();

        KillsChangeEvent killsChangeEvent = new KillsChangeEvent(AdminUtils.getCause(admin), admin, user, change);
        if (!SimpleEventHandler.handle(killsChangeEvent)) {
            return;
        }
        change = killsChangeEvent.getKillsChange();

        int finalKills = user.getRank().getKills() + change;
        user.getRank().setKills(finalKills);

        sendMessage(sender, (messages.adminKillsChanged.replace("{PLAYER}", user.getName()).replace("{KILLS}", Integer.toString(finalKills))));
    }

}
