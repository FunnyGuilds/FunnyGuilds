package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.GuildValidation;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildTagChangeEvent;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

public final class TagCommand {

    @FunnyCommand(
        name = "${admin.tag.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(MessageConfiguration messages, CommandSender sender, String[] args) {
        when (args.length < 2, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        String tag = args[1];
        when (GuildUtils.tagExists(tag), messages.createTagExists);

        User admin = AdminUtils.getAdminUser(sender);
        if (!SimpleEventHandler.handle(new GuildPreTagChangeEvent(AdminUtils.getCause(admin), admin, guild, tag))) {
            return;
        }

        String oldTag = guild.getTag();
        guild.setTag(tag);

        sender.sendMessage(messages.adminTagChanged
                .replace("{OLD_TAG}", oldTag)
                .replace("{TAG}", guild.getTag()));

        SimpleEventHandler.handle(new GuildTagChangeEvent(AdminUtils.getCause(admin), admin, guild, oldTag, tag));
    }

}
