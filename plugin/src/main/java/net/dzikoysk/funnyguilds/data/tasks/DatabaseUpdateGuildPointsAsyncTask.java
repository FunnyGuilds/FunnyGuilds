package net.dzikoysk.funnyguilds.data.tasks;

import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;

public class DatabaseUpdateGuildPointsAsyncTask extends AsyncFunnyTask {

    private final Guild guild;

    public DatabaseUpdateGuildPointsAsyncTask(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() {
        DatabaseGuildSerializer.updatePoints(this.guild);
    }

}
