package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.feature.protection.GuildProtectionPermission;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketAction extends AbstractFunnyListener {

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();

        var protection = ProtectionSystem.isProtected(
                        event.getPlayer(),
                        block.getLocation(),
                        event,
                        GuildProtectionPermission.BUCKET_FILL,
                        true
        );
        protection = protection.filter(predicate -> !this.config.placingBlocksBypassOnRegion.contains(block.getType()));
        protection.ifPresent(result -> event.setCancelled(true));
        protection.ifPresent(ProtectionSystem::defaultResponse);
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        var protection = ProtectionSystem.isProtected(
                        event.getPlayer(),
                        event.getBlockClicked().getLocation(),
                        event,
                        GuildProtectionPermission.BUCKET_EMPTY,
                        true
        );
        protection = protection.filter(predicate -> !this.config.placingBlocksBypassOnRegion.contains(event.getBucket()));
        protection.ifPresent(result -> event.setCancelled(true));
        protection.ifPresent(ProtectionSystem::defaultResponse);
    }

}
