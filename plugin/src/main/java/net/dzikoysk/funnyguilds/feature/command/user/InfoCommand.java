package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Formatter;

@FunnyComponent
public final class InfoCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.info.name}",
            description = "${user.info.description}",
            aliases = "${user.info.aliases}",
            permission = "funnyguilds.info",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        UserManager userManager = this.userManager;
        String tag = Option.when(args.length > 0, () -> args[0])
                .orElse(Option.of(sender)
                        .is(Player.class)
                        .flatMap(userManager::findByPlayer)
                        .filter(User::hasGuild)
                        .flatMap(User::getGuild)
                        .map(Guild::getTag))
                .orThrow(() -> new ValidationException(messages.infoTag));

        Guild guild = GuildValidation.requireGuildByTag(tag);

        Formatter formatter = placeholdersService.getGuildPlaceholders()
                .toVariablesFormatter(guild);

        PandaStream.of(messages.infoList)
                .map(formatter::format)
                .map(line -> placeholdersService.getGuildAlliesEnemiesPlaceholders().formatVariables(line, guild))
                .map(line -> placeholdersService.getGuildMembersPlaceholders()
                        .toVariablesFormatter(Pair.of(ChatUtils.getLastColorBefore(line, "{MEMBERS}"), guild))
                        .format(line))
                .forEach(sender::sendMessage);
    }

}
