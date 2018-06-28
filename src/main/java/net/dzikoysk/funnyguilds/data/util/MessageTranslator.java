package net.dzikoysk.funnyguilds.data.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MessageTranslator {

    private final Map<String, String> placeholders = new HashMap<>(3);

    public String translate(String message) {
        for (Entry<String, String> placeholderEntry : placeholders.entrySet()) {
            message = StringUtils.replace(message, placeholderEntry.getKey(), placeholderEntry.getValue());
        }

        return message;
    }

    public MessageTranslator register(String placeholder, Object value) {
        this.placeholders.put(placeholder, value.toString());
        return this;
    }

}
