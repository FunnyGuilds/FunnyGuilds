package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Version {

    public static final String VERSION_FILE_URL = "https://raw.githubusercontent.com/FunnyGuilds/FunnyGuilds/master/updater.txt";

    public static void isNewAvailable(final Player player) {
        if (player.hasPermission("funnyguilds.admin") || player.isOp()) {
            FunnyGuilds.getInstance().getServer().getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
                String latest = IOUtils.getContent(VERSION_FILE_URL);
                if (latest != null && !latest.equalsIgnoreCase(FunnyGuilds.getVersion())) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                    player.sendMessage(ChatColor.GRAY + "Dostepna jest nowa wersja " + ChatColor.AQUA + "FunnyGuilds" + ChatColor.GRAY + '!');
                    player.sendMessage(ChatColor.GRAY + "Obecna: " + ChatColor.AQUA + FunnyGuilds.getVersion());
                    player.sendMessage(ChatColor.GRAY + "Najnowsza: " + ChatColor.AQUA + latest);
                    player.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                    player.sendMessage("");

                    /*int interval = 225;
                    for (int i = 0; i < 4; i++) {
                        try {
                            NotePitch.play(player, 3, Tone.C);
                            NotePitch.play(player, 4, Tone.C);
                            Thread.sleep(interval);
                        } catch (Exception e) {
                            if (FunnyGuilds.exception(e.getCause())) {
                                e.printStackTrace();
                            }
                        }
                    }
                    NotePitch.play(player, 3, Tone.G);*/
                }
            });
        }
    }
}
