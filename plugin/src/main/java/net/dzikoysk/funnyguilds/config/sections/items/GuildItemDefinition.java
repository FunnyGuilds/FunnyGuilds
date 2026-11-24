package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Collections;
import java.util.List;
import net.dzikoysk.funnyguilds.shared.bukkit.ResolvedItemDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * Definicja pojedynczego itemu z guild-item-library.
 * Używana zarówno dla itemów wymaganych do gildii, jak i itemów GUI (fillery, summary, close).
 *
 * <p>Wszystkie pola tekstowe (name, lore) w formacie MiniMessage.
 * Wszystkie właściwości są opcjonalne. Brak wartości oznacza brak ograniczenia.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildItemDefinition extends OkaeriConfig {

    @Exclude
    private ResolvedItemDefinition resolved;

    @Nullable
    public String material;

    @Nullable
    public String name;

    @Nullable
    public List<String> lore;

    @Nullable
    public List<String> enchants;

    @Nullable
    public List<String> flags;

    @Nullable
    public Integer customModelData;

    @Nullable
    public String skullOwner;

    @Nullable
    public String armorColor;

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

    public void resolve(String path) {
        this.resolved = ResolvedItemDefinition.resolve(this, path);
    }

    /** Loaded definitions are resolved eagerly; programmatic GUI/API definitions resolve on first use. */
    public ResolvedItemDefinition getResolved() {
        if (this.resolved == null) {
            resolve("item");
        }
        return this.resolved;
    }

}

