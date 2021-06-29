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
        Collections.addAll(requests, delegatedRequests);
        return this;
    }

    public ConcurrencyTask build() {
        ConcurrencyRequest[] requestArray = new ConcurrencyRequest[requests.size()];
        return new ConcurrencyTask(requests.toArray(requestArray));
    }

}
