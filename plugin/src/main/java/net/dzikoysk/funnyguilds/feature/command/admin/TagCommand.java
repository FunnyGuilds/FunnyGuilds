package net.dzikoysk.funnyguilds.feature.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildTagChangeEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class TagCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.tag.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 2, messages.generalNoTagGiven);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        String tag = args[1];
        when(guildManager.tagExists(tag), messages.createTagExists);

        User admin = AdminUtils.getAdminUser(sender);

        String oldTag = guild.getTag();
        if (!SimpleEventHandler.handle(new GuildPreTagChangeEvent(AdminUtils.getCause(admin), admin, guild, oldTag, tag))) {
            return;
        }

        guild.setTag(tag);

        sender.sendMessage(messages.adminTagChanged
                .replace("{OLD_TAG}", oldTag)
                .replace("{TAG}", guild.getTag()));

        SimpleEventHandler.handle(new GuildTagChangeEvent(AdminUtils.getCause(admin), admin, guild, oldTag, tag));
    }

}
