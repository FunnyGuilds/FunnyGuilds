package net.dzikoysk.funnyguilds.module.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PanelAudit {

    String guildTag();

    List<String> categories();

    /** An empty optional means access was denied. An empty list means there are no entries. */
    Optional<List<Entry>> read();

    record Entry(String date, String actor, String category, String type, Map<String, String> details) {

        public Entry {
            details = Map.copyOf(details);
        }
    }
}
