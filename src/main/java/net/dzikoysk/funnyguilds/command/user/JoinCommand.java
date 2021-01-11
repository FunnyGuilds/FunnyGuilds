package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.panda_lang.utilities.commons.text.Formatter;

import java.util.List;

public final class JoinCommand {

    @FunnyCommand(
        name = "${user.join.name}",
        description = "${user.join.description}",
        aliases = "${user.join.aliases}",
        permission = "funnyguilds.join",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
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

        if (! ItemUtils.playerHasEnoughItems(player, requiredItems)) {
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

        Formatter formatter = new Formatter()
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
