package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import org.bukkit.command.CommandSender;

import java.time.LocalTime;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class TntCommand {

    @FunnyCommand(
        name = "${user.tnt.name}",
        description = "${user.tnt.description}",
        aliases = "${user.tnt.aliases}",
        acceptsExceeded = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, CommandSender sender, String[] args) {
        when (!config.guildTNTProtectionEnabled, messages.tntProtectDisable);

        LocalTime now = LocalTime.now();
        LocalTime start = config.guildTNTProtectionStartTime;
        LocalTime end = config.guildTNTProtectionEndTime;

        boolean isWithinTimeframe = config.guildTNTProtectionPassingMidnight
                ? now.isAfter(start) || now.isBefore(end)
                : now.isAfter(start) && now.isBefore(end);

        sender.sendMessage(messages.tntInfo.replace("{FROM}", config.guildTNTProtectionStartTime_).replace("{TO}", config.guildTNTProtectionEndTime_));
        sender.sendMessage(isWithinTimeframe ? messages.tntNowDisabled : messages.tntNowEnabled);
    }

}
