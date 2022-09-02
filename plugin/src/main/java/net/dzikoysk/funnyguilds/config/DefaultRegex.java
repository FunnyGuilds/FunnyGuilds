package net.dzikoysk.funnyguilds.config;

import java.util.regex.Pattern;
import panda.std.Option;
import panda.std.stream.PandaStream;

public enum DefaultRegex {

    LOWERCASE("[a-z]+"),
    UPPERCASE("[A-Z]+"),
    DIGITS("[0-9]+"),

    LOWERCASE_DIGITS("[a-z0-9]+"),
    UPPERCASE_DIGITS("[A-Z0-9]+"),

    LETTERS("[a-zA-Z]+"),
    LETTERS_DIGITS("[a-zA-Z0-9]+"),
    LETTERS_DIGITS_UNDERSCORE("[a-zA-Z0-9_]+");

    private final String pattern;
    private final Pattern compiledPattern;

    DefaultRegex(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = Pattern.compile(pattern);
    }

    public String getPattern() {
        return this.pattern;
    }

    public Pattern getCompiledPattern() {
        return this.compiledPattern;
    }

    public static Option<DefaultRegex> findRegex(String name) {
        return PandaStream.of(values()).find(regex -> regex.name().equalsIgnoreCase(name));
    }

}
