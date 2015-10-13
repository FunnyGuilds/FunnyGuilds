package net.dzikoysk.funnyguilds.util.element;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.OfflineUser;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class IndividualPrefix {

    private final User user;

    public IndividualPrefix(User user) {
        this.user = user;
        this.initialize();
        user.setIndividualPrefix(this);
    }

    protected void addPlayer(OfflinePlayer player) {
        if (player == null)
            return;
        User user = User.get(player);
        if (!user.hasGuild())
            return;
        Scoreboard scoreboard = this.getUser().getScoreboard();
        Team team = scoreboard.getPlayerTeam(player);
        if (team != null)
            team.removePlayer(player);
        team = scoreboard.getTeam(user.getGuild().getTag());
        if (team == null) {
            addGuild(user.getGuild());
            team = scoreboard.getTeam(user.getGuild().getTag());
        }
        if (this.getUser().hasGuild())
            if (this.getUser().equals(user) || this.getUser().getGuild().getMembers().contains(user))
                team.setPrefix(replace(
                        Settings.getInstance().prefixOur,
                        "{TAG}",
                        user.getGuild().getTag()
                ));
        team.addPlayer(player);
    }

    public void addGuild(Guild to) {
        if (to == null)
            return;
        Scoreboard scoreboard = getUser().getScoreboard();
        Guild guild = getUser().getGuild();
        if (guild != null) {
            if (guild.equals(to)) {
                initialize();
                return;
            }
            Team team = scoreboard.getTeam(to.getTag());
            if (team == null)
                team = scoreboard.registerNewTeam(to.getTag());
            for (User u : to.getMembers()) {
                OfflineUser player = new OfflineUser(u.getName());
                if (!team.hasPlayer(player))
                    team.addPlayer(player);
            }
            String prefix = Settings.getInstance().prefixOther;
            if (guild.getAllies().contains(to))
                prefix = Settings.getInstance().prefixAllies;
            if (guild.getEnemies().contains(to))
                prefix = Settings.getInstance().prefixEnemies;
            prefix = prefix.replace("{TAG}", to.getTag());
            if (prefix.length() > 16)
                prefix = prefix.substring(0, 16);
            team.setPrefix(prefix);
        } else {
            Team team = scoreboard.getTeam(to.getTag());
            if (team == null)
                team = scoreboard.registerNewTeam(to.getTag());
            for (User u : to.getMembers()) {
                OfflineUser player = new OfflineUser(u.getName());
                if (!team.hasPlayer(player))
                    team.addPlayer(player);
            }
            team.setPrefix(replace(
                    Settings.getInstance().prefixOther,
                    "{TAG}",
                    to.getTag()
            ));
        }
    }

    protected void removePlayer(OfflinePlayer op) {
        if (op == null)
            return;
        Team team = getUser().getScoreboard().getPlayerTeam(op);
        if (team != null) {
            team.removePlayer(op);
            if (team.getName() != null) {
                team.setPrefix(replace(
                        Settings.getInstance().prefixOther,
                        "{TAG}",
                        team.getName()
                ));
            }
        }
    }

    protected void removeGuild(Guild guild) {
        if (guild == null || guild.getTag() == null || guild.getTag().isEmpty())
            return;
        Team team = getUser().getScoreboard().getTeam(guild.getTag());
        if (team != null)
            team.unregister();
    }

    private void initialize() {
        if (getUser() == null)
            return;
        List<Guild> guilds = GuildUtils.getGuilds();
        Scoreboard scoreboard = getUser().getScoreboard();
        Guild guild = getUser().getGuild();
        if (guild != null) {
            guilds.remove(guild);
            Settings config = Settings.getInstance();
            String our = config.prefixOur;
            String ally = config.prefixAllies;
            String enemy = config.prefixEnemies;
            String other = config.prefixOther;
            Team team = scoreboard.getTeam(guild.getTag());
            if (team == null)
                team = scoreboard.registerNewTeam(guild.getTag());
            for (User u : guild.getMembers()) {
                if (u.getName() == null)
                    continue;
                OfflineUser player = new OfflineUser(u.getName());
                if (!team.hasPlayer(player))
                    team.addPlayer(player);
            }
            team.setPrefix(replace(our, "{TAG}", guild.getTag()));
            for (Guild one : guilds) {
                if (one == null || one.getTag() == null)
                    continue;
                team = scoreboard.getTeam(one.getTag());
                if (team == null)
                    team = scoreboard.registerNewTeam(one.getTag());
                for (User u : one.getMembers()) {
                    if (u.getName() == null)
                        continue;
                    OfflineUser player = new OfflineUser(u.getName());
                    if (!team.hasPlayer(player))
                        team.addPlayer(player);
                }
                if (guild.getAllies().contains(one))
                    team.setPrefix(replace(ally, "{TAG}", one.getTag()));
                else if (guild.getEnemies().contains(one))
                    team.setPrefix(replace(enemy, "{TAG}", one.getTag()));
                else
                    team.setPrefix(replace(other, "{TAG}", one.getTag()));
            }
        } else {
            String other = Settings.getInstance().prefixOther;
            for (Guild one : guilds) {
                if (one == null || one.getTag() == null)
                    continue;
                Team team = scoreboard.getTeam(one.getTag());
                if (team == null)
                    team = scoreboard.registerNewTeam(one.getTag());
                for (User u : one.getMembers()) {
                    if (u.getName() == null)
                        continue;
                    OfflineUser player = new OfflineUser(u.getName());
                    if (!team.hasPlayer(player))
                        team.addPlayer(player);
                }
                team.setPrefix(replace(other, "{TAG}", one.getTag()));
            }
        }
    }

    private String replace(String f, String r, String t) {
        String s = f.replace(r, t);
        if (s.length() > 16)
            s = s.substring(0, 16);
        return s;
    }

    public User getUser() {
        return user;
    }

    public Scoreboard getScoreboard() {
        return getUser().getScoreboard();
    }
}
