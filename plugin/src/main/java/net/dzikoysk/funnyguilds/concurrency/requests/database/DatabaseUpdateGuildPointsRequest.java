package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.guild.Guild;

public class DatabaseUpdateGuildPointsRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public DatabaseUpdateGuildPointsRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() {
        DatabaseGuild.updatePoints(guild);
    }

}
