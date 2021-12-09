package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import panda.std.Option;

public class PlayerCommand extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("funnyguilds.admin")) {
            return;
        }

        String[] commandElements = event.getMessage().split("\\s+");

        if (commandElements.length == 0) {
            return;
        }

        String command = commandElements[0];

        for (String blockedCommand : config.regionCommands) {
            if (("/" + blockedCommand).equalsIgnoreCase(command)) {
                command = null;
                break;
            }
        }

        if (command != null) {
            return;
        }

        Option<Region> regionOption = this.regionManager.findRegionAtLocation(player.getLocation());
        if (regionOption.isEmpty()) {
            return;
        }
        Guild guild = regionOption.get().getGuild();

        this.userManager.findByPlayer(player)
                .filterNot(user -> guild.getMembers().contains(user))
                .peek(user -> {
                    event.setCancelled(true);
                    player.sendMessage(messages.regionCommand);
                });
    }

}
