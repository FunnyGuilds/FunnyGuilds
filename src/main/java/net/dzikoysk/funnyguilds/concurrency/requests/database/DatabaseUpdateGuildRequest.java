package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.Database;
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
                Database database = Database.getInstance();
                DatabaseGuild guild = new DatabaseGuild(this.guild);
                DatabaseRegion region = new DatabaseRegion(this.guild.getRegion());

                guild.save(database);
                region.save(database);
                Stream.concat(this.guild.getMembers().stream(), Stream.of(this.guild.getOwner()))
                        .map(DatabaseUser::new).forEach(databaseUser -> databaseUser.save(database));
            }
            else if (dataModel instanceof FlatDataModel) {
                FlatGuild flatGuild = new FlatGuild(this.guild);
                flatGuild.serialize((FlatDataModel) dataModel);

                FlatRegion flatRegion = new FlatRegion(this.guild.getRegion());
                flatRegion.serialize((FlatDataModel) dataModel);

                Stream.concat(this.guild.getMembers().stream(), Stream.of(this.guild.getOwner()))
                        .map(FlatUser::new).forEach(flatUser -> flatUser.serialize((FlatDataModel) dataModel));
            }
        }
        catch (Throwable th) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not update guild", th);
        }
    }
}
