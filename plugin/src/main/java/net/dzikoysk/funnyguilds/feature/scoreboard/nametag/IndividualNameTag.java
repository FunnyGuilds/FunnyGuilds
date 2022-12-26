package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class IndividualNameTag {

    private final FunnyGuilds plugin;
    private final User user;

    public IndividualNameTag(FunnyGuilds plugin, User user) {
        this.plugin = plugin;
        this.user = user;
    }

    public void initialize() {
        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team team = this.prepareTeam(scoreboard, this.user.getName());
        team.setPrefix(this.user.getName());

        this.updatePlayers();
    }

    // Update every player for this user
    public void updatePlayers() {
        UserManager userManager = this.plugin.getUserManager();
        PandaStream.of(Bukkit.getOnlinePlayers())
                .flatMap(player -> userManager.findByUuid(player.getUniqueId()))
                .forEach(this::updatePlayer);
    }

    // Update specific player for this user
    public void updatePlayer(User target) {
        if (!target.isOnline()) {
            this.removePlayer(target);
            return;
        }

        if (this.user.equals(target)) {
            return;
        }
        FunnyGuilds.getPluginLogger().debug("[NameTag] Updating " + target.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team targetTeam = prepareTeam(scoreboard, target.getName());

        //TODO: Set normal values lol
        targetTeam.setPrefix(target.getName());
        targetTeam.setSuffix(ChatColor.RED + "Suffix");
    }

    public void removePlayer(User target) {
        if (this.user.equals(target)) {
            return;
        }
        FunnyGuilds.getPluginLogger().debug("[NameTag] Removing " + target.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to remove NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team team = scoreboard.getEntryTeam(target.getName());
        if (team != null) {
            team.removeEntry(target.getName());
            team.unregister();
        }
    }

    private Team prepareTeam(Scoreboard scoreboard, String teamOwner) {
        Team team = scoreboard.getTeam(teamOwner);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamOwner);
        }

        if (!team.hasEntry(teamOwner)) {
            team.addEntry(teamOwner);
        }

        return team;
    }

    private String prepareValue(String value, User target) {
        value = decorateValue(value, target);
        if (value.length() > 16) {
            value = value.substring(0, 16);
        }
        return value;
    }

    private String decorateValue(String value, User target) {
        Player player = Bukkit.getPlayer(this.user.getUUID());
        Player targetPlayer = Bukkit.getPlayer(target.getUUID());

        value = HookUtils.replacePlaceholders(targetPlayer, player, value);

        return value;
    }

}
