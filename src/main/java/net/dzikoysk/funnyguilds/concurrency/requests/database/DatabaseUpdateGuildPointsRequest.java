package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;

public class DatabaseUpdateGuildPointsRequest extends DefaultConcurrencyRequest {

    private final Guild guild;
    private final SQLDataModel sqlDataModel;

    public DatabaseUpdateGuildPointsRequest(Guild guild, SQLDataModel sqlDataModel) {
        this.guild = guild;
        this.sqlDataModel = sqlDataModel;
    }

    @Override
    public void execute() {
        sqlDataModel.getDatabaseGuild().updatePoints(guild);
    }

}
