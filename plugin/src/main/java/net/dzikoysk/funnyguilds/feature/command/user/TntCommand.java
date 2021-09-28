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
        when (!this.pluginConfig.guildTNTProtectionEnabled, this.messageConfig.tntProtectDisable);

        LocalTime now = LocalTime.now();
        LocalTime start = this.pluginConfig.guildTNTProtectionStartTime;
        LocalTime end = this.pluginConfig.guildTNTProtectionEndTime;
        String message = this.messageConfig.tntInfo;

        boolean isWithinTimeframe = this.pluginConfig.guildTNTProtectionPassingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        message = StringUtils.replace(message, "{PROTECTION_START}", this.pluginConfig.guildTNTProtectionStartTime_);
        message = StringUtils.replace(message, "{PROTECTION_END}", this.pluginConfig.guildTNTProtectionEndTime_);

        sender.sendMessage(message);
        sender.sendMessage(isWithinTimeframe ? this.messageConfig.tntNowDisabled : this.messageConfig.tntNowEnabled);
    }

}
