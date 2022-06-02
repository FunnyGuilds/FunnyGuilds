package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import java.util.Set;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.panda_lang.utilities.inject.annotations.Inject;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class JoinCommand extends AbstractFunnyCommand {

    @Inject
    public GuildInvitationList guildInvitationList;

    @FunnyCommand(
            name = "${user.join.name}",
            description = "${user.join.description}",
            aliases = "${user.join.aliases}",
            permission = "funnyguilds.join",
            completer = "guild-invitations:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when(user.hasGuild(), messages.joinHasGuild);

        Set<GuildInvitation> invitations = guildInvitationList.getInvitationsFor(user);
        when(invitations.isEmpty(), messages.joinHasNotInvitation);

        if (args.length < 1) {
            String guildNames = ChatUtils.toString(guildInvitationList.getInvitationGuildNames(user), false);
            FunnyFormatter formatter = FunnyFormatter.of("{GUILDS}", guildNames);

            messages.joinInvitationList.forEach(line -> user.sendMessage(formatter.format(line)));
            return;
        }

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(!guildInvitationList.hasInvitation(guild, user), messages.joinHasNotInvitationTo);

        List<ItemStack> requiredItems = config.joinItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, messages.joinItems)) {
            return;
        }

        when(guild.getMembers().size() >= config.maxMembersInGuild, FunnyFormatter.formatOnce(messages.inviteAmountJoin,
                "{AMOUNT}", config.maxMembersInGuild));

        if (!SimpleEventHandler.handle(new GuildMemberAcceptInviteEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        guildInvitationList.expireInvitation(guild, user);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        guild.addMember(user);
        user.setGuild(guild);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        this.concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(individualPrefixManager, user.getName()));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        user.sendMessage(formatter.format(messages.joinToMember));
        broadcastMessage(formatter.format(messages.broadcastJoin));

        guild.getOwner().sendMessage(formatter.format(messages.joinToOwner));
    }

}
