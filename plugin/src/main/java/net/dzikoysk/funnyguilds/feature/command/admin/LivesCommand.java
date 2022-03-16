package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class LivesCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.lives.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);
        when(args.length < 2, messages.adminNoLivesGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        int lives;
        try {
            lives = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            sendMessage(sender, (messages.adminErrorInNumber.replace("{ERROR}", args[1])));
            return;
        }

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildLivesChangeEvent(AdminUtils.getCause(admin), admin, guild, lives))) {
            return;
        }

        guild.setLives(lives);
        sendMessage(sender, (messages.adminLivesChanged.replace("{GUILD}", guild.getTag()).replace("{LIVES}", Integer.toString(lives))));
    }

}
