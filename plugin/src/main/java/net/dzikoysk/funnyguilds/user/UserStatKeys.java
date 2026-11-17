package net.dzikoysk.funnyguilds.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.rank.StatKey;

public final class UserStatKeys {
    
    public static final StatKey<Integer> POINTS = StatKey.integerStatKey("points");
    public static final StatKey<Integer> KILLS = StatKey.integerStatKey("kills");
    public static final StatKey<Integer> DEATHS = StatKey.integerStatKey("deaths");
    public static final StatKey<Integer> ASSISTS = StatKey.integerStatKey("assists");
    public static final StatKey<Integer> LOGOUTS = StatKey.integerStatKey("logouts");
    public static final StatKey<Double> KDR = StatKey.doubleStatKey("kdr");
    public static final StatKey<Double> KDA = StatKey.doubleStatKey("kda");

    public static final List<StatKey<?>> VALUES = Collections.unmodifiableList(Arrays.asList(
            POINTS, KILLS, DEATHS, ASSISTS, LOGOUTS, KDR, KDA
    ));

    
    private UserStatKeys() {
    }
}
