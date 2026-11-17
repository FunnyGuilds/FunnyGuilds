package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Definicja pojedynczego itemu z guild-item-library.
 * Używana zarówno dla itemów wymaganych do gildii, jak i itemów GUI (fillery, summary, close).
 *
 * <p>Wszystkie pola tekstowe (name, lore) w formacie MiniMessage.
 * Puste pola to pusty string / pusta lista — nigdy {@code null} w YAML.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildItemDefinition extends OkaeriConfig {

    public String material = "STONE";

    public String name = "";

    public List<String> lore = new ArrayList<>();

    public List<String> enchants = new ArrayList<>();

    public List<String> flags = new ArrayList<>();

    @Nullable
    public Integer customModelData;

    public String skullOwner = "";

    public String armorColor = "";

    public GuildItemDefinition() {
    }

    public GuildItemDefinition(String material) {
        this.material = material;
    }

    public List<String> getEnchantsOrEmpty() {
        return enchants != null ? enchants : Collections.emptyList();
    }

    public List<String> getFlagsOrEmpty() {
        return flags != null ? flags : Collections.emptyList();
    }

}

