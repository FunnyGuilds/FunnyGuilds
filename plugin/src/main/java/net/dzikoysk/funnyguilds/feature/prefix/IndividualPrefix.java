package net.dzikoysk.funnyguilds.feature.prefix;

import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
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
        plugin.getUserManager().findByName(player)
                .filter(User::hasGuild)
                .peek(byName -> {
                    Scoreboard scoreboard = getScoreboard();
                    Team team = scoreboard.getEntryTeam(player);
                    Guild guild = byName.getGuild().get();

                    if (team != null) {
                        team.removeEntry(player);
                    }

                    team = scoreboard.getTeam(guild.getTag());
                    if (team == null) {
                        addGuild(guild);
                        team = scoreboard.getTeam(guild.getTag());
                    }

                    if (team == null) {
                        FunnyGuilds.getPluginLogger().debug("We're trying to add Prefix for player, but guild team is null");
                        return;
                    }

                    if (this.user.hasGuild()) {
                        if (this.user.equals(byName) || guild.getMembers().contains(byName)) {
                            team.setPrefix(preparePrefix(plugin.getPluginConfiguration().prefixOur.getValue(), guild));
                        }
                    }

                    team.addEntry(player);
                });
    }

    public void addGuild(Guild to) {
        if (to == null) {
            return;
        }

        Scoreboard scoreboard = getScoreboard();

        if (user.hasGuild()) {
            Guild guild = user.getGuild().get();
            if (guild.equals(to)) {
                initialize();
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

            String prefix = plugin.getPluginConfiguration().prefixOther.getValue();

            if (guild.getAllies().contains(to)) {
                prefix = plugin.getPluginConfiguration().prefixAllies.getValue();
            }

            if (guild.getEnemies().contains(to) || to.getEnemies().contains(guild)) {
                prefix = plugin.getPluginConfiguration().prefixEnemies.getValue();
            }

            team.setPrefix(preparePrefix(prefix, to));
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

            team.setPrefix(preparePrefix(plugin.getPluginConfiguration().prefixOther.getValue(), to));
        }
    }

    protected void removePlayer(String playerName) {
        Team team = getScoreboard().getEntryTeam(playerName);
        if (team != null) {
            team.removeEntry(playerName);
        }

        plugin.getUserManager().findByName(playerName)
                .peek(this::registerSoloTeam);
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

        for (User member : guild.getMembers()) {
            registerSoloTeam(member);
        }
    }

    public void initialize() {
        Set<Guild> guilds = plugin.getGuildManager().getGuilds();
        Scoreboard scoreboard = getScoreboard();

        if (user.hasGuild()) {
            Guild guild = user.getGuild().get();
            guilds.remove(guild);

            PluginConfiguration config = plugin.getPluginConfiguration();
            String our = config.prefixOur.getValue();
            String ally = config.prefixAllies.getValue();
            String enemy = config.prefixEnemies.getValue();
            String other = config.prefixOther.getValue();

            Team team = scoreboard.getTeam(guild.getTag());
            if (team == null) {
                team = scoreboard.registerNewTeam(guild.getTag());
            }

            for (User member : guild.getMembers()) {
                if (!team.hasEntry(member.getName())) {
                    team.addEntry(member.getName());
                }
            }

            team.setPrefix(preparePrefix(our, guild));

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                team = scoreboard.getTeam(one.getTag());

                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User member : one.getMembers()) {
                    if (!team.hasEntry(member.getName())) {
                        team.addEntry(member.getName());
                    }
                }

                if (guild.getAllies().contains(one)) {
                    team.setPrefix(preparePrefix(ally, one));
                }
                else if (guild.getEnemies().contains(one) || one.getEnemies().contains(guild)) {
                    team.setPrefix(preparePrefix(enemy, one));
                }
                else {
                    team.setPrefix(preparePrefix(other, one));
                }
            }
        }
        else {
            String other = plugin.getPluginConfiguration().prefixOther.getValue();
            registerSoloTeam(this.user);

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
        Set<Guild> guilds = plugin.getGuildManager().getGuilds();

        if (teamName.length() > 16) {
            teamName = soloUser.getName();
        }

        for (Guild guild : guilds) {
            if (guild.getTag().equalsIgnoreCase(teamName)) {
                return;
            }
        }

        Team team = getScoreboard().getTeam(teamName);

        if (team == null) {
            team = getScoreboard().registerNewTeam(teamName);
        }

        if (!team.hasEntry(soloUser.getName())) {
            team.addEntry(soloUser.getName());
        }
    }

    public static String preparePrefix(String text, Guild guild) {
        String formatted = FunnyGuilds.getInstance().getGuildPlaceholdersService().getSimplePlaceholders()
                .map(placeholders -> placeholders.format(text, guild))
                .orElseGet(text);

        if (formatted.length() > 16) {
            formatted = formatted.substring(0, 16);
        }

        return formatted;
    }

    @NotNull
    public Scoreboard getScoreboard() {
        return this.user.getCache().getScoreboard()
                .orThrow(() -> new NullPointerException("scoreboard is null"));
    }

}
