package net.dzikoysk.funnyguilds.user;

import java.util.Map;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;

public class UserRankManager extends RankManager<UserTop, UserRank> {

    public UserRankManager(PluginConfiguration pluginConfiguration) {
        super(pluginConfiguration);
    }

    public Optional<User> getUser(String topId, int place) {
        return this.getTop(topId).flatMap(top -> top.getUser(place));
    }

    public void register(String id, UserTop userTop) {
        this.pluginConfiguration.top.enabledUserTops.stream()
                .filter(top -> top.equalsIgnoreCase(id))
                .findFirst()
                .ifPresent(enabledTop -> this.addTop(id, userTop));
    }

    public void register(Map<String, UserTop> topsToRegister) {
        topsToRegister.forEach(this::register);
    }

}
