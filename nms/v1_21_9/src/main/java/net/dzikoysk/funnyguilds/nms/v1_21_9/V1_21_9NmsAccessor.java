package net.dzikoysk.funnyguilds.nms.v1_21_9;

import net.dzikoysk.funnyguilds.nms.api.playerlist.PlayerListAccessor;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_4.V1_21_4NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21_9.playerlist.V1_21_9PlayerList;
import net.dzikoysk.funnyguilds.nms.v1_21_9.statistics.V1_21_9StatisticsAccessor;

public class V1_21_9NmsAccessor extends V1_21_4NmsAccessor {

    @Override
    public PlayerListAccessor getPlayerListAccessor() {
        return V1_21_9PlayerList::new;
    }

    @Override
    public StatisticsAccessor getStatisticsAccessor() {
        return new V1_21_9StatisticsAccessor();
    }

}
