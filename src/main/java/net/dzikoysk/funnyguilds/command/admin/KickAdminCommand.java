package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

public final class KickAdminCommand {

    @FunnyCommand(
        name = "${admin.kick.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }

        User user = User.get(args[0]);

        if (user == null) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        if (!user.hasGuild()) {
            sender.sendMessage(messages.generalPlayerHasNoGuild);
            return;
        }

        if (user.isOwner()) {
            sender.sendMessage(messages.adminGuildOwner);
            return;
        }

        Guild guild = user.getGuild();
        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, user))) {
            return;
        }

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(user.getName()));

        Player player = user.getPlayer();
        guild.removeMember(user);
        user.removeGuild();

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        if (player != null) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(player));
            player.sendMessage(formatter.format(messages.kickToPlayer));
        }

        sender.sendMessage(formatter.format(messages.kickToOwner));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastKick));
    }

}
