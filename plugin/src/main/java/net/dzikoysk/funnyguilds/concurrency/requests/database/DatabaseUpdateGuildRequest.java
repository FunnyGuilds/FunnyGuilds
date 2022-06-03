package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseUserSerializer;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatGuildSerializer;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.seralizer.FlatUserSerializer;
import net.dzikoysk.funnyguilds.guild.Guild;
import panda.std.stream.PandaStream;

public class DatabaseUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final DataModel dataModel;
    private final Guild guild;

    public DatabaseUpdateGuildRequest(DataModel dataModel, Guild guild) {
        this.dataModel = dataModel;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            if (this.dataModel instanceof SQLDataModel) {
                SQLDataModel sqlDataModel = (SQLDataModel) this.dataModel;
                DatabaseGuildSerializer.serialize(sqlDataModel, this.guild);

                this.guild.getRegion()
                        .peek(region -> DatabaseRegionSerializer.serialize(sqlDataModel, region));

                PandaStream.of(this.guild.getMembers())
                        .forEach(member -> DatabaseUserSerializer.serialize(sqlDataModel, member));

                return;
            }

            if (this.dataModel instanceof FlatDataModel) {
                FlatDataModel flatDataModel = (FlatDataModel) this.dataModel;
                FlatGuildSerializer.serialize(flatDataModel, this.guild);

                this.guild.getRegion()
                        .peek(region -> FlatRegionSerializer.serialize(flatDataModel, region));

                PandaStream.of(this.guild.getMembers())
                        .forEach(member -> FlatUserSerializer.serialize(flatDataModel, member));
            }
        }
        catch (Throwable throwable) {
            FunnyGuilds.getPluginLogger().error("Could not update guild", throwable);
        }
    }

}
