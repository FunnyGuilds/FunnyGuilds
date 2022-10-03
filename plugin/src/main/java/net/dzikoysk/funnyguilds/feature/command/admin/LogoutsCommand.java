package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
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

        int logouts = Integer.parseInt(args[1]);

        user.getRank().setLogouts(logouts);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", user.getName())
                .register("{LOGOUTS}", logouts);

        this.sendMessage(sender, formatter.format(this.messages.adminLogoutsChanged));
    }

}
