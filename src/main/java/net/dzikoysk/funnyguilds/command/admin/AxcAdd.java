package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import org.panda_lang.utilities.commons.text.MessageFormatter;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcAdd implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 1) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        if (!GuildUtils.tagExists(args[0])) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(messages.generalNoNickGiven);
            return;
        }
        
        User user = User.get(args[1]);

        if (user == null) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }

        if (user.hasGuild()) {
            sender.sendMessage(messages.generalUserHasGuild);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        User admin = (sender instanceof Player) ? User.get(sender.getName()) : null;
        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(admin == null ? EventCause.CONSOLE : EventCause.ADMIN, admin, guild, user))) {
            return;
        }
        
        guild.addMember(user);
        user.setGuild(guild);

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(user.getName()));

        MessageFormatter formatter = new MessageFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        Player player = user.getPlayer();
        Player owner = guild.getOwner().getPlayer();

        if (player != null) {
            player.sendMessage(formatter.format(messages.joinToMember));
        }

        if (owner != null) {
            owner.sendMessage(formatter.format(messages.joinToOwner));
        }

        Bukkit.broadcastMessage(formatter.format(messages.broadcastJoin));
    }

}
