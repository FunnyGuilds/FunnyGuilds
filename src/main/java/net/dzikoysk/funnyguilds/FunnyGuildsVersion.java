package net.dzikoysk.funnyguilds;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitHub;

import java.io.IOException;

public final class FunnyGuildsVersion {

    private static final String VERSION_FILE_URL = "https://funnyguilds.dzikoysk.net/latest.info";

    public static void isNewAvailable(CommandSender sender, boolean force) {
        if (!FunnyGuilds.getInstance().getPluginConfiguration().updateInfo && !force) {
            return;
        }

        if (!sender.hasPermission("funnyguilds.admin")) {
            return;
        }

        FunnyGuilds.getInstance().getServer().getScheduler().runTaskAsynchronously(FunnyGuilds.getInstance(), () -> {
            String latest = IOUtils.getContent(VERSION_FILE_URL);

            if (latest != null) {
                String currentNightlyHash = getCurrentNightlyHash(latest);

                if (StringUtils.isNotBlank(currentNightlyHash)) {
                    if (FunnyGuilds.getInstance().getPluginConfiguration().updateNightlyInfo) {
                        try {
                            GitHub github = FunnyGuilds.getInstance().getGithubAPI();

                            if (github == null) {
                                return;
                            }

                            GHCommit latestCommit = Iterables.get(
                                    github.getOrganization("FunnyGuilds").getRepository("FunnyGuilds").listCommits(), 0);

                            String commitHash = latestCommit.getSHA1().substring(0, 7);

                            if (! commitHash.equals(currentNightlyHash)) {
                                printNewVersionAvailable(sender, latest + "-" + commitHash, true);
                            }
                        }
                        catch (IOException ex) {
                            FunnyGuilds.getInstance().getPluginLogger().debug("Could not retrieve latest nightly version!");
                            FunnyGuilds.getInstance().getPluginLogger().debug(Throwables.getStackTraceAsString(ex));
                        }
                    }
                }
                else if (! latest.equalsIgnoreCase(FunnyGuilds.getInstance().getFullVersion())) {
                    printNewVersionAvailable(sender, latest, false);
                }
            }
        });
    }

    private static void printNewVersionAvailable(CommandSender sender, String latest, boolean isNightly) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
        sender.sendMessage(ChatColor.GRAY + "Dostepna jest nowa wersja " + ChatColor.AQUA
                + "FunnyGuilds" + (isNightly ? " Nightly" : "") + ChatColor.GRAY + '!');
        sender.sendMessage(ChatColor.GRAY + "Obecna: " + ChatColor.AQUA + FunnyGuilds.getInstance().getFullVersion());
        sender.sendMessage(ChatColor.GRAY + "Najnowsza: " + ChatColor.AQUA + latest);
        sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
        sender.sendMessage("");
    }

    private static String getCurrentNightlyHash(String latest) {
        String remainder = StringUtils.replace(FunnyGuilds.getInstance().getFullVersion(), latest, "").trim();

        // hyphen + short commit hash
        if (remainder.length() != 7) {
            return "";
        }

        return StringUtils.replace(remainder, "-", "");
    }

    private FunnyGuildsVersion() {}

}
