package net.dzikoysk.funnyguilds.basic.util;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Rank;
import net.dzikoysk.funnyguilds.basic.User;

import java.util.ArrayList;
import java.util.Collections;

public class RankManager {

    private static RankManager instance;
    private final ArrayList<Rank> users = new ArrayList<>();
    private final ArrayList<Rank> guilds = new ArrayList<>();

    public RankManager() {
        instance = this;
    }

    public static RankManager getInstance() {
        if (instance == null)
            new RankManager();
        return instance;
    }

    public void update(User user) {
        if (!this.users.contains(user.getRank()))
            this.users.add(user.getRank());
        Collections.sort(this.users);
        if (user.hasGuild())
            update(user.getGuild());
    }

    public void update(Guild guild) {
        if (!this.guilds.contains(guild.getRank()))
            this.guilds.add(guild.getRank());
        else
            Collections.sort(this.guilds);
    }

    public void remove(User user) {
        this.users.remove(user.getRank());
        Collections.sort(this.users);
    }

    public void remove(Guild guild) {
        this.guilds.remove(guild.getRank());
        Collections.sort(this.guilds);
    }

    public int getPosition(User user) {
        return this.users.indexOf(user.getRank()) + 1;
    }

    public int getPosition(Guild guild) {
        return this.guilds.indexOf(guild.getRank()) + 1;
    }

    public User getUser(int i) {
        if (i - 1 < this.users.size())
            return (this.users.get(i - 1)).getUser();
        return null;
    }

    public Guild getGuild(int i) {
        if (i - 1 < this.guilds.size())
            return (this.guilds.get(i - 1)).getGuild();
        return null;
    }

    public int users() {
        return this.users.size();
    }

    public int guilds() {
        return this.guilds.size();
    }
}
