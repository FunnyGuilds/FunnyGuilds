package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxcAdd implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();

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

        if (!UserUtils.playedBefore(args[1])) {
            sender.sendMessage(messages.generalNotPlayedBefore);
            return;
        }
        
        User user = User.get(args[1]);

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

        // IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getName());
        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(user.getName()));

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", user.getName());

        Player player = user.getPlayer();
        Player owner = guild.getOwner().getPlayer();

        if (player != null) {
            player.sendMessage(translator.translate(messages.joinToMember));
        }

        if (owner != null) {
            owner.sendMessage(translator.translate(messages.joinToOwner));
        }

        Bukkit.broadcastMessage(translator.translate(messages.broadcastJoin));
    }

}
