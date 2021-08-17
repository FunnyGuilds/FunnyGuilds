package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatGuild;
import net.dzikoysk.funnyguilds.data.flat.FlatRegion;
import net.dzikoysk.funnyguilds.data.flat.FlatUser;

import java.util.stream.Stream;

public class DatabaseUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public DatabaseUpdateGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() {
        DataModel dataModel = FunnyGuilds.getInstance().getDataModel();

        try {
            if (dataModel instanceof SQLDataModel) {
                DatabaseGuild.save(guild);

                if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
                    DatabaseRegion.save(guild.getRegion());
                }

                Stream.concat(guild.getMembers().stream(), Stream.of(guild.getOwner())).forEach(DatabaseUser::save);
                return;
            }

            if (dataModel instanceof FlatDataModel) {
                FlatGuild flatGuild = new FlatGuild(guild);
                flatGuild.serialize((FlatDataModel) dataModel);

                if (FunnyGuilds.getInstance().getPluginConfiguration().regionsEnabled) {
                    FlatRegion flatRegion = new FlatRegion(guild.getRegion());
                    flatRegion.serialize((FlatDataModel) dataModel);
                }

                Stream.concat(guild.getMembers().stream(), Stream.of(guild.getOwner()))
                        .map(FlatUser::new).forEach(flatUser -> flatUser.serialize((FlatDataModel) dataModel));
            }
        }
        catch (Throwable th) {
            FunnyGuilds.getPluginLogger().error("Could not update guild", th);
        }
    }
}
