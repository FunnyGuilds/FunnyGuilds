package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import org.panda_lang.panda.utilities.commons.redact.MessageFormatter;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.spigot.ItemComponentUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExcJoin implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        PluginConfig config = Settings.getConfig();
        MessagesConfig messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (user.hasGuild()) {
            player.sendMessage(messages.joinHasGuild);
            return;
        }

        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(player);

        if (invitations.size() == 0) {
            player.sendMessage(messages.joinHasNotInvitation);
            return;
        }

        if (args.length < 1) {
            String guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(player), false);
            for (String msg : messages.joinInvitationList) {
                player.sendMessage(msg.replace("{GUILDS}", guildNames));
            }
            
            return;
        }

        String tag = args[0];
        Guild guild = GuildUtils.getByTag(tag);

        if (guild == null) {
            player.sendMessage(messages.joinTagExists);
            return;
        }

        if (!InvitationList.hasInvitationFrom(player, GuildUtils.getByTag(tag))) {
            player.sendMessage(messages.joinHasNotInvitationTo);
            return;
        }

        List<ItemStack> requiredItems = config.joinItems;
        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (config.enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(messages.createItems, requiredItems, requiredItem));
            } else {
                player.sendMessage(ItemUtils.translateTextPlaceholder(messages.createItems, requiredItems, requiredItem));
            }
            
            return;
        }
        
        if (!SimpleEventHandler.handle(new GuildMemberAcceptInviteEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        InvitationList.expireInvitation(guild, player);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(EventCause.USER, user, guild, user))) {
            return;
        }
        
        guild.addMember(user);
        user.setGuild(guild);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        ConcurrencyManager concurrencyManager = FunnyGuilds.getInstance().getConcurrencyManager();
        concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(user.getName()));

        MessageFormatter formatter = new MessageFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        player.sendMessage(formatter.format(messages.joinToMember));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastJoin));

        Player owner = guild.getOwner().getPlayer();
        if (owner != null) {
            owner.sendMessage(formatter.format(messages.joinToOwner));
        }
    }

}
