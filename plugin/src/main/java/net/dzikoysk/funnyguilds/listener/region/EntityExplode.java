package net.dzikoysk.funnyguilds.listener.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import panda.std.Option;

public class EntityExplode extends AbstractFunnyListener {

    private final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void preNormalExplosionHandler(EntityExplodeEvent event) {
        List<Block> explodedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
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

        explodedBlocks.removeIf(block -> {
            int height = block.getLocation().getBlockY();

            return height < config.tntProtection.explode.minHeight || height > config.tntProtection.explode.maxHeight;
        });

        blocksInSphere.removeIf(block -> {
            int height = block.getLocation().getBlockY();

            return height < config.tntProtection.explode.minHeight || height > config.tntProtection.explode.maxHeight;
        });

        if (config.explodeShouldAffectOnlyGuild) {
            explodedBlocks.removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation())
                    .filterNot(region -> region.getGuild() == null)
                    .filter(region -> block.getType() != Material.TNT)
                    .isEmpty());

            blocksInSphere.removeIf(block -> this.regionManager.findRegionAtLocation(block.getLocation())
                    .filterNot(region -> region.getGuild() == null)
                    .isEmpty());
        }

        Option<Region> regionOption = this.regionManager.findRegionAtLocation(explodeLocation);

        if (regionOption.isPresent()) {
            Region region = regionOption.get();
            Guild guild = region.getGuild();

            if (config.warTntProtection && !guild.canBeAttacked()) {
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
                user.getPlayer()
                        .filter(player -> !informationMessageCooldowns.cooldown(player, TimeUnit.SECONDS, config.infoPlayerCooldown))
                        .peek(player -> player.sendMessage(messages.regionExplode.replace("{TIME}", Integer.toString(config.regionExplode))));
            }
        }

        if (config.warTntProtection) {
            boolean anyRemoved = explodedBlocks.removeIf(block ->
                    this.regionManager.findRegionAtLocation(block.getLocation())
                            .map(Region::getGuild)
                            .filter(guild -> !guild.canBeAttacked())
                            .isPresent()) ||
                    blocksInSphere.removeIf(block ->
                            this.regionManager.findRegionAtLocation(block.getLocation())
                                    .map(Region::getGuild)
                                    .filter(guild -> !guild.canBeAttacked())
                                    .isPresent());

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
                if (!config.allMaterialsAreExplosive) {
                    continue;
                }

                explodeChance = config.defaultExplodeChance;
            }

            if (SpaceUtils.chance(explodeChance)) {
                additionalExplodedBlocks.add(block);
            }
        }

        if (!SimpleEventHandler.handle(new GuildEntityExplodeEvent(FunnyEvent.EventCause.UNKNOWN, additionalExplodedBlocks))) {
            event.setCancelled(true);
            return;
        }

        additionalExplodedBlocks.stream()
                .filter(block -> !explodedBlocks.contains(block))
                .forEach(explodedBlocks::add);
    }
}
