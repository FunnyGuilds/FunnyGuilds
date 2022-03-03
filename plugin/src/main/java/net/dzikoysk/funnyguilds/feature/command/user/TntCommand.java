package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.LocalTime;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;
import panda.utilities.StringUtils;

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
        String message = messages.tntInfo;

        boolean isWithinTimeframe = config.tntProtection.time.passingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        message = StringUtils.replace(message, "{PROTECTION_START}", config.tntProtection.time.startTime.getFormattedTime());
        message = StringUtils.replace(message, "{PROTECTION_END}", config.tntProtection.time.endTime.getFormattedTime());

        this.sendMessage(sender, (message));
        this.sendMessage(sender, (isWithinTimeframe ? messages.tntNowDisabled : messages.tntNowEnabled));
    }

}
