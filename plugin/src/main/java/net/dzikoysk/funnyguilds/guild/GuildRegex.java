package net.dzikoysk.funnyguilds.guild;

public enum GuildRegex {

    LOWERCASE ("[a-z]+"),
    UPPERCASE ("[A-Z]+"),
    DIGITS ("[0-9]+"),
    
    LOWERCASE_DIGITS ("[a-z0-9]+"),
    UPPERCASE_DIGITS ("[A-Z0-9]+"),
    
    LETTERS ("[a-zA-Z]+"),
    LETTERS_DIGITS ("[a-zA-Z0-9]+"),
    LETTERS_DIGITS_UNDERSCORE ("[a-zA-Z0-9_]+");
    
    private final String pattern;
    
    GuildRegex(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return this.pattern;
    }
}
