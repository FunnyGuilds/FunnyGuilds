package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;

public class DataSaveRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() throws Exception {
        FunnyGuilds.getInstance().getDataModel().save(false);
    }

}
