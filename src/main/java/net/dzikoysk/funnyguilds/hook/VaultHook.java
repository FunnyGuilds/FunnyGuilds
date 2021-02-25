package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy economyHook;
    private static Permission permissionHook;

    public static void initHooks() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);

        if (economyProvider != null) {
            economyHook = economyProvider.getProvider();
        }
        else {
            FunnyGuilds.getInstance().getPluginLogger().warning("No economy provider found, some features may not be available");
        }

        if (permissionProvider != null) {
            permissionHook = permissionProvider.getProvider();
        }
        else {
            FunnyGuilds.getInstance().getPluginLogger().warning("No permission provider found, some features may not be available");
        }
    }

    public static boolean isEconomyHooked() {
        return economyHook != null;
    }

    public static boolean isPermissionHooked() {
        return permissionHook != null;
    }

    public static double accountBalance(Player player) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.getBalance(player);
    }

    public static boolean canAfford(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.has(player, money);
    }

    public static EconomyResponse withdrawFromPlayerBank(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.withdrawPlayer(player, money);
    }

    public static boolean hasPermission(OfflinePlayer player, String permission) {
        return permissionHook.playerHas(null, player, permission);
    }

    private VaultHook() {
    }

}
