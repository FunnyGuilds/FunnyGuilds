package net.dzikoysk.funnyguilds.feature.prefix;

import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.placeholders.GuildPlaceholdersService;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class IndividualPrefix {

    private final User user;
    private final FunnyGuilds plugin;

    public IndividualPrefix(User user) {
        this.user = user;
        this.plugin = FunnyGuilds.getInstance();
    }

    protected void addPlayer(String player) {
        this.plugin.getUserManager().findByName(player)
                .filter(User::hasGuild)
                .peek(byName -> {
                    Scoreboard scoreboard = this.getScoreboard();
                    Team team = scoreboard.getEntryTeam(player);
                    Guild guild = byName.getGuild().get();

                    if (team != null) {
                        team.removeEntry(player);
                    }

                    team = scoreboard.getTeam(guild.getTag());
                    if (team == null) {
                        this.addGuild(guild);
                        team = scoreboard.getTeam(guild.getTag());
                    }

                    if (team == null) {
                        FunnyGuilds.getPluginLogger().debug("We're trying to add Prefix for player, but guild team is null");
                        return;
                    }

                    if (this.user.hasGuild()) {
                        if (this.user.equals(byName) || this.user.getGuild().is(userGuild -> userGuild.isMember(byName))) {
                            team.setPrefix(preparePrefix(this.plugin.getPluginConfiguration().prefixOur.getValue(), guild));
                        }
                    }

                    team.addEntry(player);
                });
    }

    public void addGuild(Guild to) {
        if (to == null) {
            return;
        }

        Scoreboard scoreboard = this.getScoreboard();

        if (this.user.hasGuild()) {
            Guild guild = this.user.getGuild().get();

            if (guild.equals(to)) {
                this.initialize();
                return;
            }

            Team team = scoreboard.getTeam(to.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }

            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }

            team.setPrefix(chooseAndPreparePrefix(this.plugin.getPluginConfiguration(), guild, to));
        }
        else {
            Team team = scoreboard.getTeam(to.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(to.getTag());
            }

            for (User u : to.getMembers()) {
                if (!team.hasEntry(u.getName())) {
                    team.addEntry(u.getName());
                }
            }

            team.setPrefix(preparePrefix(this.plugin.getPluginConfiguration().prefixOther.getValue(), to));
        }
    }

    protected void removePlayer(String playerName) {
        Team team = this.getScoreboard().getEntryTeam(playerName);
        if (team != null) {
            team.removeEntry(playerName);
        }

        this.plugin.getUserManager().findByName(playerName).peek(this::registerSoloTeam);
    }

    protected void removeGuild(Guild guild) {
        if (guild == null) {
            return;
        }

        String tag = guild.getTag();
        if (tag.isEmpty()) {
            throw new IllegalStateException("Guild tag can't be empty!");
        }

        this.user.getCache().getScoreboard()
                .map(scoreboard -> scoreboard.getTeam(tag))
                .peek(Team::unregister);

        guild.getMembers().forEach(this::registerSoloTeam);
    }

    public void initialize() {
        Set<Guild> guilds = this.plugin.getGuildManager().getGuilds();
        Scoreboard scoreboard = this.getScoreboard();

        if (this.user.hasGuild()) {
            Guild userGuild = this.user.getGuild().get();
            guilds.remove(userGuild);

            Team team = scoreboard.getTeam(userGuild.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(userGuild.getTag());
            }

            for (User member : userGuild.getMembers()) {
                if (!team.hasEntry(member.getName())) {
                    team.addEntry(member.getName());
                }
            }

            team.setPrefix(preparePrefix(this.plugin.getPluginConfiguration().prefixOur.getValue(), userGuild));

            for (Guild otherGuild : guilds) {
                if (otherGuild == null || otherGuild.getTag() == null) {
                    continue;
                }

                team = scoreboard.getTeam(otherGuild.getTag());
                if (team == null) {
                    team = scoreboard.registerNewTeam(otherGuild.getTag());
                }

                for (User member : otherGuild.getMembers()) {
                    if (!team.hasEntry(member.getName())) {
                        team.addEntry(member.getName());
                    }
                }

                team.setPrefix(chooseAndPreparePrefix(this.plugin.getPluginConfiguration(), userGuild, otherGuild));
            }
        }
        else {
            String other = this.plugin.getPluginConfiguration().prefixOther.getValue();
            this.registerSoloTeam(this.user);

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                Team team = scoreboard.getTeam(one.getTag());
                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User member : one.getMembers()) {
                    if (!team.hasEntry(member.getName())) {
                        team.addEntry(member.getName());
                    }
                }

                team.setPrefix(preparePrefix(other, one));
            }
        }
    }

    private void registerSoloTeam(User soloUser) {
        String teamName = soloUser.getName() + "_solo";
        Set<Guild> guilds = this.plugin.getGuildManager().getGuilds();

        if (teamName.length() > 16) {
            teamName = soloUser.getName();
        }

        for (Guild guild : guilds) {
            if (guild.getTag().equalsIgnoreCase(teamName)) {
                return;
            }
        }

        Team team = this.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = this.getScoreboard().registerNewTeam(teamName);
        }

        if (!team.hasEntry(soloUser.getName())) {
            team.addEntry(soloUser.getName());
        }
    }

    public static String chooseAndPreparePrefix(PluginConfiguration config, Guild guild, Guild inPrefix) {
        if (inPrefix == null) {
            return "";
        }

        if (guild == null) {
            return preparePrefix(config.prefixOther.getValue(), inPrefix);
        }

        if (guild.equals(inPrefix)) {
            return preparePrefix(config.prefixOur.getValue(), inPrefix);
        }

        if (guild.isAlly(inPrefix)) {
            return preparePrefix(config.prefixAllies.getValue(), inPrefix);
        }

        if (guild.isEnemy(inPrefix) || inPrefix.isEnemy(guild)) {
            return preparePrefix(config.prefixEnemies.getValue(), inPrefix);
        }

        return preparePrefix(config.prefixOther.getValue(), inPrefix);
    }

    public static String preparePrefix(String text, Guild guild) {
        String formatted = GuildPlaceholdersService.getSimplePlaceholders()
                .map(placeholders -> placeholders.formatVariables(text, guild))
                .orElseGet(text);

        if (formatted.length() > 16) {
            formatted = formatted.substring(0, 16);
        }

        return formatted;
    }

    @NotNull
    public Scoreboard getScoreboard() {
        return this.user.getCache().getScoreboard().orThrow(() -> new IllegalStateException("scoreboard is null"));
    }

}
