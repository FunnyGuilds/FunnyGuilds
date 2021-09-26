package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.guild.Guild;

public class RankUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public RankUpdateGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        FunnyGuilds.getInstance().getRankManager().update(guild);
    }

}
