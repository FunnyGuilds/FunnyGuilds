package net.dzikoysk.funnyguilds.concurrency.requests;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.data.Data;

public class DataSaveRequest extends DefaultConcurrencyRequest {

    @Override
    public void execute() throws Exception {
        Data.getInstance().save();
    }

}
