package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.List;
import java.util.Set;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberAcceptInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.NameTagGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.panda_lang.utilities.inject.annotations.Inject;
import panda.std.stream.PandaStream;
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
        when(user.hasGuild(), config -> config.joinHasGuild);

        Set<GuildInvitation> invitations = this.guildInvitationList.getInvitationsFor(user);
        when(invitations.isEmpty(), config -> config.joinHasNotInvitation);

        if (args.length < 1) {
            String guildNames = FunnyStringUtils.join(this.guildInvitationList.getInvitationGuildNames(user), true);
            FunnyFormatter formatter = FunnyFormatter.of("{GUILDS}", guildNames);

            PandaStream.of(this.messages.joinInvitationList).forEach(line -> user.sendMessage(formatter.format(line)));
            return;
        }

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when(!this.guildInvitationList.hasInvitation(guild, user), config -> config.joinHasNotInvitationTo);

        List<ItemStack> requiredItems = this.config.joinItems;
        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.joinItems)) {
            return;
        }

        when(
                guild.getMembers().size() >= this.config.maxMembersInGuild,
                config -> config.inviteAmountJoin, FunnyFormatter.of("{AMOUNT}", this.config.maxMembersInGuild)
        );

        if (!SimpleEventHandler.handle(new GuildMemberAcceptInviteEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        this.guildInvitationList.expireInvitation(guild, user);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(EventCause.USER, user, guild, user))) {
            return;
        }

        guild.addMember(user);
        user.setGuild(guild);
        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));

        this.plugin.scheduleFunnyTasks(new NameTagGlobalUpdateUserSyncTask(this.plugin.getIndividualNameTagManager(), user));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", player.getName());

        this.messageService.getMessage(config -> config.joinToMember)
                .with(formatter)
                .receiver(player)
                .send();
        this.messageService.getMessage(config -> config.broadcastJoin)
                .with(formatter)
                .broadcast()
                .send();

        this.messageService.getMessage( config -> config.joinToOwner)
                .with(formatter)
                .receiver(guild.getOwner())
                .send();
    }

}
