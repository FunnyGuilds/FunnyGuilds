package net.dzikoysk.funnyguilds.guild;

import java.util.Map;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.rank.RankManager;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class GuildRankManager extends RankManager<GuildTop, GuildRank> {

    public GuildRankManager(PluginConfiguration pluginConfiguration) {
        super(pluginConfiguration);
    }

    public Option<Guild> getGuild(String topId, int place) {
        return this.getTop(topId)
                .flatMap(top -> top.getGuild(place));
    }

    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= pluginConfiguration.minMembersToInclude;
    }

    public void register(String id, GuildTop guildTop) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledGuildTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.addUserTop(id, guildTop);
    }

    public void register(Map<String, GuildTop> topsToRegister) {
        topsToRegister.forEach(this::register);
    }

}
