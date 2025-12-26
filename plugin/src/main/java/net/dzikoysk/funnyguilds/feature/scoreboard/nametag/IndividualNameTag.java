package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import java.lang.ref.WeakReference;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionChecker;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.adventure.ComponentUtil;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import panda.std.Option;

public class IndividualNameTag {

    private final PluginConfiguration pluginConfiguration;
    private final GuildPermissionChecker permissionChecker;

    private WeakReference<Player> playerRef;
    private final User user;

    IndividualNameTag(
            PluginConfiguration pluginConfiguration,
            GuildPermissionChecker permissionChecker,
            Player player,
            User user
    ) {
        this.pluginConfiguration = pluginConfiguration;
        this.permissionChecker = permissionChecker;
        this.playerRef = new WeakReference<>(player);
        this.user = user;
    }

    void initialize() {
        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to initialize NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team team = this.prepareTeam(scoreboard, this.user.getName());
        team.setPrefix(this.user.getName());
    }

    private Player getPlayer() {
        Player player = this.playerRef.get();
        if (player == null) {
            player = Bukkit.getPlayer(this.user.getUUID());
            this.playerRef = new WeakReference<>(player);
        }
        return player;
    }

    // Update specific player for this user
    public void updatePlayer(Option<Player> targetPlayerOption, User targetUser) {
        if (targetPlayerOption.isEmpty() || !targetUser.isOnline()) {
            this.removePlayer(targetUser);
            return;
        }
        Player targetPlayer = targetPlayerOption.get();

        FunnyGuilds.getPluginLogger().debug("[NameTag] Updating " + targetUser.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team targetTeam = this.prepareTeam(scoreboard, targetUser.getName());

        ScoreboardConfiguration.NameTag nameTagConfig = this.pluginConfiguration.scoreboard.nametag;
        targetTeam.prefix(this.prepareValue(this.getNameTagFormat(nameTagConfig.prefix, targetUser), targetPlayer, targetUser));
        targetTeam.suffix(this.prepareValue(this.getNameTagFormat(nameTagConfig.suffix, targetUser), targetPlayer, targetUser));
    }

    public void removePlayer(User target) {
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

    private Component prepareValue(String value, Player targetPlayer, User targetUser) {
        Component componentValue = ComponentUtil.colored(value);
        return this.decorateValue(componentValue, targetPlayer, targetUser);
    }

    private Component decorateValue(Component value, Player targetPlayer, User targetUser) {
        Player player = this.getPlayer();
        if (player == null) {
            return value;
        }

        Guild guild = this.user.getGuild().orNull();
        Guild targetGuild = targetUser.getGuild().orNull();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{REL_TAG}", this.pluginConfiguration.relationalTag.chooseTag(guild, targetGuild))
                .register("{POS}", UserUtils.getUserPosition(this.permissionChecker, targetUser));
        GuildPlaceholdersService.getSimplePlaceholders().peek(placeholders -> formatter.register(placeholders, guild));
        value = formatter.replace(null, value);
        
        value = HookUtils.replacePlaceholders(targetPlayer, value);
        value = HookUtils.replacePlaceholders(player, targetPlayer, value);

        return value;
    }

    private String getNameTagFormat(ScoreboardConfiguration.NameTag.Value value, User target) {
        Option<Guild> guildOption = this.user.getGuild();
        Option<Guild> targetGuildOption = target.getGuild();

        if (targetGuildOption.isEmpty()) {
            return value.getNoGuild();
        }
        
        if (guildOption.isEmpty()) {
            return value.getOtherGuild();
        }

        Guild guild = guildOption.get();
        Guild targetGuild = targetGuildOption.get();
        
        if (guild.equals(targetGuild)) {
            return value.getOurGuild();
        }
        
        if (guild.isAlly(targetGuild) || targetGuild.isAlly(guild)) {
            return value.getAlliesGuild();
        }
        
        if (guild.isEnemy(targetGuild) || targetGuild.isEnemy(guild)) {
            return value.getEnemiesGuild();
        }
        
        return value.getOtherGuild();
    }

}
