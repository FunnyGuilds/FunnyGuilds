package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class FunnyGuildsVersion {

    private static final String VERSION_FILE_URL = "https://funnyguilds.dzikoysk.net/latest.info";

    public static void isNewAvailable(CommandSender sender, boolean force) {
        if (!Settings.getConfig().updateInfo && !force) {
            return;
        }
        
        if (!sender.hasPermission("funnyguilds.admin")) {
            return;
        }

        FunnyGuilds.getInstance().getServer().getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
            String latest = IOUtils.getContent(VERSION_FILE_URL);

            if (latest != null && !latest.equalsIgnoreCase(FunnyGuilds.getMainVersion())) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                sender.sendMessage(ChatColor.GRAY + "Dostepna jest nowa wersja " + ChatColor.AQUA + "FunnyGuilds" + ChatColor.GRAY + '!');
                sender.sendMessage(ChatColor.GRAY + "Obecna: " + ChatColor.AQUA + FunnyGuilds.getFullVersion());
                sender.sendMessage(ChatColor.GRAY + "Najnowsza: " + ChatColor.AQUA + latest);
                sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                sender.sendMessage("");
            }
        });
    }

    private FunnyGuildsVersion() {}

}
