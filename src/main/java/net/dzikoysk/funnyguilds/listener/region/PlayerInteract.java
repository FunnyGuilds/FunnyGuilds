package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.system.security.SecuritySystem;
import net.dzikoysk.funnyguilds.system.war.WarSystem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class PlayerInteract implements Listener {

    private final ExcInfo infoExecutor = new ExcInfo();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Action eventAction = event.getAction();
        Player player = event.getPlayer();

        if (eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.LEFT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            Region region = RegionUtils.getAt(clicked.getLocation());

            if (region != null) {
                Block heart = region.getCenter().getBlock().getRelative(BlockFace.DOWN);

                if (clicked.equals(heart)) {
                    if (heart.getType() == Material.DRAGON_EGG) {
                        event.setCancelled(true);
                    }

                    Guild guild = region.getGuild();
                    if (SecuritySystem.getSecurity().checkPlayer(player, guild)) {
                        return;
                    }

                    event.setCancelled(true);

                    if (eventAction == Action.LEFT_CLICK_BLOCK) {
                        WarSystem.getInstance().attack(player, guild);
                    }
                    else {
                        if(!config.informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                            infoExecutor.execute(player, new String[]{guild.getTag()});
                        }

                    }
                }
                else if (eventAction == Action.RIGHT_CLICK_BLOCK) {
                    Guild guild = region.getGuild();
                    if (guild == null || guild.getName() == null) {
                        return;
                    }

                    User user = User.get(player);
                    if (!guild.getMembers().contains(user)) {
                        event.setCancelled(config.blockedInteract.contains(clicked.getType()) && !player.hasPermission("funnyguilds.admin.interact"));
                    }
                }
            }
        }
    }

}
