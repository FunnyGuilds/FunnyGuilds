package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.command.UserValidation;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.text.Formatter;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class AddCommand {

    @FunnyCommand(
        name = "${admin.add.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (!GuildUtils.tagExists(args[0]), messages.generalNoGuildFound);
        when (args.length < 2, messages.generalNoNickGiven);
        
        User userToAdd = UserValidation.requireUserByName(args[1]);
        when (userToAdd.hasGuild(), messages.generalUserHasGuild);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        User admin = AdminUtils.getAdminUser(sender);

        if (!SimpleEventHandler.handle(new GuildMemberJoinEvent(AdminUtils.getCause(admin), admin, guild, userToAdd))) {
            return;
        }
        
        guild.addMember(userToAdd);
        userToAdd.setGuild(guild);
        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(new PrefixGlobalAddPlayerRequest(userToAdd.getName()));

        Formatter formatter = new Formatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", userToAdd.getName());

        userToAdd.sendMessage(formatter.format(messages.joinToMember));
        guild.getOwner().sendMessage(formatter.format(messages.joinToOwner));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastJoin));
    }

}
