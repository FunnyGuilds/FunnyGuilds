package net.dzikoysk.funnyguilds;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dzikoysk.funnyguilds.util.commons.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class FunnyGuildsVersion {

    private static final Gson GSON = new Gson();

    private static final String GH_COMMITS_URL = "https://api.github.com/repos/FunnyGuilds/FunnyGuilds/commits";
    private static final String VERSION_FILE_URL = "https://funnyguilds.dzikoysk.net/latest.info";
    private static final String GITHUB_URL = "https://github.com/funnyguilds/funnyguilds";
    private static final String DISCORD_URL = "https://discord.com/invite/CYvyq3u";

    private final FunnyGuilds funnyGuilds;
    private final String fullVersion;
    private final String mainVersion;

    public FunnyGuildsVersion(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;

        String version = funnyGuilds.getDescription().getVersion();
        this.fullVersion = version;
        this.mainVersion = version.substring(0, version.lastIndexOf('-'));
    }

    public void isNewAvailable(CommandSender sender, boolean force) {
        if (!this.funnyGuilds.getPluginConfiguration().updateInfo && !force) {
            return;
        }

        if (!sender.hasPermission("funnyguilds.admin")) {
            return;
        }

        this.funnyGuilds.getServer().getScheduler().runTaskAsynchronously(this.funnyGuilds, () -> {
            String latest = IOUtils.getContent(VERSION_FILE_URL);

            if (latest != null) {
                String currentNightlyHash = getCurrentNightlyHash(latest);

                if (StringUtils.isNotBlank(currentNightlyHash)) {
                    if (this.funnyGuilds.getPluginConfiguration().updateNightlyInfo) {
                        try {
                            String ghResponse = IOUtils.getContent(GH_COMMITS_URL);
                            JsonArray ghCommits = GSON.fromJson(ghResponse, JsonArray.class);

                            if (ghCommits.size() == 0) {
                                return;
                            }

                            JsonObject latestCommit = ghCommits.get(0).getAsJsonObject();
                            String commitHash = latestCommit.get("sha").getAsString().substring(0, 7);

                            if (! commitHash.equals(currentNightlyHash)) {
                                printNewVersionAvailable(sender, latest + "-" + commitHash, true);
                            }
                        }
                        catch (Throwable th) {
                            this.funnyGuilds.getPluginLogger().update("Could not retrieve latest nightly version!");
                            this.funnyGuilds.getPluginLogger().update(Throwables.getStackTraceAsString(th));
                        }
                    }
                }
                else if (! latest.equalsIgnoreCase(this.fullVersion)) {
                    printNewVersionAvailable(sender, latest, false);
                }
            }
        });
    }

    private void printNewVersionAvailable(CommandSender sender, String latest, boolean isNightly) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
        sender.sendMessage(ChatColor.GRAY + "Dostepna jest nowa wersja " + ChatColor.AQUA + "FunnyGuilds" + (isNightly ? " Nightly" : "") + ChatColor.GRAY + '!');
        sender.sendMessage(ChatColor.GRAY + "Obecna: " + ChatColor.AQUA + this.fullVersion);
        sender.sendMessage(ChatColor.GRAY + "Najnowsza: " + ChatColor.AQUA + latest);
        sender.sendMessage(ChatColor.GRAY + "GitHub: " + ChatColor.AQUA + GITHUB_URL);
        sender.sendMessage(ChatColor.GRAY + "Discord: " + ChatColor.AQUA + DISCORD_URL);
        sender.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------");
        sender.sendMessage("");
    }

    private String getCurrentNightlyHash(String latest) {
        String remainder = StringUtils.replace(this.fullVersion, latest, "").trim();

        // hyphen + short commit hash
        if (remainder.length() != 8 || ! remainder.startsWith("-")) {
            return "";
        }

        return StringUtils.replace(remainder, "-", "");
    }

    public String getFullVersion() {
        return this.fullVersion;
    }

    public String getMainVersion() {
        return this.mainVersion;
    }
}
