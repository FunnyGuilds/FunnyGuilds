package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class AddCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.add.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, messages.generalNoTagGiven);
        when(!guildManager.tagExists(args[0]), messages.generalNoGuildFound);
        when(args.length < 2, messages.generalNoNickGiven);

        User userToAdd = UserValidation.requireUserByName(args[1]);
        when(userToAdd.hasGuild(), messages.generalUserHasGuild);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(AdminUtils.getCause(admin), admin, guild, userToAdd))) {
            return;
        }

        guild.addMember(userToAdd);
        userToAdd.setGuild(guild);
        this.concurrencyManager.postRequests(new PrefixGlobalAddPlayerRequest(userToAdd.getName()));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", userToAdd.getName());

        userToAdd.sendMessage(formatter.format(messages.joinToMember));
        guild.getOwnerOption()
                .peek(owner -> owner.sendMessage(formatter.format(messages.joinToOwner)));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastJoin));
    }

}
