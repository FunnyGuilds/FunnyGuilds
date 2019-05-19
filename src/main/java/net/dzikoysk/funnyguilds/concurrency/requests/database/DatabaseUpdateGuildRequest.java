package net.dzikoysk.funnyguilds.concurrency.requests.database;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.database.Database;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.DatabaseRegion;
import net.dzikoysk.funnyguilds.data.database.DatabaseUser;

import java.util.stream.Stream;

public class DatabaseUpdateGuildRequest extends DefaultConcurrencyRequest {

    private final Guild guild;

    public DatabaseUpdateGuildRequest(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            Database database = Database.getInstance();
            DatabaseGuild guild = new DatabaseGuild(this.guild);
            DatabaseRegion region = new DatabaseRegion(this.guild.getRegion());

            guild.save(database);
            region.save(database);
            Stream.concat(this.guild.getMembers().stream(), Stream.of(this.guild.getOwner()))
                    .map(DatabaseUser::new).forEach(databaseUser -> databaseUser.save(database));
        }
        catch (Throwable th) {
            FunnyGuilds.getInstance().getPluginLogger().error("Could not update guild", th);
        }
    }
}
