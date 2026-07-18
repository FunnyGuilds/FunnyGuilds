package net.dzikoysk.funnyguilds.feature.command.admin;

import java.time.Duration;
import java.time.Instant;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.GuildValidation;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.TimeUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import org.bukkit.command.CommandSender;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class TntAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.tnt.name}",
            permission = "funnyguilds.admin",
            completer = "guilds:3",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.generalNoTagGiven);
        when(args.length < 2, config -> config.adminNoTntTimeGive);

        Guild guild = GuildValidation.requireGuildByTag(args[0]);

        Duration duration = TimeUtils.parseTime(args[1]);
        when(duration.isZero(), config -> config.adminInvalidTntTime);

        Instant bypassEnd = Instant.now().plus(duration);
        guild.setTntProtectionBypass(bypassEnd);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{TAG}", guild.getTag())
                .register("{TIME}", args[1]);

        this.messageService.getMessage(config -> config.adminTntSetSuccessfully)
                .receiver(sender)
                .with(formatter)
                .send();
    }

}
