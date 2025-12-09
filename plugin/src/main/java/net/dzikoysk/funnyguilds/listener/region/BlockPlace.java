package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockPlace extends AbstractFunnyListener {

    private static final Vector ANTI_GLITCH_VELOCITY = new Vector(0, 0.4, 0);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        Location blockLocation = block.getLocation();

        if (type == Material.TNT) {
            if (blockLocation.getBlockY() < this.config.tntProtection.build.minHeight) {
                event.setCancelled(true);
            }

            if (blockLocation.getBlockY() > this.config.tntProtection.build.maxHeight) {
                event.setCancelled(true);
            }
        }

        // Determine which protection permission to check based on block type
        GuildProtectionPermission protectionPermission = getProtectionPermissionForBlock(type);
        
        boolean isProtected = ProtectionSystem.isProtected(
                        player,
                        blockLocation,
                        event,
                        protectionPermission,
                        true
                )
                .peek(ProtectionSystem::defaultResponse)
                .isPresent();

        if (!isProtected) {
            // Block was placed successfully in unprotected area or by member
            // Remove from regeneration tracking if applicable
            removeBlockFromRegenerationTracking(blockLocation);
            return;
        }

        if (this.config.placingBlocksBypassOnRegion.contains(type)) {
            // Block bypass allowed - remove from regeneration tracking
            removeBlockFromRegenerationTracking(blockLocation);
            return;
        }

        // always cancel to prevent breaking other protection
        // plugins or plugins using BlockPlaceEvent (eg. special ability blocks)
        event.setCancelled(true);

        // disabled bugged-blocks or blacklisted item
        if (!this.config.buggedBlocks || this.config.buggedBlocksExclude.contains(type)) {
            return;
        }

        // clone item before changing amount in the player's inventory
        ItemStack itemInHand = event.getItemInHand();
        ItemStack returnItem = itemInHand.clone();

        returnItem.setAmount(1);
        itemInHand.setAmount(itemInHand.getAmount() - 1);

        // if the player is standing on the placed block add some velocity to prevent glitching
        // side effect: velocity with +y0.4 is like equivalent to jumping while building, just hold right click, that's real fun!
        Location playerLocation = player.getLocation();
        boolean sameColumn = (playerLocation.getBlockX() == blockLocation.getBlockX()) && (playerLocation.getBlockZ() == blockLocation.getBlockZ());
        double distanceUp = (playerLocation.getY() - blockLocation.getBlockY());
        boolean upToTwoBlocks = (distanceUp > 0) && (distanceUp <= 2);

        if (sameColumn && upToTwoBlocks) {
            player.setVelocity(ANTI_GLITCH_VELOCITY);
        }

        // delay, because we cannot do {@link Block#setType(Material)} immediately
        Bukkit.getScheduler().runTask(this.plugin, () -> {

            // fake place for bugged block
            block.setType(type);

            // start timer and return the item to the player if specified to do so
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                event.getBlockReplacedState().update(true);

                if (!player.isOnline()) {
                    return;
                }

                if (this.config.buggedBlocksReturn) {
                    player.getInventory().addItem(returnItem);
                }
            }, this.config.buggedBlocksTimer);

        });
    }

    /**
     * Determines the appropriate protection permission based on block type.
     * Special blocks like TNT and Obsidian use more specific permissions.
     */
    private GuildProtectionPermission getProtectionPermissionForBlock(Material type) {
        if (type == Material.TNT) {
            return GuildProtectionPermission.TNT_PLACE;
        }
        if (type == Material.OBSIDIAN) {
            return GuildProtectionPermission.OBSIDIAN_PLACE;
        }
        return GuildProtectionPermission.BLOCK_PLACE;
    }

    /**
     * Removes a block from regeneration tracking when a player places a block at that location.
     */
    private void removeBlockFromRegenerationTracking(Location location) {
        if (!this.config.guildPanel.regeneration.enabled) {
            return;
        }

        this.regionManager.findRegionAtLocation(location)
                .map(Region::getGuild)
                .peek(guild -> this.regenerationManager.removeBlockAtLocation(guild, location));
    }

}
