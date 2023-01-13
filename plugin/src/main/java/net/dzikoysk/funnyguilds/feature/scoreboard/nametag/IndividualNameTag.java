package net.dzikoysk.funnyguilds.feature.scoreboard.nametag;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.sections.ScoreboardConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookUtils;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserManager;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class IndividualNameTag {

    private final FunnyGuilds plugin;
    private final PluginConfiguration pluginConfiguration;
    private final User user;

    public IndividualNameTag(FunnyGuilds plugin, User user) {
        this.plugin = plugin;
        this.pluginConfiguration = plugin.getPluginConfiguration();
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

        FunnyGuilds.getPluginLogger().debug("[NameTag] Updating " + target.getName() + " for " + this.user.getName());

        Option<Scoreboard> scoreboardOption = this.user.getCache().getScoreboard();
        if (scoreboardOption.isEmpty()) {
            FunnyGuilds.getPluginLogger().debug("We're trying to update NameTag, but scoreboard hasn't been initialized.");
            return;
        }
        Scoreboard scoreboard = scoreboardOption.get();

        Team targetTeam = this.prepareTeam(scoreboard, target.getName());

        ScoreboardConfiguration.NameTag nameTagConfig = this.pluginConfiguration.scoreboard.nametag;
        targetTeam.setPrefix(this.prepareValue(this.prepareConfigValue(nameTagConfig.prefix, target), target));
        targetTeam.setSuffix(this.prepareValue(this.prepareConfigValue(nameTagConfig.suffix, target), target));
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

    private String prepareValue(RawString value, User target) {
        String formatted = decorateValue(value.getValue(), target);
        if (formatted.length() > 16) {
            formatted = formatted.substring(0, 16);
        }
        return formatted;
    }

    private String decorateValue(String value, User target) {
        Player player = Bukkit.getPlayer(this.user.getUUID());
        if (player == null) {
            return value;
        }

        Player targetPlayer = Bukkit.getPlayer(target.getUUID());
        if (targetPlayer == null) {
            return value;
        }

        Guild guild = this.user.getGuild().orNull();
        Guild targetGuild = target.getGuild().orNull();

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{REL_TAG}", this.pluginConfiguration.relationalTag.chooseTag(guild, targetGuild))
                .register("{POS}", UserUtils.getUserPosition(this.pluginConfiguration, target));
        value = formatter.format(value);

        String finalValue = value;
        value = GuildPlaceholdersService.getSimplePlaceholders()
                .map(placeholders -> placeholders.formatVariables(finalValue, targetGuild))
                .orElseGet(value);

        value = HookUtils.replacePlaceholders(targetPlayer, value);
        value = HookUtils.replacePlaceholders(player, targetPlayer, value);

        // Some placeholders may pass color codes (e.g. &6) - we should recolor them
        value = ChatUtils.colored(value);

        return value;
    }

    private RawString prepareConfigValue(ScoreboardConfiguration.NameTag.Value value, User target) {
        Option<Guild> guildOption = this.user.getGuild();
        Option<Guild> targetGuildOption = target.getGuild();

        if (targetGuildOption.isEmpty()) {
            return value.getNoGuild();
        }
        else if (guildOption.isEmpty()) {
            return value.getOtherGuild();
        }

        Guild guild = guildOption.get();
        Guild targetGuild = targetGuildOption.get();

        RawString finalValue = value.getOtherGuild();
        if (guild.equals(targetGuild)) {
            finalValue = value.getOurGuild();
        }
        else if (guild.isAlly(targetGuild) || targetGuild.isAlly(guild)) {
            finalValue = value.getAlliesGuild();
        }
        else if (guild.isEnemy(targetGuild) || targetGuild.isEnemy(guild)) {
            finalValue = value.getEnemiesGuild();
        }
        return finalValue;
    }

}
