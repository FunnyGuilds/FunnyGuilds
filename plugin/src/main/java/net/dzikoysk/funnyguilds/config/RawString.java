package net.dzikoysk.funnyguilds.config;

import java.util.List;
import java.util.Objects;
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
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RawString)) {
            if (obj instanceof String) {
                return this.value.equals(obj);
            }
            return false;
        }
        RawString rawString = (RawString) obj;

        return value.equals(rawString.value);
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

