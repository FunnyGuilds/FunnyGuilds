package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.rank.Rank;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.rank.DeathsChangeEvent;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.function.Option;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class DeathsCommand {

    @FunnyCommand(
        name = "${admin.deaths.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoNickGiven);
        when (args.length < 2, messages.adminNoDeathsGiven);

        int deaths = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(args[1])).orThrow(() -> {
            throw new ValidationException(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
        });

        User admin = AdminUtils.getAdminUser(sender);
        User user = UserValidation.requireUserByName(args[0]);
        Rank userRank = user.getRank();
        int change = deaths - userRank.getDeaths();

        if (!SimpleEventHandler.handle(new DeathsChangeEvent(AdminUtils.getCause(admin), userRank, admin, change))) {
            return;
        }
        
        userRank.setDeaths(deaths);
        sender.sendMessage(messages.adminDeathsChanged.replace("{PLAYER}", user.getName()).replace("{DEATHS}", Integer.toString(deaths)));
    }

}
