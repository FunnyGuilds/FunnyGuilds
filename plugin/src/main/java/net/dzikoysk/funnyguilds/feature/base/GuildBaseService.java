package net.dzikoysk.funnyguilds.feature.base;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.command.GuildCommandPermission;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

public final class GuildBaseService {

    private final FunnyGuilds plugin;

    public GuildBaseService(FunnyGuilds plugin) {
        this.plugin = plugin;
    }

    public List<ItemStack> requiredItems(Player player) {
        if (player.hasPermission("funnyguilds.vip.base")) {
            return List.of();
        }
        List<ItemStack> cost = new ArrayList<>();
        for (ItemStack item : this.plugin.getPluginConfiguration().baseItems) {
            cost.stream().filter(item::isSimilar).findFirst().ifPresentOrElse(
                    existing -> existing.setAmount(existing.getAmount() + item.getAmount()),
                    () -> cost.add(item.clone()));
        }
        return cost;
    }

    public Duration delay(Player player) {
        PluginConfiguration settings = this.plugin.getPluginConfiguration();
        if (settings.baseDelay.isZero()) {
            return Duration.ZERO;
        }
        return player.hasPermission("funnyguilds.vip.baseTeleportTime")
                ? settings.baseDelayVip
                : settings.baseDelay;
    }

    public void teleport(Player player, User member, Guild guild) {
        PluginConfiguration settings = this.plugin.getPluginConfiguration();
        when(!settings.regionsEnabled, config -> config.regionsDisabled);
        when(!settings.baseEnable, config -> config.baseTeleportationDisabled);
        when(member.getCache().getTeleportation() != null, config -> config.baseIsTeleportation);
        when(!guild.hasHome(), config -> config.baseHasNotCenter);

        List<ItemStack> requiredItems = this.requiredItems(player);

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, config -> config.baseItems)) {
            return;
        }

        ItemStack[] items = ItemUtils.toArray(requiredItems);
        player.getInventory().removeItem(items);

        if (settings.baseDelay.isZero()) {
            this.finishTeleport(player, member, guild, items);
            return;
        }

        Duration time = this.delay(player);

        Location before = player.getLocation();
        Instant teleportStart = Instant.now();
        UserCache cache = member.getCache();

        cache.setTeleportation(Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (!player.isOnline()) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                return;
            }

            if (!LocationUtils.equals(player.getLocation(), before)) {
                cache.getTeleportation().cancel();
                this.plugin.getMessageService().getMessage(config -> config.baseMove)
                        .receiver(member)
                        .send();
                cache.setTeleportation(null);
                this.refund(player, items);
                return;
            }

            if (Duration.between(teleportStart, Instant.now()).compareTo(time) > 0) {
                cache.getTeleportation().cancel();
                cache.setTeleportation(null);
                if (member.getGuild().filter(guild::equals).isEmpty()
                        || this.plugin.getGuildManager().findByUuid(guild.getUUID()).isEmpty()
                        || !settings.regionsEnabled || !settings.baseEnable
                        || !player.hasPermission("funnyguilds.base")
                        || !this.plugin.getGuildPermissionChecker().handlePermission(guild, member, GuildCommandPermission.BASE)) {
                    this.refund(player, items);
                    return;
                }
                this.finishTeleport(player, member, guild, items);
            }
        }, 0L, 10L));

        this.plugin.getMessageService().getMessage(config -> config.baseDontMove)
                .receiver(member)
                .with("{TIME}", time.getSeconds())
                .send();
    }

    private void finishTeleport(Player player, User member, Guild guild, ItemStack[] items) {
        if (guild.getHome().filter(Location::isWorldLoaded).map(player::teleport).orElse(false)) {
            this.plugin.getMessageService().getMessage(config -> config.baseTeleport)
                    .receiver(member)
                    .send();
        } else {
            this.refund(player, items);
        }
    }

    private void refund(Player player, ItemStack[] items) {
        player.getInventory().addItem(items).values()
                .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

}
