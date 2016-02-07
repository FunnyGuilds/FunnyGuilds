package net.dzikoysk.funnyguilds.util;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import org.bukkit.ChatColor;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;

public class Version {

    public static void check(final Player player) {
        if (player.hasPermission("funnyguilds.admin") || player.isOp()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    String latest = IOUtils.getContent("http://www.dzikoysk.net/projects/funnyguilds/latest.info");
                    if (latest != null && !latest.equalsIgnoreCase(FunnyGuilds.getVersion())) {
                        player.sendMessage("");
                        player.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                        player.sendMessage(ChatColor.GRAY + "Dostepna jest nowa wersja " + ChatColor.AQUA + "FunnyGuilds" + ChatColor.GRAY + '!');
                        player.sendMessage(ChatColor.GRAY + "Obecna: " + ChatColor.AQUA + FunnyGuilds.getVersion());
                        player.sendMessage(ChatColor.GRAY + "Najnowsza: " + ChatColor.AQUA + latest);
                        player.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
                        player.sendMessage("");
                        int interval = 225;
                        for (int i = 0; i < 4; i++) {
                            try {
                                NotePitch.play(player, 3, Tone.C);
                                NotePitch.play(player, 4, Tone.C);
                                Thread.sleep(interval);
                            } catch (Exception e) {
                                if (FunnyGuilds.exception(e.getCause())) e.printStackTrace();
                            }
                        }
                        NotePitch.play(player, 3, Tone.G);
                    }
                }
            };
            thread.start();
        }
    }
}
