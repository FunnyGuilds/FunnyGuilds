package net.dzikoysk.funnyguilds.config.sections.items;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Definicja setu przedmiotów.
 * Sety sortowane malejąco po {@code priority} — pierwszy pasujący (gracz ma permisję) wygrywa.
 */
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GuildItemSet extends OkaeriConfig {

    /** Permisja wymagana do użycia tego setu. {@code null} = dostępny dla wszystkich. */
    @Nullable
    public String permission;

    /** Priorytet setu — wyższy = sprawdzany wcześniej. */
    public int priority = 0;

    /** Flagi wymagań (które wymagania są aktywne). */
    public SetRequirements requirements = new SetRequirements();

    /** Mapa: klucz itemu z library → wymagana ilość. */
    public Map<String, Integer> items = new LinkedHashMap<>();

    /** Wymagane doświadczenie. */
    public int requiredExperience = 0;

    /** Wymagane pieniądze (Vault). */
    public double requiredMoney = 0.0;

    /** Wymagany ranking. */
    public int requiredRank = 0;

    public GuildItemSet() {
    }

    public Map<String, Integer> getItems() {
        return items != null ? items : Collections.emptyMap();
    }

}
