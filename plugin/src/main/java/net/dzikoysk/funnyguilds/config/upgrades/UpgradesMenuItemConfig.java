package net.dzikoysk.funnyguilds.config.upgrades;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.ArrayList;
import java.util.List;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class UpgradesMenuItemConfig extends OkaeriConfig {

    @Comment("Typ itemu ulepszenia w menu")
    public String material = "STONE";

    @Comment("")
    @Comment("Nazwa itemu")
    public String name = "";

    @Comment("")
    @Comment("Lore itemu, gdy ulepszenie nie osiągnęło jeszcze maksymalnego poziomu")
    public List<String> lore = new ArrayList<>();

    @Comment("")
    @Comment("Lore itemu, gdy ulepszenie osiągnęło już maksymalny poziom")
    @CustomKey("maxed-lore")
    public List<String> maxedLore = new ArrayList<>();

    public UpgradesMenuItemConfig() {
    }

}
