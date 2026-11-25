package net.dzikoysk.funnyguilds.data.tasks;

import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;

public class DatabaseDeleteGuildAsyncTask extends AsyncFunnyTask {

    private final DataModel dataModel;

    public DatabaseDeleteGuildAsyncTask(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public void execute() {
        this.dataModel.flushDeletes();
    }
}
