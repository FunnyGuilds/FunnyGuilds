package net.dzikoysk.funnyguilds.feature.command.user;

import com.google.common.collect.Lists;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberInviteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberRevokeInviteEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.CanManage;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.feature.invitation.guild.GuildInvitationList;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Inject;

import java.util.List;
import java.util.stream.Collectors;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class InviteCommand extends AbstractFunnyCommand {

    @Inject
    public GuildInvitationList guildInvitationList;

    @FunnyCommand(
            name = "${user.invite.name}",
            description = "${user.invite.description}",
            aliases = "${user.invite.aliases}",
            permission = "funnyguilds.invite",
            completer = "invite-command",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(@CanManage User deputy, Player sender, Guild guild, String[] args) {
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{AMOUNT}", this.config.maxMembersInGuild)
                .register("{OWNER}", deputy.getName())
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag());

        when(args.length < 1, this.messages.generalNoNickGiven);
        when(guild.getMembers().size() >= this.config.maxMembersInGuild, formatter.format(this.messages.inviteAmount));

        if (args[0].equals(this.config.inviteCommandAllArgument)) {

            Double range;

            try {
                range = Math.pow(Double.parseDouble(args[1]), 2);
            } catch (Exception exception) {
                range = this.config.inviteCommandAllMaxRange;
            }

            Double finalRange = range;
            List<Player> nearbyPlayers = Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(player -> finalRange >= player.getLocation().distanceSquared(sender.getLocation()))
                    .filter(player -> player != sender)
                    .collect(Collectors.toList());

            when(guild.getMembers().size() + nearbyPlayers.size() > this.config.maxMembersInGuild, formatter.format(this.messages.inviteAmount));
            when(nearbyPlayers.size() == 0, this.messages.inviteNoOneIsNearby);


            for (Player player : nearbyPlayers) {
                User invitedUser = this.userManager.findByPlayer(player).get();

                if (invitedUser.isVanished()) {
                    continue;
                }

                this.inviteUserToGuild(deputy, guild, invitedUser, formatter);
            }

            return;

        }

        this.inviteUserToGuild(deputy, guild, UserValidation.requireUserByName(args[0]), formatter);
    }

    private void inviteUserToGuild(User deputy, Guild guild, User invitedUser, FunnyFormatter formatter) {

        if (this.guildInvitationList.hasInvitation(guild, invitedUser)) {
            if (!SimpleEventHandler.handle(new GuildMemberRevokeInviteEvent(EventCause.USER, deputy, guild, invitedUser))) {
                return;
            }

            deputy.sendMessage(this.messages.inviteCancelled.replace("{USER}", invitedUser.getName()));
            invitedUser.sendMessage(formatter.format(this.messages.inviteCancelledToInvited));

            this.guildInvitationList.expireInvitation(guild, invitedUser);
            return;
        }

        when(invitedUser.hasGuild(), this.messages.generalUserHasGuild);

        if (!SimpleEventHandler.handle(new GuildMemberInviteEvent(EventCause.USER, deputy, guild, invitedUser))) {
            return;
        }

        this.guildInvitationList.createInvitation(guild, invitedUser);

        deputy.sendMessage(FunnyFormatter.format(this.messages.inviteToOwner, "{PLAYER}", invitedUser.getName()));
        invitedUser.sendMessage(formatter.format(this.messages.inviteToInvited));
    }

    public static final class InviteCommandCompleter implements Completer {

        private final PluginConfiguration configuration;
        private final UserManager userManager;

        public InviteCommandCompleter(PluginConfiguration configuration, UserManager userManager) {
            this.configuration = configuration;
            this.userManager = userManager;
        }


        @Override
        public String getName() {
            return "invite-command";
        }

        @Override
        public List<String> apply(Context context, String prefix, Integer limit) {
            String[] args = context.getArguments();
            CommandSender sender = context.getCommandSender();

            if (args.length == 1) {

                List<String> toReturn = Bukkit.getOnlinePlayers().stream()
                        .filter(it -> !it.equals(sender))
                        .filter(it -> !userManager.findByPlayer(it).get().isVanished())
                        .map(HumanEntity::getName)
                        .collect(Collectors.toList());
                toReturn.add(configuration.inviteCommandAllArgument);

                return toReturn;
            } else {
                return Lists.newArrayList("");
            }
        }
    }
}
