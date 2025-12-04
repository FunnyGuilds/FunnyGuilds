package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.time.Duration;
import java.util.List;
import net.dzikoysk.funnyguilds.config.RawString;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PanelConfiguration extends OkaeriConfig {

    @Comment("Czy panel zarządzania gildią ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Tytuł GUI panelu gildii")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}")
    public RawString title = new RawString("&b&lPANEL GILDII {TAG}");

    @Comment("")
    @Comment("Ilość wierszy w GUI (1-6)")
    public int rows = 3;

    @Comment("")
    @Comment("Konfiguracja itemu informacyjnego o gildii")
    public InfoItem infoItem = new InfoItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class InfoItem extends OkaeriConfig {

        @Comment("Czy item informacyjny ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 4;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.NETHER_STAR;

        @Comment("")
        @Comment("Nazwa itemu")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString name = new RawString("&b&lGILDIA:");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}, {POINTS}, {KILLS}, {DEATHS}, {ASSISTS}, {MEMBERS}, {VALIDITY}, {LIVES}")
        public List<RawString> lore = RawString.listOf(
                "&7Tag: &b{TAG}",
                "&7Nazwa: &b{GUILD}",
                "&7Punkty: &b{POINTS}",
                "&7Zabójstwa: &b{KILLS}",
                "&7Śmierci: &b{DEATHS}",
                "&7Asysty: &b{ASSISTS}",
                "&7Członków: &b{MEMBERS}",
                "&7Ważność: &b{VALIDITY}",
                "&7Żyć: &b{LIVES}"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu przedłużenia gildii")
    public ExtendItem extendItem = new ExtendItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class ExtendItem extends OkaeriConfig {

        @Comment("Czy item przedłużenia ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 10;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.CLOCK;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&a&lPrzedłuż gildię");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {VALIDITY}, {EXTEND-TIME}, {PRICE}")
        public List<RawString> lore = RawString.listOf(
                "&7Aktualna ważność: &a{VALIDITY}",
                "&7Przedłużenie o: &a{EXTEND-TIME}",
                "",
                "&7Koszt: &e{PRICE}",
                "",
                "&aKliknij, aby przedłużyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu powiększenia gildii")
    public EnlargeItem enlargeItem = new EnlargeItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EnlargeItem extends OkaeriConfig {

        @Comment("Czy item powiększenia ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 12;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.GRASS_BLOCK;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&a&lPowiększ teren");

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {CURRENT-SIZE}, {NEW-SIZE}, {PRICE}")
        public List<RawString> lore = RawString.listOf(
                "&7Aktualny rozmiar: &a{CURRENT-SIZE}",
                "&7Nowy rozmiar: &a{NEW-SIZE}",
                "",
                "&7Koszt: &e{PRICE}",
                "",
                "&aKliknij, aby powiększyć!"
        );

        @Comment("")
        @Comment("Opis gdy osiągnięto maksymalny rozmiar")
        public List<RawString> maxSizeLore = RawString.listOf(
                "&7Aktualny rozmiar: &a{CURRENT-SIZE}",
                "",
                "&cOsiągnięto maksymalny rozmiar!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja itemu PvP w gildii")
    public PvpItem pvpItem = new PvpItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PvpItem extends OkaeriConfig {

        @Comment("Czy item PvP ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 14;

        @Comment("")
        @Comment("Typ materiału itemu gdy PvP jest włączone")
        public Material materialOn = Material.DIAMOND_SWORD;

        @Comment("")
        @Comment("Typ materiału itemu gdy PvP jest wyłączone")
        public Material materialOff = Material.WOODEN_SWORD;

        @Comment("")
        @Comment("Nazwa itemu gdy PvP jest włączone")
        public RawString nameOn = new RawString("&c&lPvP WŁĄCZONE");

        @Comment("")
        @Comment("Nazwa itemu gdy PvP jest wyłączone")
        public RawString nameOff = new RawString("&a&lPvP WYŁĄCZONE");

        @Comment("")
        @Comment("Opis itemu gdy PvP jest włączone")
        public List<RawString> loreOn = RawString.listOf(
                "&7Status: &cWŁĄCZONE",
                "",
                "&7Członkowie gildii mogą",
                "&7zadawać sobie obrażenia.",
                "",
                "&aKliknij, aby wyłączyć!"
        );

        @Comment("")
        @Comment("Opis itemu gdy PvP jest wyłączone")
        public List<RawString> loreOff = RawString.listOf(
                "&7Status: &aWYŁĄCZONE",
                "",
                "&7Członkowie gildii nie mogą",
                "&7zadawać sobie obrażeń.",
                "",
                "&cKliknij, aby włączyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja efektów gildii")
    public GuildEffects effects = new GuildEffects();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class GuildEffects extends OkaeriConfig {

        @Comment("Czy efekty dla gildii mają być włączone")
        public boolean enabled = true;

        @Comment("")
        @Comment("Konfiguracja efektu siły")
        public EffectItem strength = new EffectItem(
                16, 
                Material.BLAZE_POWDER,
                "&c&lSiła",
                "STRENGTH",
                1,
                Duration.ofMinutes(5),
                CostType.VAULT,
                500.0,
                Material.DIAMOND,
                0
        );

        @Comment("")
        @Comment("Konfiguracja efektu szybkości")
        public EffectItem speed = new EffectItem(
                18,
                Material.SUGAR,
                "&b&lSzybkość",
                "SPEED",
                1,
                Duration.ofMinutes(5),
                CostType.VAULT,
                500.0,
                Material.DIAMOND,
                0
        );

        @Comment("")
        @Comment("Konfiguracja efektu odporności na ogień")
        public EffectItem fireResistance = new EffectItem(
                20,
                Material.MAGMA_CREAM,
                "&6&lOdporność na ogień",
                "FIRE_RESISTANCE",
                0,
                Duration.ofMinutes(5),
                CostType.VAULT,
                500.0,
                Material.DIAMOND,
                0
        );

        @Comment("")
        @Comment("Konfiguracja efektu regeneracji")
        public EffectItem regeneration = new EffectItem(
                22,
                Material.GHAST_TEAR,
                "&d&lRegeneracja",
                "REGENERATION",
                1,
                Duration.ofMinutes(5),
                CostType.VAULT,
                500.0,
                Material.DIAMOND,
                0
        );
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EffectItem extends OkaeriConfig {

        @Comment("Czy efekt ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material;

        @Comment("")
        @Comment("Nazwa itemu")
        @Comment("Dostępne zmienne: {DURATION}, {AMPLIFIER}, {PRICE}")
        public RawString name;

        @Comment("")
        @Comment("Opis itemu")
        @Comment("Dostępne zmienne: {DURATION}, {AMPLIFIER}, {PRICE}")
        public List<RawString> lore = RawString.listOf(
                "&7Czas trwania: &a{DURATION}",
                "&7Poziom: &a{AMPLIFIER}",
                "",
                "&7Koszt: &e{PRICE}",
                "",
                "&aKliknij, aby kupić dla całej gildii!"
        );

        @Comment("")
        @Comment("Typ efektu (nazwa z PotionEffectType)")
        public String effectType;

        @Comment("")
        @Comment("Poziom efektu (amplifier, 0 = I, 1 = II, etc.)")
        public int amplifier;

        @Comment("")
        @Comment("Czas trwania efektu")
        public Duration duration;

        @Comment("")
        @Comment("Typ kosztu (VAULT lub ITEM)")
        public CostType costType;

        @Comment("")
        @Comment("Cena w pieniądzach (gdy costType = VAULT)")
        public double vaultPrice;

        @Comment("")
        @Comment("Typ przedmiotu jako koszt (gdy costType = ITEM)")
        public Material itemCostMaterial;

        @Comment("")
        @Comment("Ilość przedmiotów jako koszt (gdy costType = ITEM)")
        public int itemCostAmount;

        public EffectItem() {
            // Domyślny konstruktor dla konfiguracji
        }

        public EffectItem(int slot, Material material, String name, String effectType, int amplifier,
                          Duration duration, CostType costType, double vaultPrice, Material itemCostMaterial, int itemCostAmount) {
            this.slot = slot;
            this.material = material;
            this.name = new RawString(name);
            this.effectType = effectType;
            this.amplifier = amplifier;
            this.duration = duration;
            this.costType = costType;
            this.vaultPrice = vaultPrice;
            this.itemCostMaterial = itemCostMaterial;
            this.itemCostAmount = itemCostAmount;
        }

        public PotionEffectType getPotionEffectType() {
            return PotionEffectType.getByName(this.effectType);
        }
    }

    public enum CostType {
        VAULT,
        ITEM
    }

    @Comment("")
    @Comment("Konfiguracja wypełnienia pustych slotów")
    public FillItem fillItem = new FillItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class FillItem extends OkaeriConfig {

        @Comment("Czy puste sloty mają być wypełnione")
        public boolean enabled = true;

        @Comment("")
        @Comment("Typ materiału wypełnienia")
        public Material material = Material.GRAY_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Nazwa itemu wypełnienia")
        public RawString name = new RawString(" ");
    }
}
