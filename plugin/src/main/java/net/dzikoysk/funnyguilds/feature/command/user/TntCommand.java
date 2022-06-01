package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.LocalTime;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import org.bukkit.command.CommandSender;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class TntCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.tnt.name}",
            description = "${user.tnt.description}",
            aliases = "${user.tnt.aliases}",
            permission = "funnyguilds.tnt",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender) {
        when(!config.tntProtection.time.enabled, messages.tntProtectDisable);

        LocalTime now = LocalTime.now();
        LocalTime start = config.tntProtection.time.startTime.getTime();
        LocalTime end = config.tntProtection.time.endTime.getTime();

        boolean isWithinTimeframe = config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PROTECTION_START}", config.tntProtection.time.startTime.getFormattedTime())
                .register("{PROTECTION_END}", config.tntProtection.time.endTime.getFormattedTime());

        sendMessage(sender, formatter.format(messages.tntInfo));
        sendMessage(sender, isWithinTimeframe ? messages.tntNowDisabled : messages.tntNowEnabled);
    }

}
