package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DatabaseFixAlliesRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() {
        FunnyGuilds.getInstance().getGuildManager().getGuilds().forEach(guild -> {
            guild.getAllies().forEach(ally -> ally.addAlly(guild));
        });
    }

}
