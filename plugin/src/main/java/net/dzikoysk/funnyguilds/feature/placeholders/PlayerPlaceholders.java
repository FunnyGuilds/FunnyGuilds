package net.dzikoysk.funnyguilds.feature.placeholders;

import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.placeholders.impl.user.PlayerPlaceholder;
import net.dzikoysk.funnyguilds.feature.placeholders.resolver.PlayerResolver.OptionResolver;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import panda.utilities.text.Joiner;

public class PlayerPlaceholders extends Placeholders<User, PlayerPlaceholder> {

    public static final Placeholders<User, PlayerPlaceholder> PLAYER;

    static {
        PLAYER = new PlayerPlaceholders()
                .property("world", new PlayerPlaceholder((OptionResolver) playerOption -> playerOption
                        .map(player -> Bukkit.getOnlinePlayers().stream().filter(player::canSee).count())
                        .map(value -> Long.toString(value))
                        .orElseGet("")));

        if (HookManager.WORLD_GUARD.isPresent()) {
            String wgRegionNoValue = FunnyGuilds.getInstance().getMessageConfiguration().wgRegionNoValue;

            PLAYER.property("wg-region", new PlayerPlaceholder(player -> {
                List<String> regionNames = getWorldGuardRegionNames(player);
                return !regionNames.isEmpty() ? regionNames.get(0) : wgRegionNoValue;
            }));

            PLAYER.property("wg-regions", new PlayerPlaceholder(player -> {
                List<String> regionNames = getWorldGuardRegionNames(player);
                return !regionNames.isEmpty() ? Joiner.on(", ").join(regionNames) : wgRegionNoValue;
            }));
        }

        if (HookManager.VAULT.isPresent() && VaultHook.isEconomyHooked()) {
            PLAYER.property("vault-money", new PlayerPlaceholder((OptionResolver) playerOption -> playerOption
                    .map(VaultHook::accountBalance)
                    .map(value -> Double.toString(value))
                    .orElseGet("")));
        }
    }

    private static List<String> getWorldGuardRegionNames(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }

        Location location = player.getLocation();
        List<String> regionNames = HookManager.WORLD_GUARD.map(worldGuard -> worldGuard.getRegionNames(location))
                .orNull();

        if (regionNames != null && !regionNames.isEmpty()) {
            return regionNames;
        }

        return Collections.emptyList();
    }

}
