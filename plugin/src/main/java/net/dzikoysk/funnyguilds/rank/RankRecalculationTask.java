package net.dzikoysk.funnyguilds.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.GuildRankManager;
import net.dzikoysk.funnyguilds.user.UserRankManager;

public class RankRecalculationTask implements Runnable {

    private final UserRankManager userRankManager;
    private final GuildRankManager guildRankManager;

    public RankRecalculationTask(FunnyGuilds plugin) {
        this.userRankManager = plugin.getUserRankManager();
        this.guildRankManager = plugin.getGuildRankManager();
    }

    @Override
    public void run() {
        this.userRankManager.recalculateTops();
        this.guildRankManager.recalculateTops();
    }

}
