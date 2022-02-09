package net.dzikoysk.funnyguilds.rank;

import java.util.HashMap;
import java.util.Map;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import panda.std.Option;

public class RankManager {

    private final PluginConfiguration pluginConfiguration;

    protected final Map<String, UserTop> userTopMap = new HashMap<>();
    protected final Map<String, GuildTop> guildTopMap = new HashMap<>();

    @Deprecated
    private static RankManager INSTANCE;

    public RankManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;

        INSTANCE = this;
    }

    public Option<UserTop> getUserTop(String id) {
        return Option.of(this.userTopMap.get(id.toLowerCase()));
    }

    public void addUserTop(String id, UserTop userTop) {
        this.userTopMap.put(id.toLowerCase(), userTop);
    }

    public Option<GuildTop> getGuildTop(String id) {
        return Option.of(this.guildTopMap.get(id.toLowerCase()));
    }

    public void addGuildTop(String id, GuildTop guildTop) {
        this.guildTopMap.put(id.toLowerCase(), guildTop);
    }

    public Option<User> getUser(int place) {
        return getUserTop(TopFactory.USER_POINTS_TOP)
                .map(top -> top.getUser(place));
    }

    public Option<Guild> getGuild(int place) {
        return getGuildTop(TopFactory.GUILD_AVG_POINTS_TOP)
                .map(top -> top.getGuild(place));
    }

    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= pluginConfiguration.minMembersToInclude;
    }

    /**
     * Gets the rank manager.
     *
     * @return the rank manager
     * @deprecated for removal in the future, in favour of {@link FunnyGuilds#getRankManager()}
     */
    @Deprecated
    public static RankManager getInstance() {
        return INSTANCE;
    }

}