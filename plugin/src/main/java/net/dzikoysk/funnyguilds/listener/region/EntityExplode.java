package net.dzikoysk.funnyguilds.listener.region;

import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.config.ExplodeMaterialsScope;
import net.dzikoysk.funnyguilds.event.FunnyEvent;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildEntityExplodeEvent;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplode extends AbstractFunnyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void preNormalExplosionHandler(EntityExplodeEvent event) {
        Entity explosionEntity = event.getEntity();

        List<Block> explodedBlocks = event.blockList();
        Location explodeLocation = event.getLocation();
        List<Block> blocksInSphere = SpaceUtils.sphereBlocks(
                explodeLocation,
                this.config.explodeRadius,
                this.config.explodeRadius,
                0,
                false,
                true
        );

        explodedBlocks.removeIf(block -> {
            int height = block.getLocation().getBlockY();
            return height < this.config.tntProtection.explode.minHeight || height > this.config.tntProtection.explode.maxHeight;
        });

        blocksInSphere.removeIf(block -> {
            int height = block.getLocation().getBlockY();
            return height < this.config.tntProtection.explode.minHeight || height > this.config.tntProtection.explode.maxHeight;
        });

        // Drop blocks destroyed by the vanilla explosion in scopes whose wildcard is 'none' (nothing but explicitly
        // listed materials should be destroyed there). TNT is preserved as well, keeping chain explosions intact.
        explodedBlocks.removeIf(block -> this.scopeFor(block).dropsVanillaBlocks());

        this.regionManager.findRegionAtLocation(explodeLocation).peek(region -> {
            Guild guild = region.getGuild();

            if (this.config.warTntProtection && !guild.canBeAttacked()) {
                event.setCancelled(true);

                if (explosionEntity instanceof TNTPrimed) {
                    TNTPrimed entityTnt = (TNTPrimed) explosionEntity;
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        this.messageService.getMessage(config -> config.regionExplosionHasProtection)
                                .receiver(explosionSource)
                                .send();
                    }
                }

                return;
            }

            region.getHeart().peek(heart -> {
                explodedBlocks.removeIf(block -> block.getLocation().equals(heart));
                blocksInSphere.removeIf(block -> block.getLocation().equals(heart));
            });
        });

        explodedBlocks.removeIf(block -> ProtectionSystem.isGuildHeartProtectedRegion(block.getLocation()));
        blocksInSphere.removeIf(block -> ProtectionSystem.isGuildHeartProtectedRegion(block.getLocation()));

        if (this.config.warTntProtection) {
            // Remove block if protected
            boolean anyBlockRemovedInSphere = blocksInSphere.removeIf(block ->
                    this.regionManager.findRegionAtLocation(block.getLocation())
                            .map(Region::getGuild)
                            .filterNot(Guild::canBeAttacked)
                            .isPresent());
            boolean anyBlockRemovedInExplosion = explodedBlocks.removeIf(block ->
                    this.regionManager.findRegionAtLocation(block.getLocation())
                            .map(Region::getGuild)
                            .filterNot(Guild::canBeAttacked)
                            .isPresent());

            if (anyBlockRemovedInSphere || anyBlockRemovedInExplosion) {
                if (explosionEntity instanceof TNTPrimed entityTnt) {
                    Entity explosionSource = entityTnt.getSource();

                    if (explosionSource instanceof Player) {
                        this.messageService.getMessage(config -> config.regionExplosionHasProtection)
                                .receiver(explosionSource)
                                .send();
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

            Double explodeChance = this.scopeFor(block).explosionChance(block.getType());
            if (explodeChance == null) {
                continue;
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

    private ExplodeMaterialsScope scopeFor(Block block) {
        boolean onGuildTerritory = this.regionManager.findRegionAtLocation(block.getLocation())
                .filter(region -> region.getGuild() != null)
                .isPresent();
        return onGuildTerritory ? this.config.explodeMaterials.guild : this.config.explodeMaterials.global;
    }

}
