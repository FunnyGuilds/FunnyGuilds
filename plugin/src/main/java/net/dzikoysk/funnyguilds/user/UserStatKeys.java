package net.dzikoysk.funnyguilds.user;

import net.dzikoysk.funnyguilds.rank.StatKey;

public final class UserStatKeys {
    
    public static final StatKey<Integer> POINTS = StatKey.integerStatKey("points");
    public static final StatKey<Integer> KILLS = StatKey.integerStatKey("kills");
    public static final StatKey<Integer> DEATHS = StatKey.integerStatKey("deaths");
    public static final StatKey<Integer> ASSISTS = StatKey.integerStatKey("assists");
    public static final StatKey<Integer> LOGOUTS = StatKey.integerStatKey("logouts");
    
    private UserStatKeys() {
    }
}
