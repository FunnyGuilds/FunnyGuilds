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
    
    /**
     * Gets validated rows value (clamped to 1-6 range).
     * @return rows value between 1 and 6
     */
    public int getValidatedRows() {
        return Math.max(1, Math.min(6, this.rows));
    }

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
    @Comment("Konfiguracja itemu otwierającego menu efektów dla członków gildii")
    public EffectsMenuItem effectsMenuItem = new EffectsMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class EffectsMenuItem extends OkaeriConfig {

        @Comment("Czy item efektów ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 16;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.BREWING_STAND;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&d&lEFEKTY DLA CZŁONKÓW GILDII");

        @Comment("")
        @Comment("Opis itemu")
        public List<RawString> lore = RawString.listOf(
                "&7Kup efekty dla wszystkich",
                "&7członków Twojej gildii!",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja GUI efektów gildii")
    public GuildEffects effects = new GuildEffects();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class GuildEffects extends OkaeriConfig {

        @Comment("Czy efekty dla gildii mają być włączone")
        public boolean enabled = true;

        @Comment("")
        @Comment("Tytuł GUI efektów")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString title = new RawString("&d&lEFEKTY GILDII {TAG}");

        @Comment("")
        @Comment("Ilość wierszy w GUI efektów (1-6)")
        public int rows = 3;
        
        /**
         * Gets validated rows value (clamped to 1-6 range).
         * @return rows value between 1 and 6
         */
        public int getValidatedRows() {
            return Math.max(1, Math.min(6, this.rows));
        }

        @Comment("")
        @Comment("Konfiguracja itemu powrotu do głównego panelu")
        public BackItem backItem = new BackItem();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class BackItem extends OkaeriConfig {

            @Comment("Czy item powrotu ma być włączony")
            public boolean enabled = true;

            @Comment("")
            @Comment("Slot itemu (0-53)")
            public int slot = 22;

            @Comment("")
            @Comment("Typ materiału itemu")
            public Material material = Material.ARROW;

            @Comment("")
            @Comment("Nazwa itemu")
            public RawString name = new RawString("&c&lPowrót");

            @Comment("")
            @Comment("Opis itemu")
            public List<RawString> lore = RawString.listOf(
                    "&7Kliknij, aby wrócić",
                    "&7do głównego panelu."
            );
        }

        @Comment("")
        @Comment("Konfiguracja efektu siły")
        public EffectItem strength = new EffectItem(
                10, 
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
                12,
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
                14,
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
                16,
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

        /**
         * Gets the PotionEffectType for this effect.
         * @return the PotionEffectType or null if the effectType is invalid
         */
        public PotionEffectType getPotionEffectType() {
            PotionEffectType type = PotionEffectType.getByName(this.effectType);
            if (type == null) {
                net.dzikoysk.funnyguilds.FunnyGuilds.getPluginLogger().warning(
                    "Unknown potion effect type: " + this.effectType + ". Check your panel configuration."
                );
            }
            return type;
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

    @Comment("")
    @Comment("Konfiguracja itemu otwierającego menu regeneracji terenu gildii")
    public RegenerationMenuItem regenerationMenuItem = new RegenerationMenuItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class RegenerationMenuItem extends OkaeriConfig {

        @Comment("Czy item regeneracji ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot itemu (0-53)")
        public int slot = 22;

        @Comment("")
        @Comment("Typ materiału itemu")
        public Material material = Material.GRASS_BLOCK;

        @Comment("")
        @Comment("Nazwa itemu")
        public RawString name = new RawString("&a&lREGENERACJA TERENU");

        @Comment("")
        @Comment("Opis itemu")
        public List<RawString> lore = RawString.listOf(
                "&7Przywróć zniszczone bloki",
                "&7na terenie Twojej gildii!",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja GUI regeneracji terenu gildii")
    public RegenerationConfig regeneration = new RegenerationConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class RegenerationConfig extends OkaeriConfig {

        @Comment("Czy system regeneracji ma być włączony")
        public boolean enabled = true;

        @Comment("")
        @Comment("Tytuł GUI regeneracji")
        @Comment("Dostępne zmienne: {TAG}, {GUILD}")
        public RawString title = new RawString("&a&lREGENERACJA TERENU {TAG}");

        @Comment("")
        @Comment("Ilość wierszy w GUI regeneracji (1-6)")
        public int rows = 6;

        @Comment("")
        @Comment("Typ kosztu regeneracji (VAULT lub ITEM)")
        public CostType costType = CostType.ITEM;

        @Comment("")
        @Comment("Cena za blok w pieniądzach (gdy costType = VAULT)")
        public double vaultPricePerBlock = 10.0;

        @Comment("")
        @Comment("Typ przedmiotu jako koszt regeneracji (gdy costType = ITEM)")
        public Material itemCostMaterial = Material.DIAMOND;

        @Comment("")
        @Comment("Ilość przedmiotów za 100 bloków (gdy costType = ITEM)")
        @Comment("Koszt jest obliczany proporcjonalnie: (ilość bloków * itemCostPer100Blocks) / 100")
        public int itemCostPer100Blocks = 64;

        @Comment("")
        @Comment("Ilość bloków przywracanych w jednym ticku (większa wartość = szybsza regeneracja, ale większe obciążenie serwera)")
        public int batchSize = 10;

        @Comment("")
        @Comment("Minimalna ilość bloków do regeneracji w jednej operacji")
        public int minBlocksToRegenerate = 1;

        @Comment("")
        @Comment("Maksymalna ilość bloków do regeneracji w jednej operacji (0 = bez limitu)")
        public int maxBlocksToRegenerate = 0;

        @Comment("")
        @Comment("Maksymalny czas przechowywania zniszczonych bloków do regeneracji")
        @Comment("Po upływie tego czasu bloki automatycznie znikają z listy i nie można ich zregenerować")
        @Comment("Format: np. 4h (4 godziny), 30m (30 minut), 1d (1 dzień)")
        @Comment("Ustaw 0 aby wyłączyć limit czasowy (bloki będą przechowywane do restartu serwera)")
        public Duration maxBlockAge = Duration.ofHours(4);

        @Comment("")
        @Comment("Konfiguracja itemu informacyjnego o regeneracji")
        public RegenerationInfoItem infoItem = new RegenerationInfoItem();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class RegenerationInfoItem extends OkaeriConfig {

            @Comment("Slot itemu (0-53)")
            public int slot = 4;

            @Comment("")
            @Comment("Typ materiału itemu")
            public Material material = Material.BOOK;

            @Comment("")
            @Comment("Nazwa itemu")
            @Comment("Dostępne zmienne: {BLOCKS-COUNT}")
            public RawString name = new RawString("&b&lINFORMACJE O REGENERACJI");

            @Comment("")
            @Comment("Opis itemu")
            @Comment("Dostępne zmienne: {BLOCKS-COUNT}, {SELECTED-BLOCKS}, {TOTAL-COST}")
            public List<RawString> lore = RawString.listOf(
                    "&7Zniszczone bloki: &b{BLOCKS-COUNT}",
                    "&7Wybrane do regeneracji: &a{SELECTED-BLOCKS}",
                    "",
                    "&7Koszt całkowity: &e{TOTAL-COST}",
                    "",
                    "&7Użyj przycisków poniżej,",
                    "&7aby wybrać ilość bloków."
            );
        }

        @Comment("")
        @Comment("Konfiguracja przycisków zmiany ilości")
        public AmountButtons amountButtons = new AmountButtons();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class AmountButtons extends OkaeriConfig {

            @Comment("Slot przycisku -10")
            public int slotMinus10 = 19;

            @Comment("")
            @Comment("Slot przycisku -1")
            public int slotMinus1 = 20;

            @Comment("")
            @Comment("Slot przycisku +1")
            public int slotPlus1 = 24;

            @Comment("")
            @Comment("Slot przycisku +10")
            public int slotPlus10 = 25;

            @Comment("")
            @Comment("Materiał przycisku -10")
            public Material materialMinus10 = Material.RED_STAINED_GLASS_PANE;

            @Comment("")
            @Comment("Materiał przycisku -1")
            public Material materialMinus1 = Material.ORANGE_STAINED_GLASS_PANE;

            @Comment("")
            @Comment("Materiał przycisku +1")
            public Material materialPlus1 = Material.LIME_STAINED_GLASS_PANE;

            @Comment("")
            @Comment("Materiał przycisku +10")
            public Material materialPlus10 = Material.GREEN_STAINED_GLASS_PANE;

            @Comment("")
            @Comment("Nazwa przycisku -10")
            public RawString nameMinus10 = new RawString("&c-10 bloków");

            @Comment("")
            @Comment("Nazwa przycisku -1")
            public RawString nameMinus1 = new RawString("&c-1 blok");

            @Comment("")
            @Comment("Nazwa przycisku +1")
            public RawString namePlus1 = new RawString("&a+1 blok");

            @Comment("")
            @Comment("Nazwa przycisku +10")
            public RawString namePlus10 = new RawString("&a+10 bloków");
        }

        @Comment("")
        @Comment("Konfiguracja przycisków presetów (25%, 50%, 100%)")
        public PresetButtons presetButtons = new PresetButtons();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class PresetButtons extends OkaeriConfig {

            @Comment("Slot przycisku 25%")
            public int slot25 = 29;

            @Comment("")
            @Comment("Slot przycisku 50%")
            public int slot50 = 31;

            @Comment("")
            @Comment("Slot przycisku 100%")
            public int slot100 = 33;

            @Comment("")
            @Comment("Materiał przycisków presetów")
            public Material material = Material.YELLOW_STAINED_GLASS_PANE;

            @Comment("")
            @Comment("Nazwa przycisku 25%")
            public RawString name25 = new RawString("&e25% bloków");

            @Comment("")
            @Comment("Nazwa przycisku 50%")
            public RawString name50 = new RawString("&e50% bloków");

            @Comment("")
            @Comment("Nazwa przycisku 100%")
            public RawString name100 = new RawString("&e100% bloków");
        }

        @Comment("")
        @Comment("Konfiguracja przycisku potwierdzenia")
        public ConfirmButton confirmButton = new ConfirmButton();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class ConfirmButton extends OkaeriConfig {

            @Comment("Slot przycisku potwierdzenia")
            public int slot = 49;

            @Comment("")
            @Comment("Materiał przycisku")
            public Material material = Material.EMERALD_BLOCK;

            @Comment("")
            @Comment("Nazwa przycisku")
            public RawString name = new RawString("&a&lPOTWIERDŹ REGENERACJĘ");

            @Comment("")
            @Comment("Opis przycisku")
            @Comment("Dostępne zmienne: {SELECTED-BLOCKS}, {TOTAL-COST}")
            public List<RawString> lore = RawString.listOf(
                    "&7Kliknij, aby rozpocząć",
                    "&7regenerację &a{SELECTED-BLOCKS} &7bloków.",
                    "",
                    "&7Koszt: &e{TOTAL-COST}"
            );
        }

        @Comment("")
        @Comment("Konfiguracja przycisku anulowania")
        public CancelButton cancelButton = new CancelButton();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class CancelButton extends OkaeriConfig {

            @Comment("Slot przycisku anulowania")
            public int slot = 45;

            @Comment("")
            @Comment("Materiał przycisku")
            public Material material = Material.REDSTONE_BLOCK;

            @Comment("")
            @Comment("Nazwa przycisku")
            public RawString name = new RawString("&c&lANULUJ");

            @Comment("")
            @Comment("Opis przycisku")
            public List<RawString> lore = RawString.listOf(
                    "&7Kliknij, aby wrócić",
                    "&7do panelu gildii."
            );
        }

        @Comment("")
        @Comment("Konfiguracja itemu powrotu")
        public BackItem backItem = new BackItem();

        @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
        public static class BackItem extends OkaeriConfig {

            @Comment("Czy item powrotu ma być włączony")
            public boolean enabled = true;

            @Comment("")
            @Comment("Slot itemu (0-53)")
            public int slot = 45;

            @Comment("")
            @Comment("Typ materiału itemu")
            public Material material = Material.ARROW;

            @Comment("")
            @Comment("Nazwa itemu")
            public RawString name = new RawString("&c&lPowrót");

            @Comment("")
            @Comment("Opis itemu")
            public List<RawString> lore = RawString.listOf(
                    "&7Kliknij, aby wrócić",
                    "&7do głównego panelu."
            );
        }
    }
}
