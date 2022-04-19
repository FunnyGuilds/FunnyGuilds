package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import net.dzikoysk.funnyguilds.nms.Reflections;
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
            if (blockLocation.getBlockY() < config.tntProtection.build.minHeight) {
                event.setCancelled(true);
            }

            if (blockLocation.getBlockY() > config.tntProtection.build.maxHeight) {
                event.setCancelled(true);
            }
        }

        boolean isProtected = ProtectionSystem.isProtected(player, blockLocation, true)
                .peek(ProtectionSystem::defaultResponse)
                .isPresent();

        if (!isProtected) {
            return;
        }

        if (config.placingBlocksBypassOnRegion.contains(type)) {
            return;
        }

        // always cancel to prevent breaking other protection
        // plugins or plugins using BlockPlaceEvent (eg. special ability blocks)
        event.setCancelled(true);

        // disabled bugged-blocks or blacklisted item
        if (!config.buggedBlocks || config.buggedBlocksExclude.contains(type)) {
            return;
        }

        // clone item before changing amount in the player's inventory
        ItemStack itemInHand = event.getItemInHand();
        ItemStack returnItem = itemInHand.clone();
        returnItem.setAmount(1);

        // remove one item from the player's inventory
        if ((itemInHand.getAmount() - 1) == 0) {
            // wondering why? because bukkit and you probably don't want dupe glitches
            if (Reflections.USE_PRE_9_METHODS) {
                player.setItemInHand(null);
            }
            else {
                itemInHand.setAmount(0);
            }
        }
        else {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }

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
                if (config.buggedBlocksReturn) {
                    player.getInventory().addItem(returnItem);
                }
            }, config.buggedBlocksTimer);

        });
    }

}
