package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DataSaveRequest extends DefaultConcurrencyRequest {

    private final boolean fullSave;

    public DataSaveRequest(boolean fullSave) {
        this.fullSave = fullSave;
    }

    @Override
    public void execute() {
        FunnyGuilds.getInstance().getDataModel().save(!this.fullSave);
    }

}
