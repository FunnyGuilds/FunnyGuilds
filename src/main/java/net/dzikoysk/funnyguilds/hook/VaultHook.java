package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy economyHook;

    public static void initEconomyHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            FunnyGuildsLogger.warning("No economy provider found, some features may not be available");
            return;
        }

        economyHook = rsp.getProvider();
    }

    public static boolean isHooked() {
        return economyHook != null;
    }

    public static boolean canAfford(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.has(player, money);
    }

    public static EconomyResponse withdrawFromPlayerBank(Player player, double money) {
        Validate.notNull(player, "Player can not be null!");
        return economyHook.withdrawPlayer(player, money);
    }
    
    private VaultHook() {}
    
}
