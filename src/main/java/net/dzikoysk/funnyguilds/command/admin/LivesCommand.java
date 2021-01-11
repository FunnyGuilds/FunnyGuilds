package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildLivesChangeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LivesCommand {

    @FunnyCommand(
        name = "${admin.lives.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.adminNoLivesGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        int lives;

        try {
            lives = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.adminErrorInNumber.replace("{ERROR}", args[1]));
            return;
        }

        User admin = (sender instanceof Player)
                ? User.get(sender.getName())
                : null;

        if (!SimpleEventHandler.handle(new GuildLivesChangeEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, lives))) {
            return;
        }
        
        guild.setLives(lives);
        sender.sendMessage(messages.adminLivesChanged.replace("{GUILD}", guild.getTag()).replace("{LIVES}", Integer.toString(lives)));
    }

}
