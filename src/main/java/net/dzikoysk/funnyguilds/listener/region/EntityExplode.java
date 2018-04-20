package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EntityExplode implements Listener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> destroyedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        PluginConfig pluginConfiguration = Settings.getConfig();

        List<Location> sphere = SpaceUtils.sphere(explodeLocation, pluginConfiguration.explodeRadius, pluginConfiguration.explodeRadius, false, true, 0);
        Map<Material, Double> materials = pluginConfiguration.explodeMaterials;

        destroyedBlocks.removeIf(blocks -> {
            Region region = RegionUtils.getAt(blocks.getLocation());
            return region != null && region.getGuild() != null && !region.getGuild().canBeAttacked();
        });

        Region region = RegionUtils.getAt(explodeLocation);

        if (region != null) {
            Guild guild = region.getGuild();

            if (pluginConfiguration.guildTNTProtectionEnabled) {
                LocalTime start = pluginConfiguration.guildTNTProtectionStartTime;
                LocalTime end = pluginConfiguration.guildTNTProtectionEndTime;
                LocalTime now = LocalDateTime.now().toLocalTime();
                
                boolean or = pluginConfiguration.guildTNTProtectionOrMode;
                
                if (or ? now.isAfter(start) || now.isBefore(end) : now.isAfter(start) && now.isBefore(end)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (pluginConfiguration.warTntProtection & !guild.canBeAttacked()) {
                event.setCancelled(true);
                return;
            }

            Location protect = region.getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation();

            destroyedBlocks.removeIf(block -> block.getLocation().equals(protect));

            guild.setBuild(System.currentTimeMillis() + Settings.getConfig().regionExplode * 1000L);
            for (User user : guild.getMembers()) {
                Player player = user.getPlayer();
                if (player != null) {
                    if (informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, pluginConfiguration.infoPlayerCooldown)) {
                        player.sendMessage(Messages.getInstance().regionExplode.replace("{TIME}", Integer.toString(Settings.getConfig().regionExplode)));
                    }
                }
            }
        }

        for (Location l : sphere) {
            Material material = l.getBlock().getType();
            if (!materials.containsKey(material)) {
                continue;
            }
            
            if (material == Material.WATER || material == Material.LAVA) {
                if (SpaceUtils.chance(materials.get(material))) {
                    l.getBlock().setType(Material.AIR);
                }
            } else {
                if (SpaceUtils.chance(materials.get(material))) {
                    l.getBlock().breakNaturally();
                }
            }
        }
    }

}
