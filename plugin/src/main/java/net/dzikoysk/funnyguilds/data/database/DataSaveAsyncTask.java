package net.dzikoysk.funnyguilds.data.database;

import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.shared.FunnyTask.AsyncFunnyTask;

public class DataSaveAsyncTask extends AsyncFunnyTask {

    private final DataModel dataModel;
    private final boolean fullSave;

    public DataSaveAsyncTask(DataModel dataModel, boolean fullSave) {
        this.dataModel = dataModel;
        this.fullSave = fullSave;
    }

    @Override
    public void execute() {
        this.dataModel.save(!this.fullSave);
    }

}
