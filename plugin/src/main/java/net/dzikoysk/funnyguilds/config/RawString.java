package net.dzikoysk.funnyguilds.config;

import java.util.List;
import panda.std.stream.PandaStream;

public class RawString {

    private final String value;

    public RawString(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static List<RawString> listOf(String... values) {
        return PandaStream.of(values)
                .map(RawString::new)
                .toList();
    }

}

