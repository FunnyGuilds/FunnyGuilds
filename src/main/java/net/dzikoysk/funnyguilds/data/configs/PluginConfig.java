package net.dzikoysk.funnyguilds.data.configs;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.GuildRegex;
import net.dzikoysk.funnyguilds.basic.util.RankSystem;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.FunnyLogger;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.ItemBuilder;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgExclude;
import org.diorite.cfg.annotations.CfgName;
import org.diorite.cfg.annotations.CfgStringStyle;
import org.diorite.cfg.annotations.CfgStringStyle.StringStyle;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@CfgClass(name = "PluginConfig")
@CfgDelegateDefault("{new}")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("                                #")
@CfgComment("          FunnyGuilds           #")
@CfgComment("         4.3.0 Tribute          #")
@CfgComment("                                #")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
public class PluginConfig {

    @CfgExclude
    public final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();
    
    @CfgExclude
    public SimpleDateFormat dateFormat;
    
    @CfgComment("Wyswietlana nazwa pluginu")
    @CfgName("plugin-name")
    public String pluginName = "FunnyGuilds";
    
    @CfgComment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    @CfgName("update-info")
    public boolean updateInfo = true;

    @CfgComment("Mozliwosc zakladania gildii (mozna zmienic takze za pomoca komendy /ga enabled)")
    @CfgName("guilds-enabled")
    public boolean guildsEnabled = true;

