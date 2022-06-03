package net.dzikoysk.funnyguilds.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConcurrencyTaskBuilder {

    private final List<ConcurrencyRequest> requests;

    public ConcurrencyTaskBuilder() {
        this.requests = new ArrayList<>();
    }

    public ConcurrencyTaskBuilder delegate(ConcurrencyRequest... delegatedRequests) {
        Collections.addAll(this.requests, delegatedRequests);
        return this;
    }

    public ConcurrencyTask build() {
        return new ConcurrencyTask(this.requests.toArray(new ConcurrencyRequest[0]));
    }

}
