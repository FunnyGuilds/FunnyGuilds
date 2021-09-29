package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Pair;
import panda.utilities.text.Formatter;

@FunnyComponent
public final class InfoCommand {

    @FunnyCommand(
        name = "${user.info.name}",
        description = "${user.info.description}",
        aliases = "${user.info.aliases}",
        permission = "funnyguilds.info",
        completer = "guilds:3",
        acceptsExceeded = true
    )
    public void execute(FunnyGuilds plugin, MessageConfiguration messages, CommandSender sender, String[] args) {
        UserManager userManager = plugin.getUserManager();
        String tag = Option.when(args.length > 0, () -> args[0])
                .orElse(Option.of(sender)
                        .is(Player.class)
                        .flatMap(userManager::getUser)
                        .filter(User::hasGuild)
                        .map(User::getGuild)
                        .map(Guild::getTag))
                .orThrow(() -> new ValidationException(messages.infoTag));

        Guild guild = GuildValidation.requireGuildByTag(tag);
        Placeholders<Pair<String, Guild>> membersColorContext = Placeholders.GUILD_MEMBERS_COLOR_CONTEXT;
        Formatter formatter = Placeholders.GUILD_ALL
                .toFormatter(guild);

        for (String messageLine : messages.infoList) {
            messageLine = formatter.format(messageLine);
            String lastColor = ChatColor.getLastColors(messageLine.split("<online>")[0]);
            messageLine = membersColorContext.format(messageLine, Pair.of(lastColor, guild));

            sender.sendMessage(messageLine);
        }
    }

}
