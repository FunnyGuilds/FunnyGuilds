package net.dzikoysk.funnyguilds.data.tasks;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseGuildSerializer;
import net.dzikoysk.funnyguilds.data.database.serializer.DatabaseRegionSerializer;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;

/**
 * Removes a rolled-back guild creation from persistence. A periodic save may have snapshotted
 * the guild before the rollback removed it from memory, so this compensating delete runs after
 * the in-memory cleanup to erase anything that leaked through.
 */
public class DatabaseDeleteGuildAsyncTask extends AsyncFunnyTask {

    private final DataModel dataModel;
    private final Guild guild;

    public DatabaseDeleteGuildAsyncTask(DataModel dataModel, Guild guild) {
        this.dataModel = dataModel;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            if (this.dataModel instanceof SQLDataModel) {
                DatabaseGuildSerializer.delete(this.guild);
                this.guild.getRegion().ifPresent(DatabaseRegionSerializer::delete);
            }
            else if (this.dataModel instanceof FlatDataModel) {
                FlatDataModel flatDataModel = (FlatDataModel) this.dataModel;
                flatDataModel.getGuildFile(this.guild).ifPresent(FunnyIOUtils::deleteFile);
                this.guild.getRegion().ifPresent(region -> flatDataModel.getRegionFile(region).ifPresent(FunnyIOUtils::deleteFile));
            }
        }
        catch (Exception exception) {
            FunnyGuilds.getPluginLogger().error("Could not delete the rolled-back guild from persistence", exception);
        }
    }

}
