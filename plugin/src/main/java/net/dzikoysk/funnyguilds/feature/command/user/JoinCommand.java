package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.util.InvitationList;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.utilities.text.Formatter;

import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class JoinCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.join.name}",
        description = "${user.join.description}",
        aliases = "${user.join.aliases}",
        permission = "funnyguilds.join",
        completer = "guilds:3",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when (user.hasGuild(), messageConfiguration.joinHasGuild);

        List<InvitationList.Invitation> invitations = InvitationList.getInvitationsFor(player);
        when (invitations.size() == 0, messageConfiguration.joinHasNotInvitation);

        if (args.length < 1) {
            String guildNames = ChatUtils.toString(InvitationList.getInvitationGuildNames(player), false);

            for (String msg : messageConfiguration.joinInvitationList) {
                player.sendMessage(msg.replace("{GUILDS}", guildNames));
            }
            
            return;
        }

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (!InvitationList.hasInvitationFrom(player, GuildUtils.getByTag(guild.getTag())), messageConfiguration.joinHasNotInvitationTo);

        List<ItemStack> requiredItems = pluginConfiguration.joinItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        when (guild.getMembers().size() >= pluginConfiguration.maxMembersInGuild, messageConfiguration.inviteAmountJoin.replace("{AMOUNT}", Integer.toString(pluginConfiguration.maxMembersInGuild)));

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

        this.concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(user.getName()));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        player.sendMessage(formatter.format(messageConfiguration.joinToMember));
        Bukkit.broadcastMessage(formatter.format(messageConfiguration.broadcastJoin));

        Player owner = guild.getOwner().getPlayer();

        if (owner != null) {
            owner.sendMessage(formatter.format(messageConfiguration.joinToOwner));
        }
    }

}
