package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class BaseCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${user.base.name}",
        aliases = "${user.base.aliases}",
        description = "${user.base.description}",
        permission = "funnyguilds.base",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, @IsMember User user, Guild guild) {
        when (!this.pluginConfiguration.regionsEnabled, this.messageConfiguration.regionsDisabled);
        when (!this.pluginConfiguration.baseEnable, this.messageConfiguration.baseTeleportationDisabled);
        when (user.getCache().getTeleportation() != null, this.messageConfiguration.baseIsTeleportation);

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.base")
                ? Collections.emptyList()
                : this.pluginConfiguration.baseItems;

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        if (this.pluginConfiguration.baseDelay.isZero()) {
            player.teleport(guild.getHome());
            player.sendMessage(this.messageConfiguration.baseTeleport);
            return;
        }

        Duration time = player.hasPermission("funnyguilds.vip.baseTeleportTime")
                ? this.pluginConfiguration.baseDelayVip
                : this.pluginConfiguration.baseDelay;

        Location before = player.getLocation();
        Instant teleportStart = Instant.now();
        UserCache cache = user.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }
            
            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                player.sendMessage(this.messageConfiguration.baseMove);
                cache.setTeleportation(null);
                player.getInventory().addItem(items);
                return;
            }

            if (Duration.between(teleportStart, Instant.now()).compareTo(time) > 0) {
                cache.getTeleportation().cancel();
                player.sendMessage(this.messageConfiguration.baseTeleport);
                player.teleport(guild.getHome());
                cache.setTeleportation(null);
            }
        }, 0L, 10L));

        player.sendMessage(this.messageConfiguration.baseDontMove.replace("{TIME}", Long.toString(time.getSeconds())));
    }

}
