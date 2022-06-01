package net.dzikoysk.funnyguilds.config;

import java.util.List;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import panda.std.stream.PandaStream;

public class RawString {

    private final String value;

    public RawString(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public String replace(String from, String to) {
        return FunnyFormatter.formatOnce(this.value, from, to);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RawString && this.value.equals(((RawString) obj).value);
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static List<RawString> listOf(String... values) {
        try (PandaStream<String> valuesStream = PandaStream.of(values)) {
            return valuesStream.map(RawString::new).toList();
        }
    }

}

