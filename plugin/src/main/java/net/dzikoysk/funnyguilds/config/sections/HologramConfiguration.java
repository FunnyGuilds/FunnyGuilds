package net.dzikoysk.funnyguilds.config.sections;

import com.google.common.collect.ImmutableList;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.List;
import org.bukkit.Material;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class HologramConfiguration extends OkaeriConfig {

    @Comment("Czy ta sekcja ma być włączona?")
    public boolean enabled = true;

    @Comment("Co ile tick'ów ma być aktualizowany hologram? (20 tick = 1 sekunda)")
    public long updateInterval = 400L;

    @Comment("Item ozdobny nad hologramem.")
    @Comment("Jeśli ustawiony na AIR to opcja jest wyłączona.")
    public Material item = Material.AIR;

    @Comment("Tekst wyświetlany przez hologram")
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {GUILD-PROTECTION}")
    public List<String> displayedLines = new ImmutableList.Builder<String>()
            .add("&7&m-------------------")
            .add("&7- &bFunnyGuilds &7-")
            .add("&7- &bgithub.com/funnyguilds/ &7-")
            .add("&7- &bGildia {TAG} &7-")
            .add("&7- &bZycia {LIVES} &7-")
            .add("&7~ Created by &bFunnyGuilds Team &7~")
            .add("&7&m-------------------")
            .build();

    @Comment("Korekcja lokalizacji hologramu względem startowej lokalizacji.")
    public LocationConfiguration locationCorrection = new LocationConfiguration(0.5, 2.5, 0.5);

}
