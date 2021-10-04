package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.DataModel;

public class DataSaveRequest extends DefaultConcurrencyRequest {

    private final DataModel dataModel;

    private final boolean fullSave;

    public DataSaveRequest(DataModel dataModel, boolean fullSave) {
        this.dataModel = dataModel;
        this.fullSave = fullSave;
    }

    @Override
    public void execute() {
        this.dataModel.save(!this.fullSave);
    }

}
