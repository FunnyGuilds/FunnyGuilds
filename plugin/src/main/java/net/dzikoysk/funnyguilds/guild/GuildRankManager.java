package net.dzikoysk.funnyguilds.guild;

import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.guild.top.GuildTop;
import net.dzikoysk.funnyguilds.rank.RankManager;

public class GuildRankManager extends RankManager<GuildTop, GuildRank> {

    public GuildRankManager(PluginConfiguration pluginConfiguration) {
        super(pluginConfiguration);
    }

    public Optional<Guild> getGuild(String topId, int place) {
        return this.getTop(topId).flatMap(top -> top.getGuild(place));
    }

    public boolean isRankedGuild(Guild guild) {
        return guild.getMembers().size() >= this.pluginConfiguration.minMembersToInclude;
    }

    public void register(String id, GuildTop guildTop) {
        this.pluginConfiguration.top.enabledGuildTops.stream()
                .filter(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .findFirst()
                .ifPresent(enabledTop -> this.addTop(id, guildTop));
    }

    public void register(Map<String, GuildTop> topsToRegister) {
        topsToRegister.forEach(this::register);
    }

}
