package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import org.bukkit.command.CommandSender;
import panda.utilities.StringUtils;

import java.time.LocalTime;
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
        when (!this.pluginConfiguration.guildTNTProtectionEnabled, this.messageConfiguration.tntProtectDisable);

        LocalTime now = LocalTime.now();
        LocalTime start = this.pluginConfiguration.guildTNTProtectionStartTime;
        LocalTime end = this.pluginConfiguration.guildTNTProtectionEndTime;
        String message = this.messageConfiguration.tntInfo;

        boolean isWithinTimeframe = this.pluginConfiguration.guildTNTProtectionPassingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        message = StringUtils.replace(message, "{PROTECTION_START}", this.pluginConfiguration.guildTNTProtectionStartTime_);
        message = StringUtils.replace(message, "{PROTECTION_END}", this.pluginConfiguration.guildTNTProtectionEndTime_);

        sender.sendMessage(message);
        sender.sendMessage(isWithinTimeframe ? this.messageConfiguration.tntNowDisabled : this.messageConfiguration.tntNowEnabled);
    }

}
