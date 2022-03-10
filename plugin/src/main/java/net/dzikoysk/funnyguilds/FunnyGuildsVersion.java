package net.dzikoysk.funnyguilds;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dzikoysk.funnyguilds.shared.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class FunnyGuildsVersion {

    private static final Gson GSON = new Gson();

    private static final String GH_COMMITS_URL = "https://api.github.com/repos/FunnyGuilds/FunnyGuilds/commits";
    private static final String VERSION_FILE_URL = "https://funnyguilds.dzikoysk.net/latest.info";
    private static final String NIGHTLY_VERSION_FILE_URL = "https://raw.githubusercontent.com/FunnyGuilds/FunnyGuilds/master/updater-nightly.txt";
    private static final String GITHUB_URL = "https://github.com/funnyguilds/funnyguilds";
    private static final String DISCORD_URL = "https://discord.com/invite/CYvyq3u";

    private final FunnyGuilds funnyGuilds;
    private final String fullVersion;
    private final String mainVersion;
    private final String commitHash;

    public FunnyGuildsVersion(FunnyGuilds funnyGuilds) {
        this.funnyGuilds = funnyGuilds;

        String version = funnyGuilds.getDescription().getVersion();
        this.fullVersion = version;
        this.mainVersion = version.substring(0, version.lastIndexOf('-'));
        this.commitHash = version.substring(version.lastIndexOf('-') + 1);
    }

    public void isNewAvailable(CommandSender sender, boolean force) {
        if (!this.funnyGuilds.getPluginConfiguration().updateInfo && !force) {
            return;
        }

        if (!sender.hasPermission("funnyguilds.admin")) {
            return;
        }

        this.funnyGuilds.getServer().getScheduler().runTaskAsynchronously(this.funnyGuilds, () -> {
            if (funnyGuilds.getPluginConfiguration().updateNightlyInfo) {
                try {
                    String ghResponse = IOUtils.getContent(GH_COMMITS_URL);
                    JsonArray ghCommits = GSON.fromJson(ghResponse, JsonArray.class);

                    if (ghCommits.size() == 0) {
                        return;
                    }

                    String latestNightlyVersion = IOUtils.getContent(NIGHTLY_VERSION_FILE_URL);
                    JsonObject latestCommit = ghCommits.get(0).getAsJsonObject();
                    String latestCommitHash = latestCommit.get("sha").getAsString().substring(0, 7);

                    String latestNightly = latestNightlyVersion + "-" + latestCommitHash;

                    if (!commitHash.equalsIgnoreCase(latestCommitHash)) {
                        printNewVersionAvailable(sender, latestNightly, true);
                    }
                }
                catch (Throwable th) {
                    FunnyGuilds.getPluginLogger().update("Could not retrieve latest nightly version!");
                    FunnyGuilds.getPluginLogger().update(Throwables.getStackTraceAsString(th));
                }
            }
            else {
                String latestRelease = IOUtils.getContent(VERSION_FILE_URL);

                if (latestRelease == null) {
                    return;
                }

                if (latestRelease.contains("Warning:")) {
                    FunnyGuilds.getPluginLogger().warning(latestRelease);
                    return;
                }

                if (!mainVersion.equalsIgnoreCase(latestRelease)) {
                    printNewVersionAvailable(sender, latestRelease, false);
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

    public String getFullVersion() {
        return this.fullVersion;
    }

    public String getMainVersion() {
        return this.mainVersion;
    }

    public String getCommitHash() {
        return commitHash;
    }

}
