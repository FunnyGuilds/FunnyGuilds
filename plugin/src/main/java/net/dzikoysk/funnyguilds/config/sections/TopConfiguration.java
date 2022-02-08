package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class TopConfiguration extends OkaeriConfig {

    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts")
    public Set<String> enabledUserTops = new HashSet<>(Arrays.asList("points", "kills", "deaths"));

    @Comment("Dostepne typy topek: points, kills, deaths, assists, logouts, avg_points, avg_kills, avg_deaths, avg_assists, avg_logouts")
    public Set<String> enabledGuildTops = new HashSet<>(Arrays.asList("kills", "deaths", "avg_points"));

}
