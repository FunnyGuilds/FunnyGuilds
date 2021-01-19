package net.dzikoysk.funnyguilds.command.admin;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreTagChangeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildTagChangeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TagCommand {

    @FunnyCommand(
        name = "${admin.tag.name}",
        permission = "funnyguilds.admin",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (args.length < 2) {
            sender.sendMessage(messages.generalNoTagGiven);
            return;
        }

        Guild guild = GuildUtils.getByTag(args[0]);

        if (guild == null) {
            sender.sendMessage(messages.generalNoGuildFound);
            return;
        }

        String tag = args[1];

        if (GuildUtils.tagExists(tag)) {
            sender.sendMessage(messages.createTagExists);
            return;
        }

        User admin = (sender instanceof Player)
                ? User.get(sender.getName())
                : null;

        FunnyEvent.EventCause eventCause = admin == null
                ? FunnyEvent.EventCause.CONSOLE
                : FunnyEvent.EventCause.ADMIN;

        if (!SimpleEventHandler.handle(new GuildPreTagChangeEvent(eventCause, admin, guild, tag))) {
            return;
        }

        String oldTag = guild.getTag();
        guild.setTag(tag);

        sender.sendMessage(messages.adminTagChanged
                .replace("{OLD_TAG}", oldTag)
                .replace("{TAG}", guild.getTag()));

        SimpleEventHandler.handle(new GuildTagChangeEvent(eventCause, admin, guild, oldTag, tag));
    }

}
