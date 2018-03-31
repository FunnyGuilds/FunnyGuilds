package net.dzikoysk.funnyguilds.concurrency.requests.rank;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.util.RankManager;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class RankUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public RankUpdateGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        RankManager.getInstance().update(guild);
    }

}
