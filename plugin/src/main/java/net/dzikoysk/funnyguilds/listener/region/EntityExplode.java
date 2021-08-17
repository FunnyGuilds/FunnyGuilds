package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EntityExplode implements Listener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void preNormalExplosionHandler(EntityExplodeEvent event) {
        List<Block> explodedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        Map<Material, Double> explosiveMaterials = config.explodeMaterials;

        List<Block> blocksInSphere = SpaceUtils.sphereBlocks(
                explodeLocation,
                config.explodeRadius,
                config.explodeRadius,
                0,
                false,
                true
        );

        Entity explosionEntity = event.getEntity();
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();

        if (config.explodeShouldAffectOnlyGuild) {
            explodedBlocks.removeIf(block -> {
                Region region = RegionUtils.getAt(block.getLocation());

                return (region == null || region.getGuild() == null) && block.getType() != Material.TNT;
            });

            blocksInSphere.removeIf(block -> {
                Region region = RegionUtils.getAt(block.getLocation());

                return region == null || region.getGuild() == null;
            });
        }

        Region region = RegionUtils.getAt(explodeLocation);

        if (region != null) {
            Guild guild = region.getGuild();

            if (config.warTntProtection && ! guild.canBeAttacked()) {
                event.setCancelled(true);

                if (explosionEntity instanceof TNTPrimed) {
                    TNTPrimed entityTnt = (TNTPrimed) explosionEntity;
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        Player explosionPlayer = (Player) explosionSource;
                        explosionPlayer.sendMessage(messages.regionExplosionHasProtection);
                    }
                }

                return;
            }

            Location guildHeartLocation = region.getHeart();
            explodedBlocks.removeIf(block -> block.getLocation().equals(guildHeartLocation));
            blocksInSphere.removeIf(block -> block.getLocation().equals(guildHeartLocation));

            for (User user : guild.getMembers()) {
                Player player = user.getPlayer();

                if (player != null && ! informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown)) {
                    player.sendMessage(FunnyGuilds.getInstance().getMessageConfiguration().regionExplode.replace("{TIME}", Integer.toString(config.regionExplode)));
                }
            }
        }

        if (config.warTntProtection) {
            boolean anyRemoved = explodedBlocks.removeIf(block -> {
                Region regionAtExplosion = RegionUtils.getAt(block.getLocation());
                return regionAtExplosion != null && regionAtExplosion.getGuild() != null && ! regionAtExplosion.getGuild().canBeAttacked();
            }) || blocksInSphere.removeIf(block -> {
                Region regionAtExplosion = RegionUtils.getAt(block.getLocation());
                return regionAtExplosion != null && regionAtExplosion.getGuild() != null && ! regionAtExplosion.getGuild().canBeAttacked();
            });

            if (anyRemoved) {
                if (explosionEntity instanceof TNTPrimed) {
                    TNTPrimed entityTnt = (TNTPrimed) explosionEntity;
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        Player explosionPlayer = (Player) explosionSource;
                        explosionPlayer.sendMessage(messages.regionExplosionHasProtection);
                    }
                }
            }
        }

        List<Block> additionalExplodedBlocks = new ArrayList<>();

        for (Block block : blocksInSphere) {
            if (block.getType() == Material.TNT) {
                // We want to preserve TNT chain explosions, see GH-1414.
                continue;
            }

            Material material = block.getType();
            Double explodeChance = explosiveMaterials.get(material);

            if (explodeChance == null) {
                if (! config.allMaterialsAreExplosive) {
                    continue;
                }

                explodeChance = config.defaultExplodeChance;
            }

            if (SpaceUtils.chance(explodeChance)) {
                additionalExplodedBlocks.add(block);
            }
        }

        if (! SimpleEventHandler.handle(new GuildEntityExplodeEvent(FunnyEvent.EventCause.UNKNOWN, additionalExplodedBlocks))) {
            event.setCancelled(true);
            return;
        }

        additionalExplodedBlocks.stream()
                .filter(block -> ! explodedBlocks.contains(block))
                .forEach(explodedBlocks::add);
    }
}
