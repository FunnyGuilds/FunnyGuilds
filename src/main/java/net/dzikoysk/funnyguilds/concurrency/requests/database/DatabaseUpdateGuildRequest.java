package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.DataModel;

import java.util.stream.Stream;

public class DatabaseUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public DatabaseUpdateGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            DataModel dataModel = FunnyGuilds.getInstance().getDataModel();

            dataModel.saveBasic(guild);

            if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
                dataModel.saveBasic(guild.getRegion());
            }

            Stream.concat(guild.getMembers().stream(), Stream.of(guild.getOwner())).forEach(dataModel::saveBasic);
        }
        catch (Throwable th) {
            FunnyGuilds.getPluginLogger().error("Could not update guild", th);
        }
    }
}
