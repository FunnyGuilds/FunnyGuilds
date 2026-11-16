package net.dzikoysk.funnyguilds.feature.validity;

import java.util.Optional;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public final class ValidityUtils {

    private ValidityUtils() {
    }

    public static void broadcast(@Nullable Guild guild) {
        if (guild == null) {
            return;
        }

        MessageService messageService = FunnyGuilds.getInstance().getMessageService();
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{GUILD}", guild.getName());

        Optional<Region> regionOption = guild.getRegion();
        boolean hasCenter = regionOption.isPresent() && regionOption.orElseThrow().getCenter() != null;

        messageService.getMessage(config -> config.broadcastValidity)
                .all()
                .with(formatter)
                .with(CommandSender.class, receiver -> {
                    FunnyFormatter cordFormatter = new FunnyFormatter();
                    if (hasCenter) {
                        Location center = regionOption.orElseThrow().getCenter();
                        cordFormatter.register("{X}", center.getBlockX());
                        cordFormatter.register("{Y}", center.getBlockY());
                        cordFormatter.register("{Z}", center.getBlockZ());
                    }
                    else {
                        Component noInformation = messageService.get(receiver, config -> config.noInformation);
                        cordFormatter.register("{X}", noInformation);
                        cordFormatter.register("{Y}", noInformation);
                        cordFormatter.register("{Z}", noInformation);
                    }
                    return cordFormatter;
                })
                .send();
    }

}
