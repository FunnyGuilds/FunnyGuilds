package net.dzikoysk.funnyguilds.basic.rank;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankManager {

    private static final RankManager INSTANCE = new RankManager();

    private final List<Rank> users = Collections.synchronizedList(new ArrayList<>());
    private final List<Rank> guilds = Collections.synchronizedList(new ArrayList<>());

    private RankManager() {
    }

    public static RankManager getInstance() {
        return INSTANCE;
    }

    public void update(User user) {
        if (! this.users.contains(user.getRank())) {
            this.users.add(user.getRank());
        }

        Collections.sort(users);

        if (user.hasGuild()) {
            update(user.getGuild());
        }

        for (int i = 0; i < users.size(); i++) {
            Rank rank = users.get(i);
            rank.setPosition(i + 1);
        }
    }

    public void update(Guild guild) {
        if (! this.guilds.contains(guild.getRank())) {
            this.guilds.add(guild.getRank());
        }

        Collections.sort(guilds);

        for (int i = 0; i < guilds.size(); i++) {
            Rank rank = guilds.get(i);
            rank.setPosition(i + 1);
        }
    }

    public User getUser(int i) {
        if (i - 1 < this.users.size()) {
            return (this.users.get(i - 1)).getUser();
        }
        return null;
    }

    public Guild getGuild(int i) {
        if (i - 1 < this.guilds.size()) {
            return (this.guilds.get(i - 1)).getGuild();
        }
        return null;
    }

    public int users() {
        return this.users.size();
    }

    public int guilds() {
        return this.guilds.size();
    }

    public void remove(User user) {
        this.users.remove(user.getRank());

        Collections.sort(this.users);
    }

    public void remove(Guild guild) {
        this.guilds.remove(guild.getRank());

        Collections.sort(this.guilds);
    }
}