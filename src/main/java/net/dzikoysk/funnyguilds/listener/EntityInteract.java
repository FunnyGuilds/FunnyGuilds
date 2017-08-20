package net.dzikoysk.funnyguilds.listener;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.ExcInfo;
import net.dzikoysk.funnyguilds.command.ExcPlayer;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.Cooldown;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.concurrent.TimeUnit;

public class EntityInteract implements Listener {
    private final Cooldown<Player> playerInfoCooldown = new Cooldown<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Player) {
            final PluginConfig config = Settings.getConfig();
            Player clicked = (Player) entity;

            if (!config.infoPlayerEnabled || (config.infoPlayerSneaking && !event.getPlayer().isSneaking()) || playerInfoCooldown.cooldown(event.getPlayer(), TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                return;
            }
            new ExcPlayer().execute(event.getPlayer(), new String[]{ clicked.getName() });
        }
        else if (entity instanceof EnderCrystal) {
            EnderCrystal ec = (EnderCrystal) entity;
            Region region = RegionUtils.getAt(ec.getLocation());
            if (region == null) {
                return;
            }
            event.setCancelled(true);
            if (region.getCenter().getBlock().getRelative(BlockFace.UP).getLocation().toVector().equals(ec.getLocation().getBlock().getLocation().toVector())) {
                Guild guild = region.getGuild();
                if (guild == null) {
                    return;
                }
                new ExcInfo().execute(event.getPlayer(), new String[]{ guild.getTag() });
            }
        }
    }

}
