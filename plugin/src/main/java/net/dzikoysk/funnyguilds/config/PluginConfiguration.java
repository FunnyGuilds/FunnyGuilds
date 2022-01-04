package net.dzikoysk.funnyguilds.config;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.DecimalMax;
import eu.okaeri.validator.annotation.DecimalMin;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.NotBlank;
import eu.okaeri.validator.annotation.Pattern;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.config.sections.MysqlConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TntProtectionConfiguration;
import net.dzikoysk.funnyguilds.feature.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarOptions;
import net.dzikoysk.funnyguilds.guild.GuildRegex;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.rank.RankUtils;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("          FunnyGuilds           #")
@Header("         4.10.0 Tribute          #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderow znajduje sie tutaj:")
@Header("https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders#funnyguilds")
@Header(" ")
@Header("FunnyGuilds wspiera takze placeholdery w BungeeTabListPlus i MVdWPlaceholderAPI")
@Header("Placeholdery sa dokladnie takie same jak w przypadku PlaceholderAPI (bez znaku % oczywiscie)")
@Header(" ")
@Header("Jezeli chcesz, aby dana wiadomosc byla pusta, zamiast wiadomosci umiesc: ''")
@Header(" ")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfiguration extends OkaeriConfig {

    @Exclude
    public final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @Comment("Wyswietlana nazwa pluginu")
    public String pluginName = "FunnyGuilds";

    @Comment("Czy plugin ma dzialac w trybie debug. Sluzy on do wysylania dodatkowych wiadomosci w celu zdiagnozowania bledow itp.")
    public boolean debugMode = false;

    @Comment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    public boolean updateInfo = true;

    @Comment("Czy informacje o aktualizacji wersji nightly maja byc widoczne podczas wejscia na serwer")
    @Comment("Ta opcja działa tylko wtedy, gdy także jest włączona opcja 'update-info'")
    public boolean updateNightlyInfo = true;

    @Comment("Mozliwosc zakladania gildii. Mozna ja zmienic takze za pomoca komendy /ga enabled")
    public boolean guildsEnabled = true;

    @Comment("Czy tworzenie regionow gildii, oraz inne zwiazane z nimi rzeczy, maja byc wlaczone")
    @Comment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @Comment("Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @Comment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @Comment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    public boolean regionsEnabled = true;

    @Comment("Bloki, ktore mozna stawiac na terenie gildii niezaleznie od tego, czy jest się jej czlonkiem.")
    @Comment("Zostaw puste, aby wylaczyc.")
    @Comment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    public Set<Material> placingBlocksBypassOnRegion = Collections.emptySet();

    @Comment("Zablokuj rozlewanie się wody i lawy poza terenem gildii")
    @Comment("Dziala tylko jesli regiony sa wlaczone")
    @CustomKey("water-and-lava-flow-only-for-regions")
    public boolean blockFlow = false;

    @Comment("Czy gracz po smierci ma sie pojawiac w bazie swojej gildii")
    @Comment("Dziala tylko jesli regiony sa wlaczone")
    public boolean respawnInBase = true;

    @Min(1)
    @Comment("Maksymalna dlugosc nazwy gildii")
    @CustomKey("name-length")
    public int createNameLength = 22;

    @Min(1)
    @Comment("Minimalna dlugosc nazwy gildii")
    @CustomKey("name-min-length")
    public int createNameMinLength = 4;

    @Min(1)
    @Comment("Maksymalna dlugosc tagu gildii")
    @CustomKey("tag-length")
    public int createTagLength = 4;

    @Min(1)
    @Comment("Minimalna dlugosc tagu gildii")
    @CustomKey("tag-min-length")
    public int createTagMinLength = 2;

    @Comment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @Comment("Dostepne zasady:")
    @Comment("LOWERCASE - umozliwia uzycie tylko malych liter")
    @Comment("UPPERCASE - umozliwia uzycie tylko duzych liter")
    @Comment("DIGITS - umozliwia uzycie tylko cyfr")
    @Comment("LOWERCASE_DIGITS - umozliwia uzycie malych liter i cyfr")
    @Comment("UPPERCASE_DIGITS - umozliwia uzycie duzych liter i cyfr")
    @Comment("LETTERS - umozliwia uzycie malych i duzych liter")
    @Comment("LETTERS_DIGITS - umozliwia uzycie malych i duzych liter oraz cyrf")
    @Comment("LETTERS_DIGITS_UNDERSCORE - umozliwia uzycie malych i duzych liter, cyrf oraz podkreslnika")
    @CustomKey("name-regex")
    public GuildRegex nameRegex = GuildRegex.LETTERS;

    @Comment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @Comment("Mozliwe zasady sa takie same jak w przypadku name-regex")
    public GuildRegex tagRegex = GuildRegex.LETTERS;

    @Min(0)
    @Comment("Minimalna liczba graczy w gildii, aby zaliczala sie ona do rankingu")
    @CustomKey("guild-min-members")
    public int minMembersToInclude = 1;

    @Comment("Czy wiadomosci o braku potrzebnych przedmiotow maja zawierac elementy, na ktore mozna najechac")
    @Comment("Takie elementy pokazuja informacje o przedmiocie, np. jego typ, nazwe czy opis")
    @Comment("Funkcja jest obecnie troche niedopracowana i moze powodowac problemy na niektorych wersjach MC, np. 1.8.8")
    public boolean enableItemComponent = false;

    @Comment("Przedmioty wymagane do zalozenia gildii")
    @Comment("Tylko wartosci ujete w <> sa wymagane, reszta, ujeta w [], jest opcjonalna")
    @Comment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant:eggtype:skullowner:armorcolor:flags]")
    @Comment("Przyklad: \"5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!\"")
    @Comment(" ")
    @Comment("Zamiast spacji wstawiaj podkreslnik: _")
    @Comment("Aby zrobic nowa linie lore wstaw hash: #")
    @Comment("Aby w lore uzyc znaku # wstaw {HASH}")
    @Comment(" ")
    @Comment("eggtype to typ jajka do spawnu moba, uzywane tylko gdy typem przedmiotu jest MONSTER_EGG")
    @Comment("skullowner to nick gracza, ktorego glowa jest tworzona, uzywane tylko gdy typem przedmiotu jest SKULL_ITEM")
    @Comment("armorcolor to kolor, w ktorym bedzie przedmiot, uzywane tylko gdy przedmiot jest czescia zbroi skorzanej")
    @Comment("flags to flagi, ktore maja byc nalozone na przedmiot. Dostepne flagi: HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS")
    @Comment("Kolor musi byc podany w postaci: \"R_G_B\"")
    @Comment(" ")
    @Comment(
            "UWAGA: Nazwy przedmiotow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @Comment(
            "UWAGA: Typ jajka musi pasowac do typow entity podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @CustomKey("items")
    public List<String> items_ = Arrays.asList("5 stone", "5 dirt", "5 tnt");

    @Exclude
    public List<ItemStack> createItems;

    @Min(0)
    @Comment("Wymagana ilosc doswiadczenia do zalozenia gildii")
    public int requiredExperience = 0;

    @Min(0)
    @Comment("Wymagana ilosc pieniedzy do zalozenia gildii")
    @Comment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    public double requiredMoney = 0;

    @Comment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CustomKey("items-vip")
    public List<String> itemsVip_ = Collections.singletonList("1 gold_ingot");

    @Exclude
    public List<ItemStack> createItemsVip;

    @Min(0)
    @Comment("Wymagana ilosc doswiadczenia do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    public int requiredExperienceVip = 0;

    @Min(0)
    @Comment("Wymagana ilosc pieniedzy do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @Comment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    public double requiredMoneyVip = 0;

    @Comment("Czy opcja wymaganego rankingu do zalozenia gildii ma byc wlaczona?")
    public boolean rankCreateEnable = true;

    @Comment("Minimalny ranking wymagany do zalozenia gildii")
    public int rankCreate = 1000;

    @Comment("Minimalny ranking wymagany do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.rank")
    public int rankCreateVip = 800;

    @Comment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @Comment(
            "Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    public boolean useCommonGUI = false;

    @Comment("GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @Comment("Jesli wlaczone jest use-common-gui - ponizsze GUI jest uzywane takze dla osob z uprawnieniem funnyguilds.vip.items")
    @Comment(
            "Kazda linijka listy oznacza jeden slot, liczba slotow powinna byc wielokrotnoscia liczby 9 i nie powinna byc wieksza niz 54")
    @Comment(
            "Aby uzyc przedmiotu stworzonego w jednym slocie w innym mozna uzyc {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, ktory jest w pierwszym slocie")
    @Comment("Aby wstawic przedmiot na gildie nalezy uzyc {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildie")
    @Comment("Aby wstawic przedmiot na gildie z listy vip nalezy uzyc {VIPITEM-nr}")
    @CustomKey("gui-items")
    public List<String> guiItems_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItems;

    @Comment("Nazwa GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @Comment("Nazwa moze zawierac max. 32 znaki, wliczajac w to kody kolorow")
    @CustomKey("gui-items-title")
    public String guiItemsTitle_ = "&5&lPrzedmioty na gildie";

    @Exclude
    public String guiItemsTitle;

    @Comment("GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @Comment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @Comment("Ponizsze GUI bedzie ignorowane jesli wlaczone jest use-common-gui")
    @CustomKey("gui-items-vip")
    public List<String> guiItemsVip_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItemsVip;

    @Comment("Nazwa GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @Comment("Nazwa moze zawierac max. 32 znaki, wliczajac w to kody kolorow")
    @CustomKey("gui-items-vip-title")
    public String guiItemsVipTitle_ = "&5&lPrzedmioty na gildie (VIP)";

    @Exclude
    public String guiItemsVipTitle;

    @Comment("Zmiana nazwy i koloru przedmiotow na gildie (nie ma znaczenia uprawnienie funnyguilds.vip.items)")
    @Comment("Jesli nie chcesz uzywać tej funkcji, to pozostaw gui-items-name: \"\"")
    @Comment("{ITEM} - nazwa przedmiotu (np. 1 golden_apple)")
    @Comment("{ITEM-NO-AMOUNT} - nazwa przedmiotu bez liczby. (np. golden_apple)")
    @CustomKey("gui-items-name")
    public String guiItemsName_ = "&7>> &a{ITEM-NO-AMOUNT} &7<<";

    @Exclude
    public String guiItemsName;

    @Comment("Czy do przedmiotow na gildie, ktore sa w GUI, maja byc dodawane dodatkowe linie opisu?")
    @Comment("Linie te mozna ustawic ponizej")
    public boolean addLoreLines = true;

    @Comment("Dodatkowe linie opisu, dodawane do kazdego przedmiotu, ktory jest jednoczesnie przedmiotem na gildie")
    @Comment("Dodawane linie nie zaleza od otwieranego GUI - sa wspolne dla zwyklego i VIP")
    @Comment("Mozliwe do uzycia zmienne:")
    @Comment("{REQ-AMOUNT} - calkowita wymagana ilosc przedmiotu")
    @Comment("{PINV-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie")
    @Comment("{PINV-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie")
    @Comment("{EC-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma w enderchescie")
    @Comment("{EC-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma w enderchescie")
    @Comment("{ALL-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie i w enderchescie")
    @Comment("{ALL-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    @CustomKey("gui-items-lore")
    public List<String> guiItemsLore_ = Arrays.asList("", "&aPosiadzasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
            "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");

    @Exclude
    public List<String> guiItemsLore;

    @Comment("Minimalna odleglosc od spawnu")
    public int createDistance = 100;

    @Comment("Minimalna odleglosc od regionu gildii do granicy mapy")
    @CustomKey("create-guild-min-distance")
    public double createMinDistanceFromBorder = 50;

    @Comment("Konfiguracja serca")
    @CustomKey("heart-configuration")
    public HeartConfiguration heart = new HeartConfiguration();

    @Comment("Typy blokow, z ktorymi osoba spoza gildii NIE moze prowadzic interakcji na terenie innej gildii")
    public List<Material> blockedInteract = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST);

    @Comment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    public boolean buggedBlocks = false;

    @Min(0)
    @Comment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete")
    @Comment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    public long buggedBlocksTimer = 20L;

    @Comment("Bloki, ktorych nie mozna 'bugowac'")
    @Comment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CustomKey("bugged-blocks-exclude")
    public List<String> buggedBlocksExclude_ = Arrays.asList(
            // Ban basic
            "TNT", "STATIONARY_LAVA", "STATIONARY_WATER",
            // Ban TNT Minecart placement
            "RAILS", "DETECTOR_RAIL", "ACTIVATOR_RAIL", "POWERED_RAIL",
            // Ban gravity blocks that won't be removed when fallen
            "ANVIL", "GRAVEL", "SAND", "DRAGON_EGG",
            // Ban pistons and other components that may produce redstone output or interact with it
            "PISTON_BASE", "PISTON_STICKY_BASE",
            "REDSTONE_BLOCK", "REDSTONE_TORCH_ON", "REDSTONE_TORCH_OFF", "DIODE", "REDSTONE_COMPARATOR", "DAYLIGHT_DETECTOR",
            "DISPENSER", "HOPPER", "DROPPER", "OBSERVER",
            "STONE_PLATE", "WOOD_PLATE", "GOLD_PLATE", "IRON_PLATE", "LEVER", "TRIPWIRE_HOOK", "TRAP_DOOR", "IRON_TRAPDOOR", "WOOD_BUTTON", "STONE_BUTTON",
            "WOOD_DOOR", "IRON_DOOR", "SPRUCE_DOOR_ITEM", "BIRCH_DOOR_ITEM", "JUNGLE_DOOR_ITEM", "ACACIA_DOOR_ITEM", "DARK_OAK_DOOR_ITEM",
            "FENCE_GATE", "SPRUCE_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "BIRCH_FENCE_GATE",
            "REDSTONE_LAMP_ON", "REDSTONE_LAMP_OFF",
            "TRAPPED_CHEST", "CHEST"
    );

    @Exclude
    public Set<Material> buggedBlocksExclude;

    @Comment("Czy klocki po 'zbugowaniu' maja zostac oddane")
    @CustomKey("bugged-blocks-return")
    public boolean buggedBlockReturn = false;

    @Min(1)
    @Comment("Maksymalna liczba czlonkow w gildii")
    @CustomKey("max-members")
    public int maxMembersInGuild = 15;

    @Min(0)
    @Comment("Maksymalna liczba sojuszy miedzy gildiami")
    @CustomKey("max-allies")
    public int maxAlliesBetweenGuilds = 15;

    @Min(0)
    @Comment("Maksymalna liczba wojen miedzy gildiami")
    @CustomKey("max-enemies")
    public int maxEnemiesBetweenGuilds = 15;

    @Comment("Lista nazw swiatow, na ktorych mozliwosc utworzenia gildii powinna byc zablokowana")
    @CustomKey("blocked-worlds")
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    @Comment("Mozliwosc ucieczki z terenu innej gildii")
    @Comment("Funkcja niedostepna jesli mozliwosc teleportacji do gildii jest wylaczona")
    public boolean escapeEnable = true;

    @Min(0)
    @Comment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    public int escapeDelay = 120;

    @Comment("Mozliwosc ucieczki na spawn dla graczy bez gildii")
    public boolean escapeSpawn = true;

    @Comment("Mozliwosc teleportacji do gildii")
    public boolean baseEnable = true;

    @PositiveOrZero
    @Comment("Czas oczekiwania na teleportacje, w sekundach")
    public Duration baseDelay = Duration.ofSeconds(5);

    @PositiveOrZero
    @Comment("Czas oczekiwania na teleportacje, w sekundach, dla graczy posiadajacych uprawnienie funnyguilds.vip.baseTeleportTime")
    public Duration baseDelayVip = Duration.ofSeconds(3);

    @Comment("Koszt teleportacji do gildii. Jezeli teleportacja ma byc darmowa, wystarczy wpisac: base-items: []")
    @CustomKey("base-items")
    public List<String> baseItems_ = Arrays.asList("1 diamond", "1 emerald");

    @Exclude
    public List<ItemStack> baseItems;

    @Comment("Koszt dolaczenia do gildii. Jezeli dolaczenie ma byc darmowe, wystarczy wpisac: join-items: []")
    @CustomKey("join-items")
    public List<String> joinItems_ = Collections.singletonList("1 diamond");

    @Exclude
    public List<ItemStack> joinItems;

    @Comment("Mozliwosc powiekszania gildii")
    public boolean enlargeEnable = true;

    @Comment("O ile powieksza teren gildii 1 poziom")
    public int enlargeSize = 5;

    @Comment("Koszt powiekszania gildii")
    @Comment("- kazdy myslnik, to 1 poziom gildii")
    @CustomKey("enlarge-items")
    public List<String> enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @Exclude
    public List<ItemStack> enlargeItems;

    @Min(1)
    @Comment("Wielkosc regionu gildii")
    public int regionSize = 50;

    @Min(0)
    @Comment("Minimalna odleglosc miedzy terenami gildii")
    public int regionMinDistance = 10;

    @Positive
    @Comment("Czas wyswietlania powiadomienia na pasku powiadomien, w sekundach")
    public Duration regionNotificationTime = Duration.ofSeconds(15);

    @Min(1)
    @Comment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza, w sekundach")
    public int regionNotificationCooldown = 60;

    @Comment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CustomKey("region-commands")
    public List<String> regionCommands = Collections.singletonList("sethome");

    @Comment("Czy proces usuniecia gildii powinien zostac przerwany jezeli ktos spoza gildii jest na jej terenie")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    public TntProtectionConfiguration tntProtection = new TntProtectionConfiguration();

    @Min(0)
    @Comment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    public int regionExplode = 120;

    @Comment("Czy blokada po wybuchu ma obejmowac rowniez budowanie")
    public boolean regionExplodeBlockBreaking = false;

    @Comment("Czy blokada po wybuchu ma obejmowac rowniez interakcje z blocked-interact")
    public boolean regionExplodeBlockInteractions = false;

    @Min(0)
    @Comment("Zasieg pobieranych przedmiotow po wybuchu. Jezeli chcesz wylaczyc, wpisz 0")
    public int explodeRadius = 3;

    @Comment("Jakie materialy i z jaka szansa maja byc niszczone po wybuchu")
    @Comment("<material>: <szansa (w %)")
    @Comment("Jeżeli wszystkie materialy maja miec okreslony % na wybuch, uzyj specjalnego znaku '*'")
    @CustomKey("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of(
            "ender_chest", 20.0,
            "enchantment_table", 20.0,
            "obsidian", 20.0,
            "water", 33.0,
            "lava", 33.0
    );
    @Exclude
    public Map<Material, Double> explodeMaterials;
    @Exclude
    public boolean allMaterialsAreExplosive;
    @Exclude
    public double defaultExplodeChance = -1.0;

    @Comment("Czy powstale wybuchy powinny niszczyc bloki wylacznie na terenach gildii")
    public boolean explodeShouldAffectOnlyGuild = false;

    @Comment("Możliwość podbijania gildii")
    public boolean warEnabled = true;

    @Min(1)
    @Comment("Ile zyc ma gildia")
    public int warLives = 3;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CustomKey("war-protection")
    public Duration warProtection = Duration.ofHours(24);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CustomKey("war-wait")
    public Duration warWait = Duration.ofHours(24);

    @Comment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    public boolean warTntProtection = true;

    @Comment("Czy zwierzeta na terenie gildii maja byc chronione przed osobami spoza gildii")
    public boolean animalsProtection = false;

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("Jaka waznosc ma gildia po jej zalozeniu")
    @CustomKey("validity-start")
    public Duration validityStart = Duration.ofDays(14);

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("Ile czasu dodaje przedluzenie gildii")
    @CustomKey("validity-time")
    public Duration validityTime = Duration.ofDays(14);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("Ile dni przed koncem wygasania mozna przedluzyc gildie. Wpisz 0, jezeli funkcja ma byc wylaczona")
    @CustomKey("validity-when")
    public Duration validityWhen = Duration.ofDays(14);

    @Comment("Koszt przedluzenia gildii")
    @CustomKey("validity-items")
    public List<String> validityItems_ = Collections.singletonList("10 diamond");
    @Exclude
    public List<ItemStack> validityItems;

    @Comment("Czy wiadomosc o zabiciu gracza powinna byc pokazywana wszystkim")
    @Comment("Jesli wylaczone - bedzie pokazywana tylko graczom, ktorzy brali udzial w walce")
    public boolean broadcastDeathMessage = true;

    @Comment("Czy wiadomosc o zabiciu gracza powinna byc wyswietlana bez wzgledu na wylaczone wiadomosci o smierci")
    @CustomKey("ignore-death-messages-disabled")
    public boolean ignoreDisabledDeathMessages = false;

    @Comment("Ranking od ktorego rozpoczyna gracz")
    public int rankStart = 1000;

    @Comment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    public boolean rankFarmingProtect = true;

    @Comment("Czy ostatni gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CustomKey("rank-farming-last-attacker-as-killer")
    public boolean considerLastAttackerAsKiller = false;

    @Min(0)
    @Comment("Przez ile sekund gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CustomKey("rank-farming-consideration-timeout")
    public int lastAttackerAsKillerConsiderationTimeout = 30;

    @Exclude
    public long lastAttackerAsKillerConsiderationTimeout_;

    @Min(0)
    @Comment("Czas w sekundach blokady nabijania rankingu po walce dwoch osob")
    public int rankFarmingCooldown = 7200;

    @Comment("Czy ma byc zablokowana zmiana rankingu, jesli obie osoby z walki maja taki sam adres IP")
    public boolean rankIPProtect = false;

    @Comment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzglednieni przy wyznaczaniu pozycji gracza w rankingu")
    @CustomKey("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    @Min(1)
    @Comment("Co ile ticków ranking graczy oraz gildii powinien zostać odświeżony")
    public int rankingUpdateInterval = 40;

    @Comment("Czy system asyst ma byc wlaczony")
    @CustomKey("rank-assist-enable")
    public boolean assistEnable = true;

    @Min(-1)
    @Comment("Limit asyst. Wpisz liczbe ujemna aby wylaczyc")
    @CustomKey("assists-limit")
    public int assistsLimit = -1;

    @DecimalMin("0")
    @DecimalMax("1")
    @Comment("Jaka czesc rankingu za zabicie idzie na konto zabojcy")
    @Comment("1 to caly ranking, 0 to nic")
    @Comment("Reszta rankingu rozdzielana jest miedzy osoby asystujace w zaleznosci od zadanych obrazen")
    @CustomKey("rank-assist-killer-share")
    public double assistKillerShare = 0.5;

    @Comment("Na jakich regionach ma ignorowac nadawanie asyst")
    @Comment("UWAGA: Wymagany plugin WorldGuard")
    @CustomKey("assists-regions-ignored")
    public List<String> assistsRegionsIgnored = Collections.singletonList("spawnGuildHeart");

    @Comment("System rankingowy uzywany przez plugin, do wyboru:")
    @Comment("ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @Comment("PERCENT - system, ktory obu graczom zabiera procent rankingu osoby zabitej")
    @Comment("STATIC - system, ktory zawsze zabiera iles rankingu zabijajacemu i iles zabitemu")
    @CustomKey("rank-system")
    public RankSystem.Type rankSystem = RankSystem.Type.ELO;

    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @Comment(
            "Lista stalych obliczen rankingowych ELO, uzywanych przy zmianach rankingu - im mniejsza stala, tym mniejsze zmiany rankingu")
    @Comment("Stale okreslaja tez o ile maksymalnie moze zmienic sie ranking pochodzacy z danego przedzialu")
    @Comment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @Comment("Elementy listy powinny byc postaci: \"minRank-maxRank stala\", np.: \"0-1999 32\"")
    @Comment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"2401-* 16\"")
    @CustomKey("elo-constants")
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @Exclude
    public Map<IntegerRange, Integer> eloConstants;

    @Positive
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @Comment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @Comment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CustomKey("elo-divider")
    public double eloDivider = 400.0D;

    @Positive
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @Comment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @Comment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CustomKey("elo-exponent")
    public double eloExponent = 10.0D;

    @DecimalMin("0")
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest PERCENT!")
    @Comment("Procent rankingu osoby zabitej o jaki zmienia sie rankingi po walce")
    @CustomKey("percent-rank-change")
    public double percentRankChange = 1.0;

    @Min(0)
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @Comment("Punkty dawane osobie, ktora wygrywa walke")
    @CustomKey("static-attacker-change")
    public int staticAttackerChange = 15;

    @Min(0)
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @Comment("Punkty zabierane osobie, ktora przegrywa walke")
    @CustomKey("static-victim-change")
    public int staticVictimChange = 10;

    @Comment("Czy pokazywac informacje przy kliknieciu PPM na gracza")
    @CustomKey("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @Comment("Czy pokazac informacje z komendy /gracz przy kliknieciu PPM")
    @Comment("Jesli wylaczone - pokazywane beda informacje z sekcji \"playerRightClickInfo\" z messages.yml")
    @CustomKey("info-player-command")
    public boolean infoPlayerCommand = true;

    @Comment("Cooldown pomiedzy pokazywaniem informacji przez PPM (w sekundach)")
    @CustomKey("info-player-cooldown")
    public int infoPlayerCooldown = 5;

    @Comment("Czy trzeba kucac, zeby przy klikniecu PPM na gracza wyswietlilo informacje o nim")
    @CustomKey("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @Comment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CustomKey("damage-guild")
    public boolean damageGuild = false;

    @Comment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CustomKey("damage-ally")
    public boolean damageAlly = false;

    @Comment("Wyglad znaczika {POS} wstawionego w format chatu")
    @Comment("Znacznik ten pokazuje czy ktos jest liderem, zastepca czy zwyklym czlonkiem gildii")
    @CustomKey("chat-position")
    public String chatPosition_ = "&b{POS} ";
    @Exclude
    public String chatPosition;

    @Comment("Znacznik dla lidera gildii")
    @CustomKey("chat-position-leader")
    public String chatPositionLeader = "**";

    @Comment("Znacznik dla zastepcy gildii")
    @CustomKey("chat-position-deputy")
    public String chatPositionDeputy = "*";

    @Comment("Znacznik dla czlonka gildii")
    @CustomKey("chat-position-member")
    public String chatPositionMember = "";

    @Comment("Wyglad znacznika {TAG} wstawionego w format chatu")
    @CustomKey("chat-guild")
    public String chatGuild_ = "&b{TAG} ";
    @Exclude
    public String chatGuild;

    @Comment("Wyglad znacznika {RANK} wstawionego w format chatu")
    @CustomKey("chat-rank")
    public String chatRank_ = "&b{RANK} ";
    @Exclude
    public String chatRank;

    @Comment("Wyglad znacznika {POINTS} wstawionego w format chatu")
    @Comment("Mozesz tu takze uzyc znacznika {POINTS-FORMAT}")
    @CustomKey("chat-points")
    public String chatPoints_ = "&b{POINTS} ";
    @Exclude
    public String chatPoints;

    @Comment("Wyglad znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zaleznosci od wartosci punktow")
    @Comment("{G-POINTS-FORMAT}, tak samo jak {G-POINTS} jest uzywane jedynie na liscie graczy")
    @Comment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @Comment("Elementy listy powinny byc postaci: \"minRank-maxRank wyglad\", np.: \"0-750 &4{POINTS}\"")
    @Comment("Pamietaj, aby kazdy mozliwy ranking mial ustalony format!")
    @Comment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    @CustomKey("points-format")
    public List<String> pointsFormat_ = Arrays.asList("0-749 &4{POINTS}", "750-999 &c{POINTS}", "1000-1499 &a{POINTS}", "1500-* &6&l{POINTS}");

    @Exclude
    public Map<IntegerRange, String> pointsFormat;

    @Comment("Znacznik z punktami dodawany do zmiennej {PTOP-x} i {ONLINE-PTOP-x}")
    @Comment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @Comment("Jesli nie chcesz wyswietlac punktow, tylko sam nick - nie podawaj tu nic")
    @CustomKey("ptop-points")
    public String ptopPoints_ = " &7[{POINTS}&7]";

    @Exclude
    public String ptopPoints;

    @Comment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    @Comment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @Comment("Jesli nie chcesz wyswietlac punktow, tylko sam tag - nie podawaj tu nic")
    @CustomKey("gtop-points")
    public String gtopPoints_ = " &7[&b{POINTS-FORMAT}&7]";
    @Exclude
    public String gtopPoints;

    @Comment("Wyglad znacznika {PING-FORMAT} w zaleznosci od wartosci pingu")
    @Comment("Lista powinna byc podana od najmniejszych do najwiekszych wartosci i zawierac tylko liczby naturalne, z zerem wlacznie")
    @Comment("Elementy listy powinny byc postaci: \"minPing-maxPing wyglad\", np.: \"0-75 &a{PING}\"")
    @Comment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minPing w gore, np.: \"301-* &c{PING}\"")
    @CustomKey("ping-format")
    public List<String> pingFormat_ = Arrays.asList("0-75 &a{PING}", "76-150 &e{PING}", "151-300 &c{PING}", "301-* &c{PING}");
    @Exclude
    public Map<IntegerRange, String> pingFormat;

    @NotBlank
    @Comment("Symbol od ktorego zaczyna sie wiadomosc do gildii")
    @CustomKey("chat-priv")
    public String chatPriv = "!";

    @NotBlank
    @Comment("Symbol od ktorego zaczyna sie wiadomosc do sojusznikow gildii")
    @CustomKey("chat-ally")
    public String chatAlly = "!!";

    @NotBlank
    @Comment("Symbol od ktorego zaczyna sie wiadomosc do wszystkich gildii")
    @CustomKey("chat-global")
    public String chatGlobal = "!!!";

    @Comment("Wyglad wiadomosci wysylanej na czacie gildii")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CustomKey("chat-priv-design")
    public String chatPrivDesign_ = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}";
    @Exclude
    public String chatPrivDesign;

    @Comment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CustomKey("chat-ally-design")
    public String chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";
    @Exclude
    public String chatAllyDesign;

    @Comment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CustomKey("chat-global-design")
    public String chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";
    @Exclude
    public String chatGlobalDesign;

    @Comment("Czy wiadomosci z chatow gildyjnych powinny byc wyswietlane w logach serwera")
    @CustomKey("log-guild-chat")
    public boolean logGuildChat = false;

    @Comment("Wyglad tagu osob w gildii")
    @CustomKey("prefix-our")
    public String prefixOur_ = "&a{TAG}&f ";

    @Exclude
    public String prefixOur;

    @Comment("Wyglad tagu gildii sojuszniczej")
    @CustomKey("prefix-allies")
    public String prefixAllies_ = "&6{TAG}&f ";

    @Exclude
    public String prefixAllies;

    @Comment("Wyglad tagu wrogiej gildii")
    @CustomKey("prefix-enemies")
    public String prefixEnemies_ = "&c{TAG}&f ";

    @Exclude
    public String prefixEnemies;

    @Comment("Wyglad tagu gildii neutralnej. Widziany rowniez przez graczy bez gildii")
    public String prefixOther_ = "&7{TAG}&f ";

    @Exclude
    public String prefixOther;

    @Comment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    @Comment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-offline) pusta")
    @CustomKey("ptop-online")
    public String ptopOnline_ = "&a";

    @Exclude
    public String ptopOnline;

    @Comment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    @Comment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-online) pusta")
    @CustomKey("ptop-offline")
    public String ptopOffline_ = "&c";

    @Exclude
    public String ptopOffline;

    @CustomKey("use-shared-scoreboard")
    @Comment("Czy FunnyGuilds powinno korzystac z wspoldzielonego scoreboarda")
    @Comment("Ta opcja pozwala na dzialanie pluginu FunnyGuilds oraz innych pluginow modyfikujacych scoreboard ze soba")
    @Comment("UWAGA: Opcja eksperymentalna i moze powodowac bledy przy wyswietlaniu rzeczy zaleznych od scoreboardow!")
    public boolean useSharedScoreboard = false;

    @Comment("Czy wlaczyc dummy z punktami")
    @Comment(
            "UWAGA - zalecane jest wylaczenie tej opcji w przypadku konfliktow z BungeeCord'em, wiecej szczegolow tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
    @CustomKey("dummy-enable")
    public boolean dummyEnable = true;

    @Comment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CustomKey("dummy-suffix")
    public String dummySuffix_ = "pkt";

    @Exclude
    public String dummySuffix;

    @Comment("Czy tagi gildyjne obok nicku gracza maja byc wlaczone")
    @CustomKey("guild-tag-enabled")
    public boolean guildTagEnabled = true;

    @Comment("Czy tag gildii podany przy tworzeniu gildii powinien zachowac forme taka, w jakiej zostal wpisany")
    @Comment("UWAGA: Gdy ta opcja jest wlaczona, opcja \"guild-tag-uppercase\" nie bedzie miala wplywu na tag gildii")
    @CustomKey("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @Comment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @Comment("Dziala dopiero po zrestartowaniu serwera")
    @CustomKey("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @Comment("Czy wlaczyc tlumaczenie nazw przedmiotow?")
    @CustomKey("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @Comment("Tlumaczenia nazw przedmiotow dla znacznikow {ITEM}, {ITEMS}, {ITEM-NO-AMOUNT}, {WEAPON}")
    @Comment("Wypisywac w formacie nazwa_przedmiotu: \"tlumaczona nazwa przedmiotu\"")
    @CustomKey("translated-materials-name")
    public Map<String, String> translatedMaterials_ = ImmutableMap.<String, String>builder()
            .put("diamond_sword", "&3diamentowy miecz")
            .put("iron_sword", "&7zelazny miecz")
            .put("gold_ingot", "&eZloto")
            .build();

    @Exclude
    public Map<Material, String> translatedMaterials;

    @Comment("Wyglad znacznikow {ITEM} i {ITEMS} (suffix, za iloscia przedmiotu)")
    @Comment("Dla np. item-amount-suffix: \"szt.\" 1szt. golden_apple")
    @CustomKey("item-amount-suffix")
    public String itemAmountSuffix_ = "x";

    @Exclude
    public String itemAmountSuffix;

    @Comment("Czy blacklista nazw i tagow gildii powinny byc wlaczona")
    @CustomKey("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @Comment("Jesli ustawione na false, nazwy i tagi z list 'restricted-guild-names', 'restricted-guild-tags' beda niedozwolone.")
    @Comment("Jesli ustawione na true, 'restricted-guild-names', 'restricted-guild-tags' beda traktowane jako whitelist.")
    @Comment("Przydatne kiedy chcesz ograniczyc tworzenie np. do 2 gildii \"RED\", \"BLUE\"")
    @CustomKey("whitelist")
    public boolean whitelist = false;

    @Comment("Blacklista nazw przy zakladaniu gildii")
    @Comment("Zamienia sie w whiteliste jesli 'whitelist' jest ustawione na true")
    @CustomKey("restricted-guild-names")
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @Comment("Blacklista tagów przy zakladaniu gildii")
    @Comment("Zamienia sie w whiteliste jesli 'whitelist' jest ustawione na true")
    @CustomKey("restricted-guild-tags")
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @Comment("Czy powiadomienie o zabojstwie gracza powinno sie wyswietlac na title dla zabojcy")
    @CustomKey("display-title-notification-for-killer")
    public boolean displayTitleNotificationForKiller = false;

    @Comment("Czy powiadomienia o wejsciu na teren gildii czlonka gildii powinny byc wyswietlane")
    @CustomKey("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    @Comment("Gdzie maja pojawiac sie wiadomosci zwiazane z poruszaniem sie po terenach gildii")
    @Comment("Mozliwe miejsca wyswietlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CustomKey("region-move-notification-style")
    public List<NotificationStyle> regionEnterNotificationStyle = Arrays.asList(NotificationStyle.ACTIONBAR, NotificationStyle.BOSSBAR);

    @Min(1)
    @Comment("Jak dlugo title/subtitle powinien sie pojawiac")
    @Comment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @Comment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CustomKey("notification-title-fade-in")
    public int notificationTitleFadeIn = 10;

    @Min(1)
    @Comment("Jak dlugo title/subtitle powinien pozostac na ekranie gracza")
    @Comment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @Comment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CustomKey("notification-title-stay")
    public int notificationTitleStay = 10;

    @Min(1)
    @Comment("Jak dlugo title/subtitle powinien znikac")
    @Comment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @Comment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CustomKey("notification-title-fade-out")
    public int notificationTitleFadeOut = 10;

    @Pattern("PINK|BLUE|RED|GREEN|YELLOW|PURPLE|WHITE")
    @Comment("Jakiego koloru powinien byc boss bar podczas wyswietlania notyfikacji")
    @Comment("Dostepne kolory: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    @CustomKey("notification-boss-bar-color")
    public String bossBarColor = "RED";

    @Pattern("SOLID|SEGMENTED_6|SEGMENTED_10|SEGMENTED_12|SEGMENTED_20")
    @Comment("Jakiego stylu powinien byc boss bar podczas wyswietlania notyfikacji")
    @Comment("Dostepne style: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    @CustomKey("notification-boss-bar-style")
    public String bossBarStyle = "SOLID";

    @Comment("Jakie flagi powinny byc nalozone na byc boss bar podczas wyswietlania notyfikacji")
    @Comment("Dostepne flagi: DARKEN_SKY, PLAY_BOSS_MUSIC, CREATE_FOG")
    @CustomKey("notification-boss-bar-flags")
    public List<String> bossBarFlags = Collections.singletonList("CREATE_FOG");

    @Exclude
    public BossBarOptions bossBarOptions_;

    @Comment("Czy osoba, ktora zalozyla pierwsza gildie na serwerze powinna dostac nagrode")
    @CustomKey("should-give-rewards-for-first-guild")
    public boolean giveRewardsForFirstGuild = false;

    @Comment("Przedmioty, ktore zostana nadane graczowi, ktory pierwszy zalozyl gildie na serwerze")
    @Comment("Dziala tylko w wypadku, gdy opcja \"should-give-rewards-for-first-guild\" jest wlaczona")
    @CustomKey("rewards-for-first-guild")
    public List<String> firstGuildRewards_ = Collections.singletonList("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze");

    @Exclude
    public List<ItemStack> firstGuildRewards;

    @Comment("Zbior przedmiotow potrzebnych do resetu rankingu")
    @CustomKey("rank-reset-needed-items")
    public List<String> rankResetItems_ = Collections.singletonList("1 diamond");

    @Exclude
    public List<ItemStack> rankResetItems;

    @Comment("Czy przy szukaniu danych o graczu ma byc pomijana wielkosc znakow")
    @CustomKey("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @Comment("Nazwy komend")
    @CustomKey("commands")
    public CommandsConfiguration commands = new CommandsConfiguration();

    @Comment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CustomKey("event-move")
    public boolean eventMove = true;

    @Exclude
    public boolean eventPhysics;

    @Comment("Czy System Security ma byc wlaczony?")
    @CustomKey("system-security-enable")
    public boolean systemSecurityEnable = true;

    @DecimalMin("0")
    @Comment("Margines sprawdzania jak daleko uderzył gracz serce gildii")
    @Comment("Jeśli dostajesz fałszywe alarmy od Security zwiększ tę wartość do około 0.50 lub więcej")
    @CustomKey("reach-compensation")
    public double reachCompensation = 0.26;

    @Min(0)
    @Comment("Margines sprawdzania przez ile bloków uderzył gracz w serce gildii")
    @CustomKey("freeCam-compensation")
    public int freeCamCompensation = 0;

    @Min(1)
    @Comment("Ilość wątków używanych przez ConcurrencyManager")
    @CustomKey("concurrency-threads")
    public int concurrencyThreads = 1;

    @Min(1)
    @Comment("Co ile minut ma automatycznie zapisywac dane")
    @CustomKey("data-interval")
    public int dataInterval = 1;

    @Min(0)
    @Comment("Jak dlugo plugin powinien czekac na zatrzymanie wszystkich biezacych zadan przy wylaczaniu pluginu")
    @Comment("Czas podawany w sekundach")
    @CustomKey("plugin-task-termination-timeout")
    public long pluginTaskTerminationTimeout_ = 30;

    @Exclude
    public long pluginTaskTerminationTimeout;

    @Comment("Hooki do pluginow, ktore powinny zostac wylaczone. Opcja powinna byc stosowania jedynie w awaryjnych sytuacjach!")
    @Comment("Lista hookow, ktore mozna wylaczyc: WorldEdit, WorldGuard, Vault, BungeeTabListPlus, MVdWPlaceholderAPI, PlaceholderAPI, LeaderHeads, HolographicDisplays")
    @Comment("Aby zostawic wszystkie hooki wlaczone wystarczy wpisac: disabled-hooks: [] (opcja domyslna i zalecana)")
    public Set<String> disabledHooks = new HashSet<>();

    @Comment("Typ zapisu danych")
    @Comment("FLAT - Lokalne pliki")
    @Comment("MYSQL - baza danych")
    public DataModel dataModel = DataModel.FLAT;

    @Comment("Dane wymagane do polaczenia z baza")
    @Comment("UWAGA: connectionTimeout jest w milisekundach!")
    @Comment("Sekcja poolSize odpowiada za liczbe zarezerwowanych polaczen, domyslna wartosc 5 powinna wystarczyc")
    @Comment("Aby umozliwic FG automatyczne zarzadzanie liczba polaczen - ustaw poolSize na -1")
    @Comment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @Comment("Najlepiej zmieniac te nazwy tylko wtedy, gdy jest naprawde taka potrzeba (np. wystepuje konflikt z innym pluginem)")
    @Comment("Aby zmienic nazwy tabel, gdy masz juz w bazie jakies dane z FG:")
    @Comment("1. Wylacz serwer")
    @Comment("2. Zmien dane w configu FG")
    @Comment("3. Zmien nazwy tabel w bazie uzywajac np. phpMyAdmin")
    public MysqlConfiguration mysql = new MysqlConfiguration("localhost", 3306, "db", "root", "passwd", 5, 30000, true, "users", "guilds", "regions");

    private List<ItemStack> loadItemStackList(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        for (String item : strings) {
            if (item == null || "".equals(item)) {
                continue;
            }

            ItemStack itemstack = ItemUtils.parseItem(item);
            if (itemstack != null) {
                items.add(itemstack);
            }
        }

        return items;
    }

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<>();

        for (String var : contents) {
            ItemStack item = null;

            if (var.contains("GUI-")) {
                int index = RankUtils.getIndex(var);

                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            }
            else if (var.contains("VIPITEM-")) {
                try {
                    int index = RankUtils.getIndex(var);

                    if (index > 0 && index <= createItemsVip.size()) {
                        item = createItemsVip.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + var + " is > " + createItemsVip.size() + " or <= 0");
                }
            }
            else if (var.contains("ITEM-")) {
                try {
                    int index = RankUtils.getIndex(var);

                    if (index > 0 && index <= createItems.size()) {
                        item = createItems.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + var + " is > " + createItems.size() + " or <= 0");
                }
            }
            else {
                item = ItemUtils.parseItem(var);
            }

            if (item == null) {
                item = new ItemBuilder(MaterialUtils.matchMaterial("stained_glass_pane"), 1, 14).setName("&c&lERROR IN GUI CREATION: " + var, true).getItem();
            }

            items.add(item);
        }

        return items;
    }

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        heart.loadProcessedProperties();
        this.loadProcessedProperties();

        return this;
    }

    public void loadProcessedProperties() {
        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);

        this.guiItems = loadGUI(this.guiItems_);

        if (!useCommonGUI) {
            this.guiItemsVip = loadGUI(this.guiItemsVip_);
        }

        this.guiItemsTitle = ChatUtils.colored(this.guiItemsTitle_);
        this.guiItemsVipTitle = ChatUtils.colored(this.guiItemsVipTitle_);
        this.guiItemsName = ChatUtils.colored(this.guiItemsName_);
        this.guiItemsLore = ChatUtils.colored(this.guiItemsLore_);

        if (this.heart.createMaterial != null && MaterialUtils.hasGravity(this.heart.createMaterial.getLeft())) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        }
        else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        this.buggedBlocksExclude = new HashSet<>();
        for (String stringMaterial : this.buggedBlocksExclude_) {
            this.buggedBlocksExclude.add(MaterialUtils.parseMaterial(stringMaterial, false));
        }

        if (this.rankSystem == RankSystem.Type.ELO) {
            Map<IntegerRange, Integer> parsedData = new HashMap<>();

            for (Entry<IntegerRange, String> entry : IntegerRange.parseIntegerRange(this.eloConstants_, false).entrySet()) {
                try {
                    parsedData.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                }
                catch (NumberFormatException e) {
                    FunnyGuilds.getPluginLogger().parser("\"" + entry.getValue() + "\" is not a valid elo constant!");
                }
            }

            this.eloConstants = parsedData;
        }

        Map<Material, Double> map = new EnumMap<>(Material.class);
        for (Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            double chance = entry.getValue();
            if (chance < 0) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("*")) {
                this.allMaterialsAreExplosive = true;
                this.defaultExplodeChance = chance;
                continue;
            }

            Material material = MaterialUtils.parseMaterial(entry.getKey(), true);
            if (material == null || material == Material.AIR) {
                continue;
            }

            map.put(material, chance);
        }
        this.explodeMaterials = map;

        this.tntProtection.time.passingMidnight = this.tntProtection.time.startTime.isAfter(this.tntProtection.time.endTime);

        this.translatedMaterials = new HashMap<>();
        for (String materialName : translatedMaterials_.keySet()) {
            Material material = MaterialUtils.matchMaterial(materialName.toUpperCase());
            if (material == null) {
                continue;
            }

            translatedMaterials.put(material, translatedMaterials_.get(materialName));
        }

        this.itemAmountSuffix = ChatUtils.colored(this.itemAmountSuffix_);

        if (!"v1_8_R1".equals(Reflections.SERVER_VERSION) && !"v1_8_R3".equals(Reflections.SERVER_VERSION)) {
            this.bossBarOptions_ = BossBarOptions.builder()
                    .color(this.bossBarColor)
                    .style(this.bossBarStyle)
                    .flags(this.bossBarFlags)
                    .build();
        }

        this.rankResetItems = loadItemStackList(this.rankResetItems_);

        this.firstGuildRewards = loadItemStackList(this.firstGuildRewards_);

        this.validityItems = this.loadItemStackList(this.validityItems_);

        this.joinItems = this.loadItemStackList(this.joinItems_);
        this.baseItems = this.loadItemStackList(this.baseItems_);

        this.prefixOur = ChatUtils.colored(this.prefixOur_);
        this.prefixAllies = ChatUtils.colored(this.prefixAllies_);
        this.prefixOther = ChatUtils.colored(this.prefixOther_);
        this.prefixEnemies = ChatUtils.colored(this.prefixEnemies_);

        this.ptopOnline = ChatUtils.colored(this.ptopOnline_);
        this.ptopOffline = ChatUtils.colored(this.ptopOffline_);

        this.dummySuffix = ChatUtils.colored(this.dummySuffix_);

        this.chatPosition = ChatUtils.colored(this.chatPosition_);
        this.chatGuild = ChatUtils.colored(this.chatGuild_);
        this.chatRank = ChatUtils.colored(this.chatRank_);
        this.chatPoints = ChatUtils.colored(this.chatPoints_);

        this.pointsFormat = IntegerRange.parseIntegerRange(this.pointsFormat_, true);
        this.pingFormat = IntegerRange.parseIntegerRange(this.pingFormat_, true);

        this.ptopPoints = ChatUtils.colored(this.ptopPoints_);
        this.gtopPoints = ChatUtils.colored(this.gtopPoints_);

        this.chatPrivDesign = ChatUtils.colored(this.chatPrivDesign_);
        this.chatAllyDesign = ChatUtils.colored(this.chatAllyDesign_);
        this.chatGlobalDesign = ChatUtils.colored(this.chatGlobalDesign_);

        this.lastAttackerAsKillerConsiderationTimeout_ = TimeUnit.SECONDS.toMillis(this.lastAttackerAsKillerConsiderationTimeout);
    }

    public enum DataModel {
        FLAT,
        MYSQL
    }

}
