package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildExtendValidityEvent;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import org.bukkit.command.CommandSender;

import java.util.Date;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class ValidityAdminCommand {

    @FunnyCommand(
        name = "${admin.validity.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, PluginConfiguration config, CommandSender sender, String[] args) {
        when (args.length < 1, messages.generalNoTagGiven);
        when (args.length < 2, messages.adminNoValidityTimeGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);
        when (guild.isBanned(), messages.adminGuildBanned);

        long time = TimeUtils.parseTime(args[1]);
        if (time < 1) {
            sender.sendMessage(messages.adminTimeError);
            return;
        }

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildExtendValidityEvent(AdminUtils.getCause(admin), admin, guild, time))) {
            return;
        }

        long validity = guild.getValidity();
        if (validity == 0) {
            validity = System.currentTimeMillis();
        }

        validity += time;
        guild.setValidity(validity);

        String date = config.dateFormat.format(new Date(validity));
        sender.sendMessage(messages.adminNewValidity.replace("{GUILD}", guild.getName()).replace("{VALIDITY}", date));
    }

}
