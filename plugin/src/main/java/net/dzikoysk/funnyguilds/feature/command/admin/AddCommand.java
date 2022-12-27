package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.concurrency.requests.nametag.NameTagGlobalUpdateUserRequest;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class AddCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.add.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3 online-players:3",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, this.messages.generalNoTagGiven);
        when(!this.guildManager.tagExists(args[0]), this.messages.generalNoGuildFound);
        when(args.length < 2, this.messages.generalNoNickGiven);

        User userToAdd = UserValidation.requireUserByName(args[1]);
        when(userToAdd.hasGuild(), this.messages.generalUserHasGuild);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(AdminUtils.getCause(admin), admin, guild, userToAdd))) {
            return;
        }

        guild.addMember(userToAdd);
        userToAdd.setGuild(guild);
        this.concurrencyManager.postRequests(new NameTagGlobalUpdateUserRequest(this.plugin, userToAdd));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", userToAdd.getName());

        userToAdd.sendMessage(formatter.format(this.messages.joinToMember));
        guild.getOwner().sendMessage(formatter.format(this.messages.joinToOwner));
        this.broadcastMessage(formatter.format(this.messages.broadcastJoin));
    }

}