    @CfgComment("Czy tworzenie regionow gildii (i inne zwiazane z nimi rzeczy) maja byc wlaczone")
    @CfgComment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @CfgComment("Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @CfgComment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @CfgComment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    @CfgName("regions-enabled")
    public boolean regionsEnabled = true;
    
    @CfgComment("Maksymalna dlugosc nazwy gildii")
    @CfgName("name-length")
    public int createNameLength = 22;

    @CfgComment("Minimalna dlugosc nazwy gildii")
    @CfgName("name-min-length")
    public int createNameMinLength = 4;

    @CfgComment("Maksymalna dlugosc tagu gildii")
    @CfgName("tag-length")
    public int createTagLength = 4;

    @CfgComment("Minimalna dlugosc tagu gildii")
    @CfgName("tag-min-length")
    public int createTagMinLength = 2;
    
    @CfgComment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @CfgComment("Dostepne zasady:")
    @CfgComment("LOWERCASE - umozliwia uzycie tylko malych liter")
    @CfgComment("UPPERCASE - umozliwia uzycie tylko duzych liter")
    @CfgComment("DIGITS - umozliwia uzycie tylko cyfr")
    @CfgComment("LOWERCASE_DIGITS - umozliwia uzycie malych liter i cyfr")
    @CfgComment("UPPERCASE_DIGITS - umozliwia uzycie duzych liter i cyfr")
    @CfgComment("LETTERS - umozliwia uzycie malych i duzych liter")
    @CfgComment("LETTERS_DIGITS - umozliwia uzycie malych i duzych liter oraz cyrf")
    @CfgComment("LETTERS_DIGITS_UNDERSCORE - umozliwia uzycie malych i duzych liter, cyrf oraz podkreslnika")
    @CfgName("name-regex")
    public String nameRegex_ = "LETTERS";
    
    @CfgExclude
    public GuildRegex nameRegex;
    
    @CfgComment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @CfgComment("Mozliwe zasady sa takie same jak w przypadku name-regex")
    @CfgName("tag-regex")
    public String tagRegex_ = "LETTERS";
    
    @CfgExclude
    public GuildRegex tagRegex;

    @CfgComment("Minimalna liczba graczy w gildii, aby zaliczala sie ona do rankingu")
    @CfgName("guild-min-members")
    public int minMembersToInclude = 3;

    @CfgComment("Przedmioty wymagane do zalozenia gildii")
    @CfgComment("Tylko wartosci ujete w <> sa wymagane, reszta (ujeta w []) jest opcjonalna")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant:eggtype:skullowner:armorcolor]")
    @CfgComment("")
    @CfgComment("Zamiast spacji wstawiaj podkreslnik: _")
    @CfgComment("Aby zrobic nowa linie lore wstaw hash: #")
    @CfgComment("Aby w lore uzyc znaku # wstaw {HASH}")
    @CfgComment("")
    @CfgComment("eggtype to typ jajka do spawnu moba, uzywane tylko gdy typem przedmiotu jest MONSTER_EGG")
    @CfgComment("skullowner to nick gracza, ktorego glowa jest tworzona, uzywane tylko gdy typem przedmiotu jest SKULL_ITEM")
    @CfgComment("armorcolor to kolor, w ktorym bedzie przedmiot, uzywane tylko gdy przedmiot jest czescia zbroi skorzanej")
    @CfgComment("Kolor musi byc podany w postaci: \"R_G_B\"")
    @CfgComment("")
    @CfgComment("UWAGA: Nazwy przedmiotow musza pasowac do nazw podanych tutaj: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
    @CfgComment("UWAGA: Typ jajka musi pasowac do typow entity podanych tutaj: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html")
    @CfgName("items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> items_ = Arrays.asList("5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!", "5 dirt", "5 tnt");

    @CfgExclude
    public List<ItemStack> createItems;

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii")
    @CfgName("required-experience")
    public int requiredExperience = 0;

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money")
    public double requiredMoney = 0;

    @CfgComment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("items-vip")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> itemsVip_ = Collections.singletonList("1 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!");

    @CfgExclude
    public List<ItemStack> createItemsVip;

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("required-experience-vip")
    public int requiredExperienceVip = 0;

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money-vip")
    public double requiredMoneyVip = 0;

    @CfgComment("Czy opcja wymaganego rankingu do zalozenia gildi ma byc wlaczona?")
    @CfgName("rank-create-enable")
    public boolean rankCreateEnable = true;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi")
    @CfgName("rank-create")
    public int rankCreate = 1000;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi dla osoby z uprawnieniem funnyguilds.vip.rank")
    @CfgName("rank-create-vip")
    public int rankCreateVip = 800;

    @CfgComment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @CfgComment("Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    @CfgName("use-common-gui")
    public boolean useCommonGUI = false;
    
    @CfgComment("GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Jesli wlaczone jest use-common-gui - ponizsze GUI jest uzywane takze dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Kazda linijka listy oznacza jeden slot, liczba slotow powinna byc wielokrotnoscia liczby 9 i nie powinna byc wieksza niz 54")
    @CfgComment("Aby uzyc przedmiotu stworzonego w jednym slocie w innym mozna uzyc {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, ktory jest w pierwszym slocie")
    @CfgComment("Aby wstawic przedmiot na gildie nalezy uzyc {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildie")
    @CfgComment("Aby wstawic przedmiot na gildie z listy vip nalezy uzyc {VIPITEM-nr}")
    @CfgName("gui-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItems_ = Arrays.asList("1 stained_glass_pane name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
                    "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
                    "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");
    
    @CfgExclude
    public List<ItemStack> guiItems;
    
    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-title")
    public String guiItemsTitle_ = "&5&lPrzedmioty na gildie";
    
    @CfgExclude
    public String guiItemsTitle;
    
    @CfgComment("GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @CfgComment("Ponizsze GUI bedzie ignorowane jesli wlaczone jest use-common-gui")
    @CfgName("gui-items-vip")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItemsVip_ = Arrays.asList("1 stained_glass_pane name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
                    "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
                    "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @CfgExclude
    public List<ItemStack> guiItemsVip;
    
    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-vip-title")
    public String guiItemsVipTitle_ = "&5&lPrzedmioty na gildie (VIP)";
    
    @CfgExclude
    public String guiItemsVipTitle;
    
    @CfgComment("Czy do przedmiotow na gildie, ktore sa w GUI, maja byc dodawane dodatkowe linie opisu?")
    @CfgComment("Linie te mozna ustawic ponizej")
    @CfgName("add-lore-lines")
    public boolean addLoreLines = true;
    
    @CfgComment("Dodatkowe linie opisu, dodawane do kazdego przedmiotu, ktory jest jednoczesnie przedmiotem na gildie")
    @CfgComment("Dodawane linie nie zaleza od otwieranego GUI - sa wspolne dla zwyklego i VIP")
    @CfgComment("Mozliwe do uzycia zmienne:")
    @CfgComment("{PINV-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie")
    @CfgComment("{PINV-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie")
    @CfgComment("{EC-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma w enderchescie")
    @CfgComment("{EC-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma w enderchescie")
    @CfgComment("{ALL-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie i w enderchescie")
    @CfgComment("{ALL-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    @CfgName("gui-items-lore")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public List<String> guiItemsLore_ = Arrays.asList("", "&aPosiadzasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
                    "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");
    
    @CfgExclude
    public List<String> guiItemsLore;
    
    @CfgComment("Minimalna odleglosc od spawnu")
    @CfgName("create-distance")
    public int createDistance = 100;

    @CfgComment("Minimalna odleglosc od granicy mapy, na ktorej znajduje sie gracz")
    @CfgComment("Wartosc -1 oznacza brak minimalnej odlegosci od granicy")
    @CfgName("create-guild-min-distance")
    public double createMinDistanceFromBorder = -1.0;

    @CfgComment("Blok lub entity, ktore jest sercem gildii")
    @CfgComment("Zmiana entity wymaga pelnego restartu serwera")
    @CfgComment("Bloki musza byc podawane w formacie - material:metadata (tak jak przedmioty na gildie, tylko bez ilosci, nazwy czy opisu)")
    @CfgComment("Typ entity musi byc zgodny z ta lista (i zdrowym rozsadkiem) - https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html")
    @CfgComment("UWAGA: Zmiana bloku, gdy sa juz zrobione jakies gildie, spowoduje niedzialanie ich regionow")
    @CfgName("create-type")
    public String createType = "ender_crystal";

    @CfgExclude
    public MaterialData createMaterialData;
    
    @CfgExclude
    public EntityType createEntityType;

    @CfgComment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @CfgComment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CfgName("create-center-y")
    public int createCenterY = 60;

    @CfgComment("Czy ma sie tworzyc kula z obsydianu dookola centrum gildii")
    @CfgName("create-center-sphere")
    public boolean createCenterSphere = true;

    @CfgComment("Czy przy tworzeniu gildii powinien byc wklejany schemat")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-on-creation")
    public boolean pasteSchematicOnCreation = false;

    @CfgComment("Nazwa pliku z schematem poczatkowym gildii")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("guild-schematic-file-name")
    public String guildSchematicFileName = "funnyguilds.schematic";

    @CfgComment("Czy schemat przy tworzeniu gildii powinien byc wklejany razem z powietrzem?")
    @CfgComment("Przy duzych schematach ma to wplyw na wydajnosc")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-with-air")
    public boolean pasteSchematicWithAir = true;

    @CfgExclude
    public File guildSchematicFile;

    @CfgComment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    @CfgName("bugged-blocks")
    public boolean buggedBlocks = false;

    @CfgComment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgName("bugged-blocks-timer")
    public long buggedBlocksTimer = 20L;
    
    @CfgComment("Czy klocki po 'zbugowaniu' maja zostac oddane")
    @CfgName("bugged-blocks-return")
    public boolean buggedBlockReturn = false;

    @CfgComment("Maksymalna liczba czlonkow w gildii")
    @CfgName("max-members")
    public int inviteMembers = 15;

    @CfgComment("Lista nazw swiatow, na ktorych mozliwosc utworzenia gildii powinna byc zablokowana")
    @CfgName("blocked-worlds")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    @CfgComment("Mozliwosc ucieczki z terenu innej gildii")
    @CfgComment("Funkcja niedostepna jesli mozliwosc teleportacji do gildii jest wylaczona")
    @CfgName("escape-enable")
    public boolean escapeEnable = true;
    
    @CfgComment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    @CfgName("escape-delay")
    public int escapeDelay = 120;
    
    @CfgComment("Mozliwosc teleportacji do gildii")
    @CfgName("base-enable")
    public boolean baseEnable = true;

    @CfgComment("Czas oczekiwania na teleportacje (w sekundach)")
    @CfgName("base-delay")
    public int baseDelay = 5;

    @CfgComment("Koszt teleportacji do gildii (jezeli brak, wystarczy zrobic: base-items: [])")
    @CfgName("base-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> baseItems_ = Arrays.asList("1 diamond", "1 emerald");

    @CfgExclude
    public List<ItemStack> baseItems;

    @CfgComment("Koszt dolaczenia do gildii (jezeli brak, wystarczy zrobic: join-items: [])")
    @CfgName("join-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> joinItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> joinItems;

    @CfgComment("Mozliwosc powiekszania gildii")
    @CfgName("enlarge-enable")
    public boolean enlargeEnable = true;

    @CfgComment("O ile powieksza teren gildii 1 poziom")
    @CfgName("enlarge-size")
    public int enlargeSize = 5;

    @CfgComment("Koszt powiekszania gildii")
    @CfgComment("- kazdy myslnik, to 1 poziom gildii")
    @CfgName("enlarge-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @CfgExclude
    public List<ItemStack> enlargeItems;

    @CfgComment("Wielkosc regionu gildii")
    @CfgName("region-size")
    public int regionSize = 50;

    @CfgComment("Minimalna odleglosc miedzy terenami gildii")
    @CfgName("region-min-distance")
    public int regionMinDistance = 10;

    @CfgComment("Czas wyswietlania powiadomienia na pasku powiadomien (w sekundach)")
    @CfgName("region-notification-time")
    public int regionNotificationTime = 15;

    @CfgComment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza (w sekundach)")
    @CfgName("region-notification-cooldown")
    public int regionNotificationCooldown = 60;

    @CfgComment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CfgName("region-commands")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionCommands = Collections.singletonList("sethome");

    @CfgComment("Czy mozna usunac gildie jezeli ktos spoza gildii jest na jej terenie")
    @CfgName("region-delete-if-near")
    public boolean regionDeleteIfNear = false;
    
    @CfgComment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
    @CfgName("guild-tnt-protection-enabled")
    public boolean guildTNTProtectionEnabled = false;
    
    @CfgComment("O której godzinie ma sie zaczac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-start-time")
    public String guildTNTProtectionStartTime_ = "22:00";
    
    @CfgExclude
    public LocalDateTime guildTNTProtectionStartTime;
    
    @CfgComment("Do której godziny ma dzialac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-end-time")
    public String guildTNTProtectionEndTime_ = "06:00";
    
    @CfgExclude
    public LocalDateTime guildTNTProtectionEndTime;

    @CfgComment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    @CfgName("region-explode")
    public int regionExplode = 120;

    @CfgComment("Jaki ma byc zasieg pobieranych przedmiotow po wybuchu (jezeli chcesz wylaczyc, wpisz 0)")
    @CfgName("explode-radius")
    public int explodeRadius = 3;

    @CfgComment("Jakie materialy i z jaka szansa maja byc niszczone po wybuchu")
    @CfgComment("<material>: <szansa (w %)")
    @CfgName("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of("obsidian", 20.0, "water", 33.0, "lava", 33.0);

    @CfgExclude
    public Map<Material, Double> explodeMaterials;

    @CfgComment("Ile zyc ma gildia")
    @CfgName("war-lives")
    public int warLives = 3;

    @CfgComment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CfgName("war-protection")
    public String warProtection_ = "24h";

    @CfgExclude
    public long warProtection;

    @CfgComment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CfgName("war-wait")
    public String warWait_ = "24h";

    @CfgExclude
    public long warWait;

    @CfgComment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    @CfgName("war-tnt-protection")
    public boolean warTntProtection = true;

    @CfgComment("Jaka waznosc ma gildia po jej zalozeniu")
    @CfgName("validity-start")
    public String validityStart_ = "14d";

    @CfgExclude
    public long validityStart;

    @CfgComment("Ile czasu dodaje przedluzenie gildii")
    @CfgName("validity-time")
    public String validityTime_ = "14d";

    @CfgExclude
    public long validityTime;

    @CfgComment("Ile dni przed koncem wygasania mozna przedluzyc gildie (wpisz 0, jezeli funkcja ma byc wylaczona)")
    @CfgName("validity-when")
    public String validityWhen_ = "14d";

    @CfgExclude
    public long validityWhen;

    @CfgComment("Koszt przedluzenia gildii")
    @CfgName("validity-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> validityItems_ = Collections.singletonList("10 diamond");

    @CfgExclude
    public List<ItemStack> validityItems;

    @CfgComment("Ranking od ktorego rozpoczyna gracz")
    @CfgName("rank-start")
    public int rankStart = 1000;

    @CfgComment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    @CfgName("rank-farming-protect")
    public boolean rankFarmingProtect = true;

    @CfgComment("Czas (w sekundach) blokady nabijania rankingu po walce dwoch osob")
    @CfgName("rank-farming-cooldown")
    public int rankFarmingCooldown = 7200;
    
    @CfgComment("Czy ma byc zablokowana zmiana rankingu, jesli obie osoby z walki maja taki sam adres IP")
    @CfgName("rank-ip-protect")
    public boolean rankIPProtect = false;

    @CfgComment("Czy system asyst ma byc wlaczony")
    @CfgName("rank-assist-enable")
    public boolean assistEnable = true;

    @CfgComment("Limit asyst (liczba ujemna = wylaczony)")
    @CfgName("assists-limit")
    public int assistsLimit = -1;
    
    @CfgComment("Jaka czesc rankingu za zabicie idzie na konto zabojcy")
    @CfgComment("1 to caly ranking, 0 to nic")
    @CfgComment("Reszta rankingu rozdzielana jest miedzy osoby asystujace w zaleznosci od zadanych obrazen")
    @CfgName("rank-assist-killer-share")
    public double assistKillerShare = 0.5;
    
    @CfgComment("System rankingowy uzywany przez plugin, do wyboru:")
    @CfgComment("ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @CfgComment("PERCENT - system, ktory obu graczom zabiera procent rankingu osoby zabitej")
    @CfgComment("STATIC - system, ktory zawsze zabiera iles rankingu zabijajacemu i iles zabitemu")
    @CfgName("rank-system")
    public String rankSystem_ = "ELO";

    @CfgExclude
    public RankSystem rankSystem;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Lista stalych obliczen rankingowych ELO, uzywanych przy zmianach rankingu - im mniejsza stala, tym mniejsze zmiany rankingu")
    @CfgComment("Stale okreslaja tez o ile maksymalnie moze zmienic sie ranking pochodzacy z danego przedzialu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank stala\", np.: \"0-1999 32\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"2401-* 16\"")
    @CfgName("elo-constants")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");
    
    @CfgExclude
    public Map<IntegerRange, Integer> eloConstants;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-divider")
    public double eloDivider = 400.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-exponent")
    public double eloExponent = 10.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest PERCENT!")
    @CfgComment("Procent rankingu osoby zabitej o jaki zmienia sie rankingi po walce")
    @CfgName("percent-rank-change")
    public double percentRankChange = 1.0;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty dawane osobie, ktora wygrywa walke")
    @CfgName("static-attacker-change")
    public int staticAttackerChange = 15;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty zabierane osobie, ktora przegrywa walke")
    @CfgName("static-victim-change")
    public int staticVictimChange = 10;

    @CfgComment("Czy pokazywac informacje przy kliknieciu PPM na gracza")
    @CfgName("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @CfgComment("Cooldown pomiedzy pokazywaniem informacji przez PPM (w sekundach)")
    @CfgName("info-player-cooldown")
    public int infoPlayerCooldown = 0;

    @CfgComment("Czy trzeba kucac, zeby przy klikniecu PPM na gracza wyswietlilo informacje o nim")
    @CfgName("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @CfgComment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CfgName("damage-guild")
    public boolean damageGuild = false;

    @CfgComment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CfgName("damage-ally")
    public boolean damageAlly = false;

    @CfgComment("Wyglad znaczika {POS} wstawionego w format chatu")
    @CfgComment("Znacznik ten pokazuje czy ktos jest liderem, zastepca czy zwyklym czlonkiem gildii")
    @CfgName("chat-position")
    public String chatPosition_ = "&b{POS} ";
    
    @CfgExclude
    public String chatPosition;
    
    @CfgComment("Znacznik dla lidera gildii")
    @CfgName("chat-position-leader")
    public String chatPositionLeader = "**";
    
    @CfgComment("Znacznik dla zastepcy gildii")
    @CfgName("chat-position-deputy")
    public String chatPositionDeputy = "*";
    
    @CfgComment("Znacznik dla czlonka gildii")
    @CfgName("chat-position-member")
    public String chatPositionMember = "";
    
    @CfgComment("Wyglad znaczika {TAG} wstawionego w format chatu")
    @CfgName("chat-guild")
    public String chatGuild_ = "&b{TAG} ";

    @CfgExclude
    public String chatGuild;

    @CfgComment("Wyglad znaczika {RANK} wstawionego w format chatu")
    @CfgName("chat-rank")
    public String chatRank_ = "&b{RANK} ";

    @CfgExclude
    public String chatRank;
    
    @CfgComment("Wyglad znaczika {POINTS} wstawionego w format chatu")
    @CfgComment("Mozesz tu takze uzyc znacznika {POINTS-FORMAT}")
    @CfgName("chat-points")
    public String chatPoints_ = "&b{POINTS} ";

    @CfgExclude
    public String chatPoints;

    @CfgComment("Wyglad znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zaleznosci od wartosci punktow")
    @CfgComment("{G-POINTS-FORMAT} (tak samo jak {G-POINTS}) jest uzywane jedynie na liscie graczy")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank wyglad\", np.: \"0-750 &4{POINTS}\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    @CfgName("points-format")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> pointsFormat_ = Arrays.asList("0-749 &4{POINTS}", "750-999 &c{POINTS}", "1000-1499 &a{POINTS}", "1500-* &6&l{POINTS}");
    
    @CfgExclude
    public Map<IntegerRange, String> pointsFormat;
    
    @CfgComment("Znacznik z punktami dodawany do zmiennej {PTOP-x} i {ONLINE-PTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam nick - nie podawaj tu nic")
    @CfgName("ptop-points")
    public String ptopPoints_ = " &7[{POINTS}&7]";
    
    @CfgExclude
    public String ptopPoints;
    
    @CfgComment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam tag - nie podawaj tu nic")
    @CfgName("gtop-points")
    public String gtopPoints_ = " &7[&b{POINTS-FORMAT}&7]";
    
    @CfgExclude
    public String gtopPoints;

    @CfgComment("Wyglad znacznika {PING-FORMAT} w zaleznosci od wartosci pingu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych wartosci i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minPing-maxPing wyglad\", np.: \"0-75 &a{PING}\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minPing w gore, np.: \"301-* &c{PING}\"")
    @CfgName("ping-format")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> pingFormat_ = Arrays.asList("0-75 &a{PING}", "76-150 &e{PING}", "151-300 &c{PING}", "301-* &c{PING}");
    
    @CfgExclude
    public Map<IntegerRange, String> pingFormat;
    
    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do gildii gildii")
    @CfgName("chat-priv")
    public String chatPriv = "!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do sojusznikow gildii")
    @CfgName("chat-ally")
    public String chatAlly = "!!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do wszystkich gildii")
    @CfgName("chat-global")
    public String chatGlobal = "!!!";

    @CfgComment("Wyglad wiadomosci wysylanej na czacie gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-priv-design")
    public String chatPrivDesign_ = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatPrivDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-ally-design")
    public String chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatAllyDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-global-design")
    public String chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatGlobalDesign;

    @CfgComment("Wyglad tagu osob w gildii")
    @CfgName("prefix-our")
    public String prefixOur_ = "&a{TAG}&f ";

    @CfgExclude
    public String prefixOur;

    @CfgComment("Wyglad tagu gildii sojuszniczej")
    @CfgName("prefix-allies")
    public String prefixAllies_ = "&6{TAG}&f ";

    @CfgExclude
    public String prefixAllies;

    @CfgExclude
    public String prefixEnemies;

    @CfgComment("Wyglad tagu gildii neutralnej (widziany rowniez przez graczy bez gildii)")
    @CfgName("prefix-other")
    public String prefixOther_ = "&7{TAG}&f ";

    @CfgExclude
    public String prefixOther;
    
    @CfgComment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-offline) pusta")
    @CfgName("ptop-online")
    public String ptopOnline_ = "&a";
    
    @CfgExclude
    public String ptopOnline;
    
    @CfgComment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-online) pusta")
    @CfgName("ptop-offline")
    public String ptopOffline_ = "&c";
    
    @CfgExclude
    public String ptopOffline;

    @CfgComment("Czy wlaczyc dummy z punktami")
    @CfgComment("UWAGA - zalecane jest wylaczenie tej opcji w przypadku konfliktow z BungeeCord'em, wiecej szczegolow tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
    @CfgName("dummy-enable")
    public boolean dummyEnable = true;

    @CfgComment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CfgName("dummy-suffix")
    public String dummySuffix_ = "pkt";

    @CfgExclude
    public String dummySuffix;

    @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_QUOTED)
    @CfgComment("Wyglad listy graczy, przedzial slotow - od 1 do 80")
    @CfgComment("Schemat wygladu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/tab-scheme.png")
    @CfgComment("> Spis zmiennych gracza:")
    @CfgComment("{PLAYER} - nazwa gracza")
    @CfgComment("{PING} - ping gracza")
    @CfgComment("{PING-FORMAT} - ping gracza z formatowaniem")
    @CfgComment("{POINTS} - punkty gracza")
    @CfgComment("{POINTS-FORMAT} - punkty gracza z formatowaniem")
    @CfgComment("{POSITION} - pozycja gracza w rankingu")
    @CfgComment("{KILLS} - liczba zabojstw gracza")
    @CfgComment("{DEATHS} - liczba smierci gracza")
    @CfgComment("{KDR} - stosunek zabojstw do smierci gracza")
    @CfgComment("> Spis zmiennych gildyjnych:")
    @CfgComment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @CfgComment("{G-TAG} - tag gildii gracza")
    @CfgComment("{G-OWNER} - wlasciciel gildii")
    @CfgComment("{G-DEPUTIES} - zastepcy gildii")
    @CfgComment("{G-DEPUTY} - losowy z zastepcow gildii")
    @CfgComment("{G-LIVES} - liczba zyc gildii")
    @CfgComment("{G-ALLIES} - liczba sojusznikow gildii")
    @CfgComment("{G-POINTS} - punkty gildii")
    @CfgComment("{G-POINTS-FORMAT} - punkty gildii z formatowaniem")
    @CfgComment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @CfgComment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @CfgComment("{G-DEATHS} - suma smierci czlonkow gildii")
    @CfgComment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @CfgComment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @CfgComment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @CfgComment("{G-VALIDITY} - data wygasniecia gildii")
    @CfgComment("{G-REGION-SIZE} - rozmiar gildii")
    @CfgComment("> Spis pozostalych zmiennych:")
    @CfgComment("{GUILDS} - liczba gildii na serwerze")
    @CfgComment("{USERS} - liczba uzytkownikow serwera")
    @CfgComment("{ONLINE} - liczba graczy online")
    @CfgComment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @CfgComment("{SECOND} - Sekunda")
    @CfgComment("{MINUTE} - Minuta")
    @CfgComment("{HOUR} - Godzina")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CfgName("player-list")
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

    @CfgComment("Wyglad naglowka w liscie graczy.")
    @CfgName("player-list-header")
    public String playerListHeader = "&7FunnyGuilds &b4.3.0 Tribute";

    @CfgComment("Wyglad stopki w liscie graczy.")
    @CfgName("player-list-footer")
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";

    @CfgComment("Liczba pingu pokazana przy kazdej komorce.")
    @CfgName("player-list-ping")
    public int playerListPing = 0;

    @CfgComment("Czy tablista ma byc wlaczona")
    @CfgName("player-list-enable")
    public boolean playerlistEnable = true;

    @CfgComment("Czy tag gildii podany przy tworzeniu gildii powinien zachowac forme taka, w jakiej zostal wpisany")
    @CfgComment("UWAGA: Gdy ta opcja jest wlaczona, opcja \"guild-tag-uppercase\" nie bedzie miala wplywu na tag gildii")
    @CfgName("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @CfgComment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @CfgComment("Dziala dopiero po zrestartowaniu serwera")
    @CfgName("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @CfgComment("Czy wlaczyc tlumaczenie nazw przedmiotow przy zabojstwie")
    @CfgName("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @CfgComment("Tlumaczenia nazw przedmiotow przy zabojstwie")
    @CfgComment("Wypisywac w formacie nazwa_przedmiotu: \"tlumaczona nazwa przedmiotu\"")
    @CfgName("translated-materials-name")
    public Map<String, String> translatedMaterials_ = ImmutableMap.<String, String>builder()
            .put("diamond_sword", "&3diamentowy miecz")
            .put("iron_sword", "&7zelazny miecz")
            .build();

    @CfgExclude
    public Map<Material, String> translatedMaterials;

    @CfgComment("Czy filtry nazw i tagow gildii powinny byc wlaczone")
    @CfgName("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @CfgComment("Niedozwolone nazwy przy zakladaniu gildii")
    @CfgName("restricted-guild-names")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @CfgComment("Niedozwolone tagi przy zakladaniu gildii")
    @CfgName("restricted-guild-tags")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @CfgComment("Czy powiadomienia o wejsciu na teren gildii czlonka gildii powinny byc wyswietlane")
    @CfgName("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    @CfgComment("Gdzie maja pojawiac sie wiadomosci zwiazane z poruszaniem sie po terenach gildii")
    @CfgComment("Mozliwe miejsca wyswietlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CfgName("region-move-notification-style")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionEnterNotificationStyle_ = Arrays.asList("ACTIONBAR", "BOSSBAR");
    
    @CfgExclude
    public List<NotificationStyle> regionEnterNotificationStyle = new ArrayList<>();
    
    @CfgComment("Jak dlugo title/subtitle powinien sie pojawiac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-in")
    public int notificationTitleFadeIn = 10;

    @CfgComment("Jak dlugo title/subtitle powinien pozostac na ekranie gracza")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-stay")
    public int notificationTitleStay = 10;

    @CfgComment("Jak dlugo title/subtitle powinien znikac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-out")
    public int notificationTitleFadeOut = 10;

    @CfgComment("Zbior przedmiotow potrzebnych do resetu rankingu")
    @CfgName("rank-reset-needed-items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> rankResetItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> rankResetItems;

    @CfgComment("Czy przy szukaniu danych o graczu ma byc pomijana wielkosc znakow")
    @CfgName("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @CfgComment("Nazwy komend")
    @CfgName("commands")
    public Commands commands = new Commands();

    @CfgComment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CfgName("event-move")
    public boolean eventMove = true;
    @CfgExclude
    public boolean eventPhysics;

    @CfgComment("Ilość wątków używanych przez ConcurrencyManager")
    @CfgName("concurrency-threads")
    public int concurrencyThreads = 1;

    @CfgComment("Co ile minut ma automatycznie zapisywac dane")
    @CfgName("data-interval")
    public int dataInterval = 1;

    @CfgComment("Typ zapisu danych")
    @CfgComment("Flat - Lokalne pliki")
    @CfgComment("MySQL - baza danych")
    @CfgName("data-type")
    public DataType dataType = new DataType(true, false);

    @CfgComment("Dane wymagane do polaczenia z baza")
    @CfgComment("UWAGA: connectionTimeout jest w milisekundach!")
    @CfgComment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @CfgComment("Najlepiej zmieniac te nazwy tylko wtedy, gdy jest naprawde taka potrzeba (np. wystepuje konflikt z innym pluginem)")
    @CfgComment("Aby zmienic nazwy tabel, gdy masz juz w bazie jakies dane z FG:")
    @CfgComment("1. Wylacz serwer")
    @CfgComment("2. Zmien dane w configu FG")
    @CfgComment("3. Zmien nazwy tabel w bazie uzywajac np. phpMyAdmin")
    @CfgName("mysql")
    public MySQL mysql = new MySQL("localhost", 3306, "db", "root", "passwd", 30000, "users", "guilds", "regions");

    private List<ItemStack> loadItemStackList(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        for (String item : strings) {
            if (item == null || "".equals(item)) {
                continue;
            }
            
            ItemStack itemstack = Parser.parseItem(item);
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
                int index = Parser.getIndex(var);

                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            }
            else if (var.contains("VIPITEM-")) {
                try {
                    int index = Parser.getIndex(var);

                    if (index > 0 && index <= createItemsVip.size()) {
                        item = createItemsVip.get(index - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                    FunnyLogger.parser("Index given in " + var + " is > " + createItemsVip.size() + " or <= 0");
                }
            }
            else if (var.contains("ITEM-")) {
                try {
                    int index = Parser.getIndex(var);

                    if (index > 0 && index <= createItems.size()) {
                        item = createItems.get(index - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                    FunnyLogger.parser("Index given in " + var + " is > " + createItems.size() + " or <= 0");
                }
            }
            else {
                item = Parser.parseItem(var);
            }

            if (item == null) {
                item = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setName("&c&lERROR IN GUI CREATION: " + var, true).getItem();
            }

            items.add(item);
        }
        
        return items;
    }
    
    public void reload() {
        this.dateFormat = new SimpleDateFormat(Messages.getInstance().dateFormat);
        
        try {
            this.nameRegex = GuildRegex.valueOf(this.nameRegex_.toUpperCase());
        } catch (Exception e) {
            this.nameRegex = GuildRegex.LETTERS;
            FunnyLogger.exception(new IllegalArgumentException("\"" + this.nameRegex_ + "\" is not a valid regex option!"));
        }
        
        try {
            this.tagRegex = GuildRegex.valueOf(this.tagRegex_.toUpperCase());
        } catch (Exception e) {
            this.tagRegex = GuildRegex.LETTERS;
            FunnyLogger.exception(new IllegalArgumentException("\"" + this.tagRegex_ + "\" is not a valid regex option!"));
        }
        
        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);
        
        this.guiItems = loadGUI(this.guiItems_);
        if (!useCommonGUI) {
            this.guiItemsVip = loadGUI(this.guiItemsVip_);
        }
        
        this.guiItemsTitle = StringUtils.colored(this.guiItemsTitle_);
        this.guiItemsVipTitle = StringUtils.colored(this.guiItemsVipTitle_);
        this.guiItemsLore = StringUtils.colored(this.guiItemsLore_);
        
        try {
            this.createEntityType = EntityType.valueOf(this.createType.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
            this.createMaterialData = Parser.parseMaterialData(this.createType, true);
        }

        if (this.createMaterialData != null && this.createMaterialData.getItemType() == Material.DRAGON_EGG) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        } else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        if (this.buggedBlocksTimer < 0L) {
            FunnyLogger.exception(new IllegalArgumentException("The field named \"bugged-blocks-timer\" can not be less than zero!"));
            this.buggedBlocksTimer = 20L; // default value
        }

        try {
            this.rankSystem = RankSystem.valueOf(this.rankSystem_.toUpperCase());
        } catch (Exception e) {
            this.rankSystem = RankSystem.ELO;
            FunnyLogger.exception(new IllegalArgumentException("\"" + this.rankSystem_ + "\" is not a valid rank system!"));
        }

        if (this.rankSystem == RankSystem.ELO) {
            Map<IntegerRange, Integer> parsedData = new HashMap<>();
            for(Entry<IntegerRange, String> entry : Parser.parseIntegerRange(this.eloConstants_, false).entrySet()) {
                try {
                    parsedData.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException e) {
                    FunnyLogger.parser("\"" + entry.getValue() + "\" is not a valid elo constant!");
                }
            }
            
            this.eloConstants = parsedData;
        }

        HashMap<Material, Double> map = new HashMap<>();
        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            Material material = Parser.parseMaterial(entry.getKey(), true);
            if (material == null || material == Material.AIR) {
                continue;
            }
            
            double chance = entry.getValue();
            if (chance == 0) {
                continue;
            }
            
            map.put(material, chance);
        }
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        LocalTime startTime = LocalTime.parse(guildTNTProtectionStartTime_, timeFormatter);
        LocalTime endTime = LocalTime.parse(guildTNTProtectionEndTime_, timeFormatter);
                
        this.guildTNTProtectionStartTime = LocalDateTime.of(LocalDate.now(), startTime);
        this.guildTNTProtectionEndTime = LocalDateTime.of(LocalDate.now(), endTime);
        
        if (this.guildTNTProtectionEndTime.isBefore(guildTNTProtectionStartTime)) {
            this.guildTNTProtectionEndTime = this.guildTNTProtectionEndTime.plusDays(1);
        }

        this.explodeMaterials = map;

        this.translatedMaterials = new HashMap<>();
        for (String materialName : translatedMaterials_.keySet()) {
            Material material = Material.matchMaterial(materialName.toUpperCase());
            if (material == null) {
                continue;
            }
            
            translatedMaterials.put(material, translatedMaterials_.get(materialName));
        }

        for (String s : this.regionEnterNotificationStyle_) {
            this.regionEnterNotificationStyle.add(NotificationStyle.valueOf(s.toUpperCase()));
        }

        if (this.notificationTitleFadeIn <= 0) {
            FunnyLogger.exception(new IllegalArgumentException("The field named \"notification-title-fade-in\" can not be less than or equal to zero!"));
            this.notificationTitleFadeIn = 10;
        }

        if (this.notificationTitleStay <= 0) {
            FunnyLogger.exception(new IllegalArgumentException("The field named \"notification-title-stay\" can not be less than or equal to zero!"));
            this.notificationTitleStay = 10;
        }

        if (this.notificationTitleFadeOut <= 0) {
            FunnyLogger.exception(new IllegalArgumentException("The field named \"notification-title-fade-out\" can not be less than or equal to zero!"));
            this.notificationTitleFadeOut = 10;
        }

        this.rankResetItems = loadItemStackList(this.rankResetItems_);

        this.warProtection = Parser.parseTime(this.warProtection_);
        this.warWait = Parser.parseTime(this.warWait_);

        this.validityStart = Parser.parseTime(this.validityStart_);
        this.validityTime = Parser.parseTime(this.validityTime_);
        this.validityWhen = Parser.parseTime(this.validityWhen_);

        this.validityItems = this.loadItemStackList(this.validityItems_);

        this.joinItems = this.loadItemStackList(this.joinItems_);
        this.baseItems = this.loadItemStackList(this.baseItems_);

        this.prefixOur = StringUtils.colored(this.prefixOur_);
        this.prefixAllies = StringUtils.colored(this.prefixAllies_);
        this.prefixOther = StringUtils.colored(this.prefixOther_);
        
        this.ptopOnline = StringUtils.colored(this.ptopOnline_);
        this.ptopOffline = StringUtils.colored(this.ptopOffline_);

        this.dummySuffix = StringUtils.colored(this.dummySuffix_);

        this.chatPosition = StringUtils.colored(this.chatPosition_);
        this.chatGuild = StringUtils.colored(this.chatGuild_);
        this.chatRank = StringUtils.colored(this.chatRank_);
        this.chatPoints = StringUtils.colored(this.chatPoints_);

        this.pointsFormat = Parser.parseIntegerRange(this.pointsFormat_, true);
        this.pingFormat = Parser.parseIntegerRange(this.pingFormat_, true);
        
        this.ptopPoints = StringUtils.colored(this.ptopPoints_);
        this.gtopPoints = StringUtils.colored(this.gtopPoints_);

        this.chatPrivDesign = StringUtils.colored(this.chatPrivDesign_);
        this.chatAllyDesign = StringUtils.colored(this.chatAllyDesign_);
        this.chatGlobalDesign = StringUtils.colored(this.chatGlobalDesign_);

        if (this.pasteSchematicOnCreation) {
            if (this.guildSchematicFileName == null || this.guildSchematicFileName.isEmpty()) {
                FunnyLogger.exception(new IllegalArgumentException("The field named \"guild-schematic-file-name\" is empty, but field \"paste-schematic-on-creation\" is set to true!"));
                this.pasteSchematicOnCreation = false;
            } else {
                this.guildSchematicFile = new File(FunnyGuilds.getInstance().getDataFolder(), this.guildSchematicFileName);

                if (!this.guildSchematicFile.exists()) {
                    FunnyLogger.exception(new IllegalArgumentException("File with given name in field \"guild-schematic-file-name\" does not exist!"));
                    this.pasteSchematicOnCreation = false;
                }
            }
        }

    }

    public static class Commands {
        public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

        public FunnyCommand guild = new FunnyCommand("gildia", Arrays.asList("gildie", "g"));
        public FunnyCommand create = new FunnyCommand("zaloz");
        public FunnyCommand delete = new FunnyCommand("usun");
        public FunnyCommand confirm = new FunnyCommand("potwierdz");
        public FunnyCommand invite = new FunnyCommand("zapros");
        public FunnyCommand join = new FunnyCommand("dolacz");
        public FunnyCommand leave = new FunnyCommand("opusc");
        public FunnyCommand kick = new FunnyCommand("wyrzuc");
        public FunnyCommand base = new FunnyCommand("baza");
        public FunnyCommand enlarge = new FunnyCommand("powieksz");
        public FunnyCommand ally = new FunnyCommand("sojusz");
        public FunnyCommand items = new FunnyCommand("przedmioty");
        public FunnyCommand escape = new FunnyCommand("ucieczka", Collections.singletonList("escape"));
        public FunnyCommand rankReset = new FunnyCommand("rankreset", Collections.singletonList("resetrank"));
        @CfgName("break")
        public FunnyCommand break_ = new FunnyCommand("rozwiaz");
        public FunnyCommand info = new FunnyCommand("info");
        public FunnyCommand player = new FunnyCommand("gracz");
        public FunnyCommand top = new FunnyCommand("top", Collections.singletonList("top10"));
        public FunnyCommand validity = new FunnyCommand("przedluz");
        public FunnyCommand leader = new FunnyCommand("lider", Collections.singletonList("zalozyciel"));
        public FunnyCommand deputy = new FunnyCommand("zastepca");
        public FunnyCommand ranking = new FunnyCommand("ranking");
        public FunnyCommand setbase = new FunnyCommand("ustawbaze", Collections.singletonList("ustawdom"));
        public FunnyCommand pvp = new FunnyCommand("pvp", Collections.singletonList("ustawpvp"));
        @CfgComment("Komendy administratora")
        public AdminCommands admin = new AdminCommands();

        public static class FunnyCommand {
            @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_SINGLE_QUOTED)
            public String name;
            @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
            public List<String> aliases;
            public boolean enabled;

            public FunnyCommand() {}

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

        public static class AdminCommands {
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
            public String spy = "ga spy";
            public String enabled = "ga enabled";
            public String leader = "ga lider";
            public String deputy = "ga zastepca";
        }
    }

    public static class DataType {
        public boolean flat;
        public boolean mysql;

        public DataType() {}

        public DataType(boolean flat, boolean mysql) {
            this.flat = flat;
            this.mysql = mysql;
        }
    }

    public static class MySQL {
        public String hostname;
        public int port;
        public String database;
        public String user;
        public String password;
        public int connectionTimeout;
        public String usersTableName;
        public String guildsTableName;
        public String regionsTableName;

        public MySQL() {}

        public MySQL(String hostname, int port, String database, String user, String password, int connectionTimeout, String usersTableName, String guildsTableName, String regionsTableName) {
            this.hostname = hostname;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.connectionTimeout = connectionTimeout;
            this.usersTableName = usersTableName;
            this.guildsTableName = guildsTableName;
            this.regionsTableName = regionsTableName;
        }
    }
	
}
