package net.dzikoysk.funnyguilds.config;

import java.util.regex.Pattern;

public class FunnyPattern {

    private final String pattern;
    private final Pattern compiledPattern;

    public FunnyPattern(String pattern) {
        this.compiledPattern = DefaultRegex.findRegex(pattern)
                .map(DefaultRegex::getPattern)
                .map(Pattern::compile)
                .orElseGet(Pattern.compile(pattern));
        this.pattern = pattern;
    }

    public FunnyPattern(DefaultRegex regex) {
        this.pattern = regex.name();
        this.compiledPattern = regex.getCompiledPattern();
    }

    public Pattern getCompiledPattern() {
        return this.compiledPattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    public boolean matches(String string) {
        return this.compiledPattern.matcher(string).matches();
    }

}
