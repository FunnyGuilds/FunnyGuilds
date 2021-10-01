package net.dzikoysk.funnyguilds.config.subcomponents;

import com.google.common.collect.ImmutableList;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import org.bukkit.Material;

import java.util.List;

@Names(strategy = NameStrategy.IDENTITY)
public class HologramConfiguration extends OkaeriConfig {

    @Comment("Czy ta sekcja ma być włączona?")
    @CustomKey("enabled")
    public boolean enabled = true;

    @Comment("Co ile tick'ów ma być aktualizowany hologram? (20 tick = 1 sekunda)")
    @CustomKey("update-interval")
    public long updateInterval = 400L;

    @Comment("Item ozdobny nad hologramem.")
    @Comment("Jeśli ustawiony na AIR to opcja jest wyłączona.")
    @CustomKey("item")
    public Material item = Material.AIR;

    @Comment("Tekst wyświetlany przez hologram")
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {GUILD-PROTECTION}")
    @CustomKey("displayed-lines")
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
    @CustomKey("location-correction")
    public LocationConfiguration locationCorrection = new LocationConfiguration(0.5, 8.0, 0.5);

}
