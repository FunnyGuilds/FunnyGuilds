package net.dzikoysk.funnyguilds.data.configs;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.validator.annotation.*;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.GuildRegex;
import net.dzikoysk.funnyguilds.basic.rank.RankSystem;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarOptions;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("          FunnyGuilds           #")
@Header("         4.9.5 Tribute          #")
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

    @Exclude
    public SimpleDateFormat dateFormat;

    @Comment("Wyswietlana nazwa pluginu")
    @CustomKey("plugin-name")
    public String pluginName = "FunnyGuilds";

    @Comment("Czy plugin ma dzialac w trybie debug. Sluzy on do wysylania dodatkowych wiadomosci w celu zdiagnozowania bledow itp.")
    @CustomKey("debug-mode")
    public boolean debugMode = false;

    @Comment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    @CustomKey("update-info")
    public boolean updateInfo = true;

    @Comment("Czy informacje o aktualizacji wersji nightly maja byc widoczne podczas wejscia na serwer")
    @Comment("Ta opcja działa tylko wtedy, gdy także jest włączona opcja 'update-info'")
    @CustomKey("update-nightly-info")
    public boolean updateNightlyInfo = true;

    @Comment("Mozliwosc zakladania gildii. Mozna ja zmienic takze za pomoca komendy /ga enabled")
    @CustomKey("guilds-enabled")
    public boolean guildsEnabled = true;

    @Comment("Czy tworzenie regionow gildii, oraz inne zwiazane z nimi rzeczy, maja byc wlaczone")
    @Comment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @Comment(
            "Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @Comment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @Comment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    @CustomKey("regions-enabled")
    public boolean regionsEnabled = true;

    @Comment("Zablokuj rozlewanie się wody i lawy poza terenem gildii")
    @Comment("Dziala tylko jesli regiony sa wlaczone")
    @CustomKey("water-and-lava-flow-only-for-regions")
    public boolean blockFlow = false;

    @Comment("Czy gracz po smierci ma sie pojawiac w bazie swojej gildii")
    @Comment("Dziala tylko jesli regiony sa wlaczone")
    @CustomKey("respawn-in-base")
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
    @CustomKey("tag-regex")
    public GuildRegex tagRegex = GuildRegex.LETTERS;

    @Min(0)
    @Comment("Minimalna liczba graczy w gildii, aby zaliczala sie ona do rankingu")
    @CustomKey("guild-min-members")
    public int minMembersToInclude = 1;

    @Comment("Czy wiadomosci o braku potrzebnych przedmiotow maja zawierac elementy, na ktore mozna najechac")
    @Comment("Takie elementy pokazuja informacje o przedmiocie, np. jego typ, nazwe czy opis")
    @Comment("Funkcja jest obecnie troche niedopracowana i moze powodowac problemy na niektorych wersjach MC, np. 1.8.8")
    @CustomKey("enable-item-component")
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
    @CustomKey("required-experience")
    public int requiredExperience = 0;

    @Min(0)
    @Comment("Wymagana ilosc pieniedzy do zalozenia gildii")
    @Comment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CustomKey("required-money")
    public double requiredMoney = 0;

    @Comment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CustomKey("items-vip")
    public List<String> itemsVip_ = Collections.singletonList("1 gold_ingot");

    @Exclude
    public List<ItemStack> createItemsVip;

    @Min(0)
    @Comment("Wymagana ilosc doswiadczenia do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CustomKey("required-experience-vip")
    public int requiredExperienceVip = 0;

    @Min(0)
    @Comment("Wymagana ilosc pieniedzy do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @Comment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CustomKey("required-money-vip")
    public double requiredMoneyVip = 0;

    @Comment("Czy opcja wymaganego rankingu do zalozenia gildii ma byc wlaczona?")
    @CustomKey("rank-create-enable")
    public boolean rankCreateEnable = true;

    @Comment("Minimalny ranking wymagany do zalozenia gildii")
    @CustomKey("rank-create")
    public int rankCreate = 1000;

    @Comment("Minimalny ranking wymagany do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.rank")
    @CustomKey("rank-create-vip")
    public int rankCreateVip = 800;

    @Comment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @Comment(
            "Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    @CustomKey("use-common-gui")
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
    @CustomKey("add-lore-lines")
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
    @CustomKey("create-distance")
    public int createDistance = 100;

    @Comment("Minimalna odleglosc od regionu gildii do granicy mapy")
    @CustomKey("create-guild-min-distance")
    public double createMinDistanceFromBorder = 50;

    @Comment("Blok lub entity, ktore jest sercem gildii")
    @Comment("Zmiana entity wymaga pelnego restartu serwera")
    @Comment("Bloki musza byc podawane w formacie - material:metadata")
    @Comment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @Comment("Typ entity musi byc zgodny z ta lista (i zdrowym rozsadkiem) - https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @Comment("UWAGA: Zmiana bloku, gdy sa juz zrobione jakies gildie, spowoduje niedzialanie ich regionow")
    @Comment(" ")
    @Comment("UWAGA: Jesli jako serca gildii chcesz uzyc bloku, ktory spada pod wplywem grawitacji - upewnij sie, ze bedzie on stal na jakims bloku!")
    @Comment("Jesli pojawi sie w powietrzu - spadnie i plugin nie bedzie odczytywal go poprawnie!")
    @CustomKey("create-type")
    public String createType = "ender_crystal";
    @Exclude
    public Pair<Material, Byte> createMaterial;
    @Exclude
    public EntityType createEntityType;

    @Comment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @Comment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CustomKey("create-center-y")
    public int createCenterY = 60;

    @Comment("Czy ma sie tworzyc kula z obsydianu dookola centrum gildii")
    @CustomKey("create-center-sphere")
    public boolean createCenterSphere = true;

    @Comment("Czy przy tworzeniu gildii powinien byc wklejany schemat")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CustomKey("paste-schematic-on-creation")
    public boolean pasteSchematicOnCreation = false;

    @Comment("Nazwa pliku ze schematem poczatkowym gildii")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @Comment("Schemat musi znajdować się w folderze FunnyGuilds")
    @CustomKey("guild-schematic-file-name")
    public String guildSchematicFileName = "funnyguilds.schematic";

    @Comment("Czy schemat przy tworzeniu gildii powinien byc wklejany razem z powietrzem?")
    @Comment("Przy duzych schematach ma to wplyw na wydajnosc")
    @Comment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CustomKey("paste-schematic-with-air")
    public boolean pasteSchematicWithAir = true;

    @Exclude
    public File guildSchematicFile;

    @Comment("Typy blokow, z ktorymi osoba spoza gildii NIE moze prowadzic interakcji na terenie innej gildii")
    @CustomKey("blocked-interact")
    public List<String> _blockedInteract = Arrays.asList("CHEST", "TRAPPED_CHEST");
    @Exclude
    public Set<Material> blockedInteract;

    @Comment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    @CustomKey("bugged-blocks")
    public boolean buggedBlocks = false;

    @Min(0)
    @Comment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete")
    @Comment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @CustomKey("bugged-blocks-timer")
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
    @CustomKey("escape-enable")
    public boolean escapeEnable = true;

    @Min(0)
    @Comment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    @CustomKey("escape-delay")
    public int escapeDelay = 120;

    @Comment("Mozliwosc ucieczki na spawn dla graczy bez gildii")
    @CustomKey("escape-spawn")
    public boolean escapeSpawn = true;

    @Comment("Mozliwosc teleportacji do gildii")
    @CustomKey("base-enable")
    public boolean baseEnable = true;

    @Min(0)
    @Comment("Czas oczekiwania na teleportacje, w sekundach")
    @CustomKey("base-delay")
    public int baseDelay = 5;

    @Min(0)
    @Comment("Czas oczekiwania na teleportacje, w sekundach, dla graczy posiadajacych uprawnienie funnyguilds.vip.baseTeleportTime")
    @CustomKey("base-delay-vip")
    public int baseDelayVip = 3;

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
    @CustomKey("enlarge-enable")
    public boolean enlargeEnable = true;

    @Comment("O ile powieksza teren gildii 1 poziom")
    @CustomKey("enlarge-size")
    public int enlargeSize = 5;

    @Comment("Koszt powiekszania gildii")
    @Comment("- kazdy myslnik, to 1 poziom gildii")
    @CustomKey("enlarge-items")
    public List<String> enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @Exclude
    public List<ItemStack> enlargeItems;

    @Min(1)
    @Comment("Wielkosc regionu gildii")
    @CustomKey("region-size")
    public int regionSize = 50;

    @Min(0)
    @Comment("Minimalna odleglosc miedzy terenami gildii")
    @CustomKey("region-min-distance")
    public int regionMinDistance = 10;

    @Min(1)
    @Comment("Czas wyswietlania powiadomienia na pasku powiadomien, w sekundach")
    @CustomKey("region-notification-time")
    public int regionNotificationTime = 15;

    @Min(1)
    @Comment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza, w sekundach")
    @CustomKey("region-notification-cooldown")
    public int regionNotificationCooldown = 60;

    @Comment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CustomKey("region-commands")
    public List<String> regionCommands = Collections.singletonList("sethome");

    @Comment("Czy proces usuniecia gildii powinien zostac przerwany jezeli ktos spoza gildii jest na jej terenie")
    @CustomKey("guild-delete-cancel-if-someone-is-on-region")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    @Comment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
    @CustomKey("guild-tnt-protection-enabled")
    public boolean guildTNTProtectionEnabled = false;

    @Comment("Czy wlaczyc ochrone przed TNT na całym serwerze w podanych godzinach")
    @CustomKey("guild-tnt-protection-global")
    public boolean guildTNTProtectionGlobal = false;

    @Comment("O której godzinie ma sie zaczac ochrona przed TNT w gildii")
    @Comment("Godzina w formacie HH:mm")
    @CustomKey("guild-tnt-protection-start-time")
    public String guildTNTProtectionStartTime_ = "22:00";
    @Exclude
    public LocalTime guildTNTProtectionStartTime;

    @Comment("Do której godziny ma dzialac ochrona przed TNT w gildii")
    @Comment("Godzina w formacie HH:mm")
    @CustomKey("guild-tnt-protection-end-time")
    public String guildTNTProtectionEndTime_ = "06:00";
    @Exclude
    public LocalTime guildTNTProtectionEndTime;
    @Exclude
    public boolean guildTNTProtectionPassingMidnight;

    @Min(0)
    @Comment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    @CustomKey("region-explode")
    public int regionExplode = 120;

    @Comment("Czy blokada po wybuchu ma obejmowac rowniez budowanie")
    @CustomKey("region-explode-block-breaking")
    public boolean regionExplodeBlockBreaking = false;

    @Comment("Czy blokada po wybuchu ma obejmowac rowniez interakcje z blocked-interact")
    @CustomKey("region-explode-block-interactions")
    public boolean regionExplodeBlockInteractions = false;

    @Min(0)
    @Comment("Zasieg pobieranych przedmiotow po wybuchu. Jezeli chcesz wylaczyc, wpisz 0")
    @CustomKey("explode-radius")
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
    @CustomKey("explode-should-affect-only-guild")
    public boolean explodeShouldAffectOnlyGuild = false;

    @Comment("Możliwość podbijania gildii")
    @CustomKey("war-enabled")
    public boolean warEnabled = true;

    @Min(1)
    @Comment("Ile zyc ma gildia")
    @CustomKey("war-lives")
    public int warLives = 3;

    @Comment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CustomKey("war-protection")
    public String warProtection_ = "24h";
    @Exclude
    public long warProtection;

    @Comment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CustomKey("war-wait")
    public String warWait_ = "24h";
    @Exclude
    public long warWait;

    @Comment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    @CustomKey("war-tnt-protection")
    public boolean warTntProtection = true;

    @Comment("Czy zwierzeta na terenie gildii maja byc chronione przed osobami spoza gildii")
    @CustomKey("animals-protection")
    public boolean animalsProtection = false;

    @Comment("Jaka waznosc ma gildia po jej zalozeniu")
    @CustomKey("validity-start")
    public String validityStart_ = "14d";

    @Exclude
    public long validityStart;

    @Comment("Ile czasu dodaje przedluzenie gildii")
    @CustomKey("validity-time")
    public String validityTime_ = "14d";

    @Exclude
    public long validityTime;

    @Comment("Ile dni przed koncem wygasania mozna przedluzyc gildie. Wpisz 0, jezeli funkcja ma byc wylaczona")
    @CustomKey("validity-when")
    public String validityWhen_ = "14d";
    @Exclude
    public long validityWhen;

    @Comment("Koszt przedluzenia gildii")
    @CustomKey("validity-items")
    public List<String> validityItems_ = Collections.singletonList("10 diamond");
    @Exclude
    public List<ItemStack> validityItems;

    @Comment("Czy wiadomosc o zabiciu gracza powinna byc pokazywana wszystkim")
    @Comment("Jesli wylaczone - bedzie pokazywana tylko graczom, ktorzy brali udzial w walce")
    @CustomKey("broadcast-death-message")
    public boolean broadcastDeathMessage = true;

    @Comment("Czy wiadomosc o zabiciu gracza powinna byc wyswietlana bez wzgledu na wylaczone wiadomosci o smierci")
    @CustomKey("ignore-death-messages-disabled")
    public boolean ignoreDisabledDeathMessages = false;

    @Comment("Ranking od ktorego rozpoczyna gracz")
    @CustomKey("rank-start")
    public int rankStart = 1000;

    @Comment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    @CustomKey("rank-farming-protect")
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
    @CustomKey("rank-farming-cooldown")
    public int rankFarmingCooldown = 7200;

    @Comment("Czy ma byc zablokowana zmiana rankingu, jesli obie osoby z walki maja taki sam adres IP")
    @CustomKey("rank-ip-protect")
    public boolean rankIPProtect = false;

    @Comment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzglednieni przy wyznaczaniu pozycji gracza w rankingu")
    @CustomKey("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    @Min(1)
    @Comment("Co ile ticków ranking graczy oraz gildii powinien zostać odświeżony")
    @CustomKey("ranking-update-interval")
    public int rankingUpdateInterval = 40;

    @Exclude
    public long rankingUpdateInterval_;

    @Comment("Czy system asyst ma byc wlaczony")
    @CustomKey("rank-assist-enable")
    public boolean assistEnable = true;

    @Min(-1)
    @Comment("Limit asyst. Wpisz liczbe ujemna aby wylaczyc")
    @CustomKey("assists-limit")
    public int assistsLimit = - 1;

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
    public RankSystem rankSystem = RankSystem.ELO;

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

    @DecimalMin("0.00001")
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @Comment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @Comment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CustomKey("elo-divider")
    public double eloDivider = 400.0D;

    @DecimalMin("0.00001")
    @Comment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @Comment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @Comment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CustomKey("elo-exponent")
    public double eloExponent = 10.0D;

    @DecimalMin("0")
    @DecimalMax("1")
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
    @CustomKey("prefix-other")
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

    @Comment("Wyglad listy graczy, przedzial slotow - od 1 do 80")
    @Comment("Schemat wygladu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/assets/tab-scheme.png")
    @Comment("> Spis zmiennych gracza:")
    @Comment("{PLAYER} - nazwa gracza")
    @Comment("{WORLD} - swiat, w ktorym znajduje sie gracz")
    @Comment("{PING} - ping gracza")
    @Comment("{PING-FORMAT} - ping gracza z formatowaniem")
    @Comment("{POINTS} - punkty gracza")
    @Comment("{POINTS-FORMAT} - punkty gracza z formatowaniem")
    @Comment("{POSITION} - pozycja gracza w rankingu")
    @Comment("{KILLS} - liczba zabojstw gracza")
    @Comment("{DEATHS} - liczba smierci gracza")
    @Comment("{ASSISTS} - liczba asyst gracza")
    @Comment("{LOGOUTS} - liczba wylogowań gracza podczas walki")
    @Comment("{KDR} - stosunek zabojstw do smierci gracza")
    @Comment("{WG-REGION} - region WorldGuard'a, na ktorym znajduje sie gracz (pierwszy, jesli jest ich kilka)")
    @Comment("{WG-REGIONS} - regiony WorldGuard'a, na ktorych znajduje sie gracz (oddzielone przecinkami)")
    @Comment("{VAULT-MONEY} - balans konta gracza pobierany z pluginu Vault")
    @Comment("> Spis zmiennych gildyjnych:")
    @Comment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @Comment("{G-TAG} - tag gildii gracza")
    @Comment("{G-OWNER} - wlasciciel gildii")
    @Comment("{G-DEPUTIES} - zastepcy gildii")
    @Comment("{G-DEPUTY} - losowy z zastepcow gildii")
    @Comment("{G-LIVES} - liczba zyc gildii")
    @Comment("{G-ALLIES} - liczba sojusznikow gildii")
    @Comment("{G-POINTS} - punkty gildii")
    @Comment("{G-POINTS-FORMAT} - punkty gildii z formatowaniem")
    @Comment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @Comment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @Comment("{G-DEATHS} - suma smierci czlonkow gildii")
    @Comment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @Comment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @Comment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @Comment("{G-VALIDITY} - data wygasniecia gildii")
    @Comment("{G-REGION-SIZE} - rozmiar gildii")
    @Comment("> Spis pozostalych zmiennych:")
    @Comment("{GUILDS} - liczba gildii na serwerze")
    @Comment("{USERS} - liczba uzytkownikow serwera")
    @Comment("{ONLINE} - liczba graczy online")
    @Comment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @Comment("{SECOND} - Sekunda")
    @Comment("{MINUTE} - Minuta")
    @Comment("{HOUR} - Godzina")
    @Comment("{DAY_OF_WEEK} - Dzien tygodnia wyrazony w postaci nazwy dnia")
    @Comment("{DAY_OF_MONTH} - Dzien miesiaca wyrazony w postaci liczby")
    @Comment("{MONTH} - Miesiac wyrazony w postaci nazwy miesiaca")
    @Comment("{MONTH_NUMBER} - Miesiac wyrazony w postaci liczby")
    @Comment("{YEAR} - Rok")
    @Comment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @Comment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CustomKey("player-list")
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(1, "&7Nick: &b{PLAYER}")
            .put(2, "&7Ping: &b{PING}")
            .put(3, "&7Punkty: &b{POINTS}")
            .put(4, "&7Zabojstwa: &b{KILLS}")
            .put(5, "&7Smierci: &b{DEATHS}")
            .put(6, "&7KDR: &b{KDR}")
            .put(7, "&7Gildia: &b{G-NAME}")
            .put(9, "&7TAG: &b{G-TAG}")
            .put(10, "&7Punkty gildii: &b{G-POINTS-FORMAT}")
            .put(11, "&7Pozycja gildii: &b{G-POSITION}")
            .put(12, "&7Liczba graczy online: &b{G-MEMBERS-ONLINE}")
            .put(21, "&7Online: &b{ONLINE}")
            .put(22, "&7TPS: &b{TPS}")
            .put(41, "&bTop 3 Gildii")
            .put(42, "&71. &b{GTOP-1}")
            .put(43, "&72. &b{GTOP-2}")
            .put(44, "&73. &b{GTOP-3}")
            .put(61, "&bTop 3 Graczy")
            .put(62, "&71. &b{PTOP-1}")
            .put(63, "&72. &b{PTOP-2}")
            .put(64, "&73. &b{PTOP-3}")
            .build();

    @Comment("Wyglad naglowka w liscie graczy.")
    @CustomKey("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.9.5 Tribute";

    @Comment("Wyglad stopki w liscie graczy.")
    @CustomKey("player-list-footer")
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";

    @Min(0)
    @Comment("Liczba pingu pokazana przy kazdej komorce.")
    @CustomKey("player-list-ping")
    public int playerListPing = 0;

    @Comment("Czy wszystkie mozliwe komorki maja zostac zapelnione, nie zwazywszy na liczbe graczy online")
    @CustomKey("player-list-fill-cells")
    public boolean playerListFillCells = true;

    @Comment("Czy tablista ma byc wlaczona")
    @CustomKey("player-list-enable")
    public boolean playerListEnable = true;

    @Min(1)
    @Comment("Co ile tickow lista graczy powinna zostac odswiezona")
    @CustomKey("player-list-update-interval")
    public int playerListUpdateInterval = 20;

    @Exclude
    public long playerListUpdateInterval_;

    @Comment("Czy zmienne typu {PTOP-%} oraz {GTOP-%} powinny byc pokolorowane w zaleznosci od relacji gildyjnych")
    @CustomKey("player-list-use-relationship-colors")
    public boolean playerListUseRelationshipColors = false;

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

    @Comment("Czy filtry nazw i tagow gildii powinny byc wlaczone")
    @CustomKey("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @Comment("Niedozwolone nazwy przy zakladaniu gildii")
    @CustomKey("restricted-guild-names")
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @Comment("Niedozwolone tagi przy zakladaniu gildii")
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
    public Commands commands = new Commands();

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

    @Comment("Typ zapisu danych")
    @Comment("FLAT - Lokalne pliki")
    @Comment("MYSQL - baza danych")
    @CustomKey("data-model")
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
    @CustomKey("mysql")
    public MySQL mysql = new MySQL("localhost", 3306, "db", "root", "passwd", 5, 30000, true, "users", "guilds", "regions");

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
        this.dateFormat = new SimpleDateFormat(FunnyGuilds.getInstance().getMessageConfiguration().dateFormat);

        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);

        this.guiItems = loadGUI(this.guiItems_);

        if (! useCommonGUI) {
            this.guiItemsVip = loadGUI(this.guiItemsVip_);
        }

        this.guiItemsTitle = ChatUtils.colored(this.guiItemsTitle_);
        this.guiItemsVipTitle = ChatUtils.colored(this.guiItemsVipTitle_);
        this.guiItemsName = ChatUtils.colored(this.guiItemsName_);
        this.guiItemsLore = ChatUtils.colored(this.guiItemsLore_);

        try {
            this.createEntityType = EntityType.valueOf(this.createType.toUpperCase().replace(" ", "_"));
        }
        catch (Exception materialThen) {
            this.createMaterial = MaterialUtils.parseMaterialData(this.createType, true);
        }

        if (this.createMaterial != null && MaterialUtils.hasGravity(this.createMaterial.getLeft())) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        }
        else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        this.blockedInteract = new HashSet<>();

        for (String s : this._blockedInteract) {
            this.blockedInteract.add(MaterialUtils.parseMaterial(s, false));
        }

        this.buggedBlocksExclude = new HashSet<>();

        for (String s : this.buggedBlocksExclude_) {
            this.buggedBlocksExclude.add(MaterialUtils.parseMaterial(s, false));
        }

        if (this.rankSystem == RankSystem.ELO) {
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

        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
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

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.guildTNTProtectionStartTime = LocalTime.parse(guildTNTProtectionStartTime_, timeFormatter);
        this.guildTNTProtectionEndTime = LocalTime.parse(guildTNTProtectionEndTime_, timeFormatter);
        this.guildTNTProtectionPassingMidnight = this.guildTNTProtectionStartTime.isAfter(this.guildTNTProtectionEndTime);
        this.translatedMaterials = new HashMap<>();

        for (String materialName : translatedMaterials_.keySet()) {
            Material material = MaterialUtils.matchMaterial(materialName.toUpperCase());

            if (material == null) {
                continue;
            }

            translatedMaterials.put(material, translatedMaterials_.get(materialName));
        }

        this.itemAmountSuffix = ChatUtils.colored(this.itemAmountSuffix_);

        if (! "v1_8_R1".equals(Reflections.SERVER_VERSION) && ! "v1_8_R3".equals(Reflections.SERVER_VERSION)) {
            this.bossBarOptions_ = BossBarOptions.builder()
                    .color(this.bossBarColor)
                    .style(this.bossBarStyle)
                    .flags(this.bossBarFlags)
                    .build();
        }

        this.rankResetItems = loadItemStackList(this.rankResetItems_);

        this.firstGuildRewards = loadItemStackList(this.firstGuildRewards_);

        this.warProtection = TimeUtils.parseTime(this.warProtection_);
        this.warWait = TimeUtils.parseTime(this.warWait_);

        this.validityStart = TimeUtils.parseTime(this.validityStart_);
        this.validityTime = TimeUtils.parseTime(this.validityTime_);
        this.validityWhen = TimeUtils.parseTime(this.validityWhen_);

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

        if (this.pasteSchematicOnCreation) {
            if (this.guildSchematicFileName == null || this.guildSchematicFileName.isEmpty()) {
                FunnyGuilds.getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field \"paste-schematic-on-creation\" is set to true!");
                this.pasteSchematicOnCreation = false;
            }
            else {
                this.guildSchematicFile = new File(FunnyGuilds.getInstance().getDataFolder(), this.guildSchematicFileName);

                if (! this.guildSchematicFile.exists()) {
                    FunnyGuilds.getPluginLogger().error("File with given name in field \"guild-schematic-file-name\" does not exist!");
                    this.pasteSchematicOnCreation = false;
                }
            }
        }

        this.playerListUpdateInterval_ = Math.max(1, this.playerListUpdateInterval);
        this.lastAttackerAsKillerConsiderationTimeout_ = TimeUnit.SECONDS.toMillis(this.lastAttackerAsKillerConsiderationTimeout);

        this.rankingUpdateInterval_ = Math.max(1, this.rankingUpdateInterval);
        this.pluginTaskTerminationTimeout = Math.max(1, this.pluginTaskTerminationTimeout_);

        return this;
    }

    public static class Commands extends OkaeriConfig {
        public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

        @Comment public FunnyCommand guild     = new FunnyCommand("gildia", Arrays.asList("gildie", "g"));
        @Comment public FunnyCommand create    = new FunnyCommand("zaloz");
        @Comment public FunnyCommand delete    = new FunnyCommand("usun");
        @Comment public FunnyCommand confirm   = new FunnyCommand("potwierdz");
        @Comment public FunnyCommand invite    = new FunnyCommand("zapros");
        @Comment public FunnyCommand join      = new FunnyCommand("dolacz");
        @Comment public FunnyCommand leave     = new FunnyCommand("opusc");
        @Comment public FunnyCommand kick      = new FunnyCommand("wyrzuc");
        @Comment public FunnyCommand base      = new FunnyCommand("baza");
        @Comment public FunnyCommand enlarge   = new FunnyCommand("powieksz");
        @Comment public FunnyCommand ally      = new FunnyCommand("sojusz");
        @Comment public FunnyCommand war       = new FunnyCommand("wojna");
        @Comment public FunnyCommand items     = new FunnyCommand("przedmioty");
        @Comment public FunnyCommand escape    = new FunnyCommand("ucieczka", Collections.singletonList("escape"));
        @Comment public FunnyCommand rankReset = new FunnyCommand("rankreset", Collections.singletonList("resetrank"));
        @Comment public FunnyCommand tnt       = new FunnyCommand("tnt");

        @CustomKey("break")
        @Comment public FunnyCommand break_ = new FunnyCommand("rozwiaz");

        @Comment public FunnyCommand info     = new FunnyCommand("info");
        @Comment public FunnyCommand player   = new FunnyCommand("gracz");
        @Comment public FunnyCommand top      = new FunnyCommand("top", Collections.singletonList("top10"));
        @Comment public FunnyCommand validity = new FunnyCommand("przedluz");
        @Comment public FunnyCommand leader   = new FunnyCommand("lider", Collections.singletonList("zalozyciel"));
        @Comment public FunnyCommand deputy   = new FunnyCommand("zastepca");
        @Comment public FunnyCommand ranking  = new FunnyCommand("ranking");
        @Comment public FunnyCommand setbase  = new FunnyCommand("ustawbaze", Collections.singletonList("ustawdom"));
        @Comment public FunnyCommand pvp      = new FunnyCommand("pvp", Collections.singletonList("ustawpvp"));

        @Comment({"", "Komendy administratora"})
        public AdminCommands admin = new AdminCommands();

        public static class FunnyCommand extends OkaeriConfig {

            public String name;
            public List<String> aliases;
            public boolean enabled;

            public FunnyCommand() {
            }

            public FunnyCommand(String name) {
                this(name, Collections.emptyList(), true);
            }

            public FunnyCommand(String name, List<String> aliases) {
                this(name, aliases, true);
            }

            public FunnyCommand(String name, List<String> aliases, boolean enabled) {
                this.name = name;
                this.aliases = aliases;
                this.enabled = enabled;
            }
        }

        public static class AdminCommands extends OkaeriConfig {
            public String main = "ga";
            public String add = "ga dodaj";
            public String delete = "ga usun";
            public String kick = "ga wyrzuc";
            public String teleport = "ga tp";
            public String points = "ga points";
            public String kills = "ga kills";
            public String deaths = "ga deaths";
            public String ban = "ga ban";
            public String lives = "ga zycia";
            public String move = "ga przenies";
            public String unban = "ga unban";
            public String validity = "ga przedluz";
            public String name = "ga nazwa";
            public String tag = "ga tag";
            public String spy = "ga spy";
            public String enabled = "ga enabled";
            public String leader = "ga lider";
            public String deputy = "ga zastepca";
            public String protection = "ga ochrona";
            public String base = "ga baza";
        }
    }

    public enum DataModel {
        FLAT,
        MYSQL
    }

    public static class MySQL extends OkaeriConfig {

        @Variable("FG_MYSQL_HOSTNAME")
        public String hostname;
        @Variable("FG_MYSQL_PORT")
        public int port;
        @Variable("FG_MYSQL_DATABASE")
        public String database;
        @Variable("FG_MYSQL_USER")
        public String user;
        @Variable("FG_MYSQL_PASSWORD")
        public String password;

        @Variable("FG_MYSQL_POOL_SIZE")
        public int     poolSize;
        @Variable("FG_MYSQL_CONNECTION_TIMEOUT")
        public int     connectionTimeout;
        @Variable("FG_MYSQL_USE_SSL")
        public boolean useSSL;

        @Variable("FG_MYSQL_USERS_TABLE_NAME")
        public String usersTableName;
        @Variable("FG_MYSQL_GUILDS_TABLE_NAME")
        public String guildsTableName;
        @Variable("FG_MYSQL_REGIONS_TABLE_NAME")
        public String regionsTableName;

        public MySQL() {
        }

        public MySQL(String hostname, int port, String database, String user, String password, int poolSize, int connectionTimeout, boolean useSSL, String usersTableName, String guildsTableName, String regionsTableName) {
            this.hostname = hostname;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
            this.connectionTimeout = connectionTimeout;
            this.useSSL = useSSL;
            this.usersTableName = usersTableName;
            this.guildsTableName = guildsTableName;
            this.regionsTableName = regionsTableName;
        }
    }

}
