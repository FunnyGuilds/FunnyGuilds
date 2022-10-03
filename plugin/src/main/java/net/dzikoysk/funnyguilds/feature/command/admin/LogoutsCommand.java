package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.LogoutsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class LogoutsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.logouts.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);
        when(args.length < 2, this.messages.adminNoLogoutsGiven);

        User user = UserValidation.requireUserByName(args[0]);

        int newLogouts = Integer.parseInt(args[1]);
        int previousLogouts = user.getRank().getLogouts();
        int logoutsChange = newLogouts - previousLogouts;

        User admin = AdminUtils.getAdminUser(sender);

        LogoutsChangeEvent logoutsChangeEvent = new LogoutsChangeEvent(AdminUtils.getCause(admin), admin, user, logoutsChange);
        if (!SimpleEventHandler.handle(logoutsChangeEvent)) {
            return;
        }

        newLogouts = previousLogouts + logoutsChangeEvent.getLogoutsChange();

        user.getRank().setLogouts(newLogouts);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", user.getName())
                .register("{LOGOUTS}", newLogouts);

        this.sendMessage(sender, formatter.format(this.messages.adminLogoutsChanged));
    }

}
