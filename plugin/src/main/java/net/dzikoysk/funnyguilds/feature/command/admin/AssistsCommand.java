package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.AssistsChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class AssistsCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.assists.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);
        when(args.length < 2, this.messages.adminNoAssistsGiven);

        User user = UserValidation.requireUserByName(args[0]);

        int newAssists = Integer.parseInt(args[1]);
        int previousAssists = user.getRank().getAssists();
        int assistsChange = newAssists - previousAssists;

        User admin = AdminUtils.getAdminUser(sender);

        AssistsChangeEvent assistsChangeEvent = new AssistsChangeEvent(AdminUtils.getCause(admin), admin, user, assistsChange);
        if (!SimpleEventHandler.handle(assistsChangeEvent)) {
            return;
        }

        newAssists = previousAssists + assistsChangeEvent.getAssistsChange();

        user.getRank().setAssists(newAssists);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", user.getName())
                .register("{ASSISTS}", newAssists);

        this.sendMessage(sender, formatter.format(this.messages.adminAssistsChanged));
    }

}
