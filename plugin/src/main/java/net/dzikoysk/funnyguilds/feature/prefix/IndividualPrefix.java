package net.dzikoysk.funnyguilds.feature.prefix;

import java.util.Set;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.UserUtils;
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
        if (player == null) {
            return;
        }

        plugin.getUserManager().findByName(player)
                .filter(User::hasGuild)
                .peek(byName -> {
                    Scoreboard scoreboard = getScoreboard();
                    Team team = scoreboard.getEntryTeam(player);

                    if (team != null) {
                        team.removeEntry(player);
                    }

                    team = scoreboard.getTeam(byName.getGuild().getTag());
                    if (team == null) {
                        addGuild(byName.getGuild());
                        team = scoreboard.getTeam(byName.getGuild().getTag());
                    }

                    if (team == null) {
                        FunnyGuilds.getPluginLogger().debug("We're trying to add Prefix for player, but guild team is null");
                        return;
                    }

                    if (this.getUser().hasGuild()) {
                        if (this.getUser().equals(byName) || this.getUser().getGuild().getMembers().contains(byName)) {
                            team.setPrefix(replace(plugin.getPluginConfiguration().prefixOur, "{TAG}", byName.getGuild().getTag()));
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
        Guild guild = getUser().getGuild();

        if (guild != null) {
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

            String prefix = plugin.getPluginConfiguration().prefixOther;

            if (guild.getAllies().contains(to)) {
                prefix = plugin.getPluginConfiguration().prefixAllies;
            }

            if (guild.getEnemies().contains(to) || to.getEnemies().contains(guild)) {
                prefix = plugin.getPluginConfiguration().prefixEnemies;
            }

            team.setPrefix(replace(prefix, "{TAG}", to.getTag()));
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

            team.setPrefix(replace(plugin.getPluginConfiguration().prefixOther, "{TAG}", to.getTag()));
        }
    }

    protected void removePlayer(String player) {
        if (player == null) {
            return;
        }

        Team team = getScoreboard().getEntryTeam(player);
        if (team != null) {
            team.removeEntry(player);
            if (team.getName().isEmpty()) {
                team.setPrefix(replace(plugin.getPluginConfiguration().prefixOther, "{TAG}", team.getName()));
            }
        }

        registerSoloTeam(UserUtils.get(player));
    }

    protected void removeGuild(Guild guild) {
        if (guild == null || guild.getTag() == null || guild.getTag().isEmpty()) {
            return;
        }

        this.getUser().getCache().getScoreboard()
                .map(scoreboard -> scoreboard.getTeam(guild.getTag()))
                .peek(Team::unregister);

        for (User member : guild.getMembers()) {
            registerSoloTeam(member);
        }
    }

    public void initialize() {
        if (getUser() == null) {
            return;
        }

        Set<Guild> guilds = GuildUtils.getGuilds();
        Scoreboard scoreboard = getScoreboard();
        Guild guild = getUser().getGuild();

        if (guild != null) {
            guilds.remove(guild);

            PluginConfiguration config = plugin.getPluginConfiguration();
            String our = config.prefixOur;
            String ally = config.prefixAllies;
            String enemy = config.prefixEnemies;
            String other = config.prefixOther;
            Team team = scoreboard.getTeam(guild.getTag());

            if (team == null) {
                team = scoreboard.registerNewTeam(guild.getTag());
            }

            for (User member : guild.getMembers()) {
                if (member.getName() == null) {
                    continue;
                }

                if (!team.hasEntry(member.getName())) {
                    team.addEntry(member.getName());
                }
            }

            team.setPrefix(replace(our, "{TAG}", guild.getTag()));

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                team = scoreboard.getTeam(one.getTag());

                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User u : one.getMembers()) {
                    if (u.getName() == null) {
                        continue;
                    }

                    if (!team.hasEntry(u.getName())) {
                        team.addEntry(u.getName());
                    }
                }

                if (guild.getAllies().contains(one)) {
                    team.setPrefix(replace(ally, "{TAG}", one.getTag()));
                }
                else if (guild.getEnemies().contains(one) || one.getEnemies().contains(guild)) {
                    team.setPrefix(replace(enemy, "{TAG}", one.getTag()));
                }
                else {
                    team.setPrefix(replace(other, "{TAG}", one.getTag()));
                }
            }
        }
        else {
            String other = plugin.getPluginConfiguration().prefixOther;
            registerSoloTeam(this.getUser());

            for (Guild one : guilds) {
                if (one == null || one.getTag() == null) {
                    continue;
                }

                Team team = scoreboard.getTeam(one.getTag());

                if (team == null) {
                    team = scoreboard.registerNewTeam(one.getTag());
                }

                for (User u : one.getMembers()) {
                    if (u.getName() == null) {
                        continue;
                    }

                    if (!team.hasEntry(u.getName())) {
                        team.addEntry(u.getName());
                    }
                }

                team.setPrefix(replace(other, "{TAG}", one.getTag()));
            }
        }
    }

    private void registerSoloTeam(User soloUser) {
        String teamName = soloUser.getName() + "_solo";
        Set<Guild> guilds = GuildUtils.getGuilds();

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

    private String replace(String f, String r, String t) {
        String s = f.replace(r, t);

        if (s.length() > 16) {
            s = s.substring(0, 16);
        }

        return s;
    }

    @NotNull
    public Scoreboard getScoreboard() {
        return this.user.getCache().getScoreboard()
                .orThrow(() -> new NullPointerException("scoreboard is null"));
    }

    public User getUser() {
        return this.user;
    }

}
