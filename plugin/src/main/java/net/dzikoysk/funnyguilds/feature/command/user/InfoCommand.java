package net.dzikoysk.funnyguilds.feature.command.user;

import java.util.Optional;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.shared.Optionals;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        String tag = Optionals.when(args.length > 0, () -> args[0])
                .or(() -> Optional.ofNullable(sender)
                        .filter(Player.class::isInstance)
                        .map(Player.class::cast)
                        .flatMap(userManager::findByPlayer)
                        .filter(User::hasGuild)
                        .flatMap(User::getGuild)
                        .map(Guild::getTag))
                .orElseThrow(() -> new InternalValidationException(config -> config.infoTag));

        Guild guild = GuildValidation.requireGuildByTag(tag);
        this.messageService.getMessage(config -> config.infoList)
                .receiver(sender)
                .with(this.guildPlaceholdersService.asReplaceable(guild))
                .send();
    }

}
