package net.dzikoysk.funnyguilds.user;

import java.util.Map;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.rank.RankManager;
import net.dzikoysk.funnyguilds.user.top.UserTop;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class UserRankManager extends RankManager<UserTop, UserRank> {

    public UserRankManager(PluginConfiguration pluginConfiguration) {
        super(pluginConfiguration);
    }

    public Option<User> getUser(String topId, int place) {
        return this.getTop(topId)
                .flatMap(top -> top.getUser(place));
    }

    public void register(String id, UserTop top) {
        if (PandaStream.of(this.pluginConfiguration.top.enabledUserTops)
                .find(enabledTop -> enabledTop.equalsIgnoreCase(id))
                .isEmpty()) {
            return;
        }
        this.addUserTop(id, top);
    }

    public void register(Map<String, UserTop> topsToRegister) {
        topsToRegister.forEach(this::register);
    }

}
