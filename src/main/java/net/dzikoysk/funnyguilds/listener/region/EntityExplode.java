package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildRegionLeaveEvent;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EntityExplode implements Listener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        List<Block> destroyedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        List<Location> blockSphereLocations = SpaceUtils.sphere(
                explodeLocation,
                config.explodeRadius,
                config.explodeRadius,
                false,
                true,
                0
        );

        Map<Material, Double> explosiveMaterials = config.explodeMaterials;

        if (config.explodeShouldAffectOnlyGuild) {
            destroyedBlocks.removeIf(block -> {
                Region region = RegionUtils.getAt(block.getLocation());

                return region == null || region.getGuild() == null;
            });

            blockSphereLocations.removeIf(location -> {
                Region region = RegionUtils.getAt(location);

                return region == null || region.getGuild() == null;
            });
        }

        destroyedBlocks.removeIf(block -> {
            Region region = RegionUtils.getAt(block.getLocation());
            return region != null && region.getGuild() != null && ! region.getGuild().canBeAttacked();
        });

        Region region = RegionUtils.getAt(explodeLocation);

        if (region != null) {
            Guild guild = region.getGuild();

            if (config.guildTNTProtectionEnabled) {
                LocalTime now = LocalDateTime.now().toLocalTime();

                boolean afterStart = now.isAfter(config.guildTNTProtectionStartTime);
                boolean beforeEnd = now.isBefore(config.guildTNTProtectionEndTime);

                if (config.guildTNTProtectionOrMode ? afterStart || beforeEnd : afterStart && beforeEnd) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (config.warTntProtection && ! guild.canBeAttacked()) {
                event.setCancelled(true);
                return;
            }

            Location protect = region.getHeart();
            destroyedBlocks.removeIf(block -> block.getLocation().equals(protect));

            guild.setBuild(System.currentTimeMillis() + FunnyGuilds.getInstance().getPluginConfiguration().regionExplode * 1000L);

            for (User user : guild.getMembers()) {
                Player player = user.getPlayer();
                if (player != null) {
                    if (informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                        player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionExplode.replace("{TIME}", Integer.toString(FunnyGuilds.getInstance().getPluginConfiguration().regionExplode)));
                    }
                }
            }
        }

        List<Block> affectedBlocks = new ArrayList<>();

        for (Location blockLocation : blockSphereLocations) {
            Material material = blockLocation.getBlock().getType();

            Double explodeChance = explosiveMaterials.get(material);

            if (explodeChance == null) {
                if (config.allMaterialsAreExplosive) {
                    explodeChance = config.defaultExplodeChance;
                }
                else {
                    continue;
                }
            }

            affectedBlocks.add(blockLocation.getBlock());
            if (material == Material.WATER || material == Material.LAVA) {
                if (SpaceUtils.chance(explodeChance)) {
                    blockLocation.getBlock().setType(Material.AIR);
                }
            }
            else {
                if (SpaceUtils.chance(explodeChance)) {
                    blockLocation.getBlock().breakNaturally();
                }
            }
        }

        if (!SimpleEventHandler.handle(new GuildEntityExplodeEvent(affectedBlocks))) {
            event.setCancelled(true);
        }
    }

}
