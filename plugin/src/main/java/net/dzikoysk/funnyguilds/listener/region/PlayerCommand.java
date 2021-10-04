package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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

        Region region = RegionUtils.getAt(player.getLocation());

        if (region == null) {
            return;
        }

        Guild guild = region.getGuild();

        this.userManager.findByPlayer(player).peek(user -> {
            if (guild.getMembers().contains(user)) {
                return;
            }

            event.setCancelled(true);
            player.sendMessage(messages.regionCommand);
        });
    }

}
