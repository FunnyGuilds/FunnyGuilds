package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class KickAdminCommand {

    @FunnyCommand(
        name = "${admin.kick.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);

        User user = UserValidation.requireUserByName(args[0]);
        when (!user.hasGuild(), messages.generalPlayerHasNoGuild);
        when (user.isOwner(), messages.adminGuildOwner);

        Guild guild = user.getGuild();
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(AdminUtils.getCause(admin), admin, guild, user))) {
            return;
        }

        ConcurrencyManager concurrencyManager = plugin.getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(user.getName(), plugin));

        Player player = user.getPlayer();
        guild.removeMember(user);
        user.removeGuild();

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        if (player != null) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(plugin, player));
            player.sendMessage(formatter.format(messages.kickToPlayer));
        }

        sender.sendMessage(formatter.format(messages.kickToOwner));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastKick));
    }

}
