package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.panda_lang.panda.utilities.commons.redact.MessageFormatter;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExcKick implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.generalHasNoGuild);
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.generalIsNotOwner);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(messages.generalNoNickGiven);
            return;
        }

        User formerUser = User.get(args[0]);

        if (!formerUser.hasGuild()) {
            player.sendMessage(messages.generalPlayerHasNoGuild);
            return;
        }

        if (!user.getGuild().equals(formerUser.getGuild())) {
            player.sendMessage(messages.kickOtherGuild);
            return;
        }

        if (formerUser.isOwner()) {
            player.sendMessage(messages.kickOwner);
            return;
        }

        Guild guild = user.getGuild();

        if (!SimpleEventHandler.handle(new GuildMemberKickEvent(EventCause.USER, user, guild, formerUser))) {
            return;
        }
        
        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalRemovePlayerRequest(formerUser.getName()));

        guild.removeMember(formerUser);
        formerUser.removeGuild();

        if (formerUser.isOnline()) {
            concurrencyManager.postRequests(new PrefixGlobalUpdatePlayer(player));
        }

        MessageFormatter formatter = new MessageFormatter()
                .register("{PLAYER}", formerUser.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        player.sendMessage(formatter.format(messages.kickToOwner));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastKick));

        Player formerPlayer = formerUser.getPlayer();

        if (formerPlayer != null) {
            formerPlayer.sendMessage(formatter.format(messages.kickToPlayer));
        }
    }

}
