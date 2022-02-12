package net.dzikoysk.funnyguilds.concurrency.requests.database;

import java.util.stream.Stream;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatGuild;
import net.dzikoysk.funnyguilds.data.flat.FlatRegion;
import net.dzikoysk.funnyguilds.data.flat.FlatUser;
import net.dzikoysk.funnyguilds.guild.Guild;

public class DatabaseUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final PluginConfiguration config;
    private final DataModel dataModel;
    private final Guild guild;

    public DatabaseUpdateGuildRequest(PluginConfiguration config, DataModel dataModel, Guild guild) {
        this.config = config;
        this.dataModel = dataModel;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            if (this.dataModel instanceof SQLDataModel) {
                DatabaseGuild.save(guild);

                if (this.config.regionsEnabled) {
                    DatabaseRegion.save(guild.getRegion());
                }

                Stream.concat(guild.getMembers().stream(), guild.getOwnerOption().toJavaStream()).forEach(DatabaseUser::save);
                return;
            }

            if (this.dataModel instanceof FlatDataModel) {
                FlatGuild flatGuild = new FlatGuild(guild);
                flatGuild.serialize((FlatDataModel) this.dataModel);

                if (this.config.regionsEnabled) {
                    FlatRegion flatRegion = new FlatRegion(guild.getRegion());
                    flatRegion.serialize((FlatDataModel) this.dataModel);
                }

                Stream.concat(guild.getMembers().stream(), guild.getOwnerOption().toJavaStream())
                        .map(FlatUser::new)
                        .forEach(flatUser -> flatUser.serialize((FlatDataModel) this.dataModel));
            }
        }
        catch (Throwable th) {
            FunnyGuilds.getPluginLogger().error("Could not update guild", th);
        }
    }
}
