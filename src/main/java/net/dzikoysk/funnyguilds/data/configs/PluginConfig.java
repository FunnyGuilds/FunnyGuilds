package net.dzikoysk.funnyguilds.data.configs;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.RankSystem;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.elo.EloUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.diorite.cfg.annotations.*;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import java.util.*;

@CfgClass(name = "PluginConfig")
@CfgDelegateDefault("{new}")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("                                #")
@CfgComment("          FunnyGuilds           #")
@CfgComment("             4.0.1              #")
@CfgComment("                                #")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
public class PluginConfig {

    @CfgComment("Wyswietlana nazwa pluginu")
    @CfgName("plugin-name")
    public String pluginName = "FunnyGuilds";

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

    @CfgComment("Minimalna ilosc graczy w gildii aby zaliczala sie do rankingu.")
    @CfgName("guild-min-members")
    public int minMembersToInclude = 3;

    @CfgComment("Przedmioty wymagane do zalozenia gildii")
    @CfgComment("Dwukropek i metadata po nazwie przedmiotu sa opcjonalne")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant]")
    @CfgComment("Spacja = _ ")
    @CfgComment("Nowa linia lore = #")
    @CfgName("items")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> items_ = Arrays.asList("5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!", "5 dirt");

    @CfgExclude
    public List<ItemStack> createItems;

    @CfgName("items-vip")
    @CfgComment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> itemsVip_ = Collections.singletonList("1 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!");

    @CfgExclude
    public List<ItemStack> createItemsVip;

    @CfgComment("Odleglosc od spawnu")
    @CfgName("create-distance")
    public int createDistance = 100;

    @CfgComment("Blok, jaki pojawi sie pod nami po stworzeniu gildii")
    @CfgName("create-material")
    public String createStringMaterial = "ender crystal";

    @CfgExclude
    public Material createMaterial;

    @CfgComment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @CfgComment("- Wpisz 0, jezeli ma byc ustalone przez gracza")
    @CfgName("create-center-y")
    public int createCenterY = 60;

    @CfgComment("Czy ma tworzyc pusta przestrzen dookola centrum gildii")
    @CfgName("create-center-sphere")
    public boolean createCenterSphere = true;

    @CfgComment("Maksymalna ilosc czlonkow w gildii")
    @CfgName("max-members")
    public int inviteMembers = 15;

    @CfgComment("Swiaty, na ktorych powinno byc zablokowane tworzenie gildii")
    @CfgName("blockedworlds")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> blockedWorlds = Collections.singletonList("some_world");

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
    public HashMap<Material, Double> explodeMaterials;

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
    public List<String> eloConstants = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

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

    @CfgComment("Czy pokazywac informacje przy kliknieciu prawym na gracza")
    @CfgName("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @CfgComment("Cooldown pomiedzy pokazywanie informacji przez prawy klik (w sekundach)")
    @CfgName("info-player-cooldown")
    public int infoPlayerCooldown = 0;

    @CfgComment("Czy trzeba kucac, zeby przy klikniecu prawym na gracza wyswietlilo informacje o nim")
    @CfgName("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @CfgComment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CfgName("damage-guild")
    public boolean damageGuild = false;

    @CfgComment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CfgName("damage-ally")
    public boolean damageAlly = false;

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
    @CfgName("chat-points")
    public String chatPoints_ = "&b{POINTS} ";

    @CfgExclude
    public String chatPoints;

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
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}")
    @CfgName("chat-priv-design")
    public String chatPrivDesign_ = "&8[&aChat gildii&8] &7{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatPrivDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}")
    @CfgName("chat-ally-design")
    public String chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatAllyDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}")
    @CfgName("chat-global-design")
    public String chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{PLAYER}&8:&f {MESSAGE}";

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

    @CfgComment("Czy wlaczyc dummy z punktami")
    @CfgName("dummy-enable")
    public boolean dummyEnable = true;

    @CfgComment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CfgName("dummy-suffix")
    public String dummySuffix_ = "pkt";

    @CfgExclude
    public String dummySuffix;

    @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_QUOTED)
    @CfgComment("Wyglad listy graczy. Przedzial od 1 do 80")
    @CfgComment("> Spis zmiennych gracza:")
    @CfgComment("{PLAYER} - nazwa gracza")
    @CfgComment("{PING} - ping gracza")
    @CfgComment("{POINTS} - punkty gracza")
    @CfgComment("{POSITION} - pozycja gracza w rankingu")
    @CfgComment("{KILLS} - liczba zabojstw gracza")
    @CfgComment("{DEATHS} - liczba smierci gracza")
    @CfgComment("> Spis zmiennych gildyjnych:")
    @CfgComment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @CfgComment("{G-TAG} - tag gildii gracza")
    @CfgComment("{G-OWNER} - wlasciciel gildii")
    @CfgComment("{G-LIVES} - liczba zyc gildii")
    @CfgComment("{G-ALLIES} - liczba sojusznikow gildii")
    @CfgComment("{G-POINTS} - punkty gildii")
    @CfgComment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @CfgComment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @CfgComment("{G-DEATHS} - suma smierci czlonkow gildii")
    @CfgComment("{G-MEMBERS-ONLINE} - ilosc czlonkow gildii online")
    @CfgComment("{G-MEMBERS-ALL} - ilosc wszystkich czlonkow gildii")
    @CfgComment("> Spis pozostalych zmiennych:")
    @CfgComment("{ONLINE} - liczba graczy online")
    @CfgComment("{TPS} - TPS serwera")
    @CfgComment("{SECOND} - Sekunda")
    @CfgComment("{MINUTE} - Minuta")
    @CfgComment("{HOUR} - Godzina")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanym miejscu w rankingu (np. {PTOP-1}, {PTOP-60})")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CfgName("player-list")
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(1, "&7Nick: &b{PLAYER}")
            .put(2, "&7Ping: &b{PING}")
            .put(3, "&7Punkty: &b{POINTS}")
            .put(4, "&7Zabojstwa: &b{KILLS}")
            .put(5, "&7Smierci: &b{DEATHS}")
            .put(7, "&7Gildia: &b{G-NAME}")
            .put(8, "&7TAG: &b{G-TAG}")
            .put(9, "&7Punkty gildii: &b{G-POINTS}")
            .put(10, "&7Pozycja gildii: &b{G-POSITION}")
            .put(11, "&7Ilosc graczy online: &b{G-MEMBERS-ONLINE}")
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
    public String playerListHeader = "&7FunnyGuilds &b4.0.1 Tribute";

    @CfgComment("Wyglad stopki w liscie graczy.")
    @CfgName("player-list-footer")
    public String playerListFooter = "&7O, dziala! &8{HOUR}:{MINUTE}:{SECOND}";

    @CfgComment("Ilosc pingu pokazana przy kazdej komorce.")
    @CfgName("player-list-ping")
    public int playerListPing = 0;

    @CfgComment("Tablista wlaczona")
    @CfgName("player-list-enable")
    public boolean playerlistEnable = false;

    @CfgComment("Co ile sekund ma odswiezac liste graczy")
    @CfgName("player-list-interval")
    public int playerlistInterval = 1;

    @CfgComment("Wyglad punktow wyswietlanych przy gildii w rankingu")
    @CfgName("player-list-points")
    public String playerlistPoints_ = "&7[&b{POINTS}&7]";

    @CfgComment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @CfgComment("Dziala dopiero po zrestartowaniu serwera")
    @CfgName("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @CfgExclude
    public String playerlistPoints;

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

    @CfgComment("Nazwy komend")
    @CfgName("commands")
    public Commands commands = new Commands();
    @CfgComment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CfgName("event-move")
    public boolean eventMove = true;
    @CfgExclude
    public boolean eventPhysics;
    @CfgComment("Co ile minut ma automatycznie zapisywac dane")
    @CfgName("data-interval")
    public int dataInterval = 1;
    @CfgComment("Typ zapisu danych")
    @CfgComment("Flat - Lokalne pliki")
    @CfgComment("MySQL - baza danych")
    @CfgName("data-type")
    public DataType dataType = new DataType(true, false);
    @CfgComment("Dane wymagane do polaczenia z baza")
    @CfgName("mysql")
    public MySQL mysql = new MySQL("localhost", 3306, "db", "root", "passwd", 16);

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

    public void reload() {
        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);
        this.createMaterial = Parser.parseMaterial(this.createStringMaterial);

        if (this.createMaterial != null && this.createMaterial == Material.DRAGON_EGG) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        } else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        try {
            this.rankSystem = RankSystem.valueOf(this.rankSystem_.toUpperCase());
        } catch (Exception e) {
            this.rankSystem = RankSystem.ELO;
            FunnyGuilds.exception(new IllegalArgumentException("\"" + this.rankSystem_ + "\" is not a valid rank system!"));
        }

        if (this.rankSystem == RankSystem.ELO) {
            EloUtils.parseData(this.eloConstants);
        }

        HashMap<Material, Double> map = new HashMap<>();
        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            Material material = Parser.parseMaterial(entry.getKey());
            if (material == null || material == Material.AIR) {
                continue;
            }
            double chance = entry.getValue();
            if (chance == 0) {
                continue;
            }
            map.put(material, chance);
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

        this.dummySuffix = StringUtils.colored(this.dummySuffix_);

        this.chatGuild = StringUtils.colored(this.chatGuild_);
        this.chatRank = StringUtils.colored(this.chatRank_);
        this.chatPoints = StringUtils.colored(this.chatPoints_);

        this.chatPrivDesign = StringUtils.colored(this.chatPrivDesign_);
        this.chatAllyDesign = StringUtils.colored(this.chatAllyDesign_);
        this.chatGlobalDesign = StringUtils.colored(this.chatGlobalDesign_);

        this.playerlistPoints = StringUtils.colored(this.playerlistPoints_);


    }

    public static class Commands {
        public Command funnyguilds = new Command("funnyguilds", Collections.singletonList("fg"));

        public Command guild = new Command("gildia", Arrays.asList("gildie", "g"));
        public Command create = new Command("zaloz");
        public Command delete = new Command("usun");
        public Command confirm = new Command("potwierdz");
        public Command invite = new Command("zapros");
        public Command join = new Command("dolacz");
        public Command leave = new Command("opusc");
        public Command kick = new Command("wyrzuc");
        public Command base = new Command("baza");
        public Command enlarge = new Command("powieksz");
        public Command ally = new Command("sojusz");
        public Command items = new Command("przedmioty");
        @CfgName("break")
        public Command break_ = new Command("rozwiaz");
        public Command info = new Command("info");
        public Command player = new Command("gracz");
        public Command top = new Command("top", Collections.singletonList("top10"));
        public Command validity = new Command("przedluz");
        public Command leader = new Command("lider", Collections.singletonList("zalozyciel"));
        public Command deputy = new Command("zastepca");
        public Command ranking = new Command("ranking");
        public Command setbase = new Command("ustawbaze", Collections.singletonList("ustawdom"));
        public Command pvp = new Command("pvp", Collections.singletonList("ustawpvp"));
        @CfgComment("Komendy administratora")
        public AdminCommands admin = new AdminCommands();

        public static class Command {
            @CfgStringStyle(CfgStringStyle.StringStyle.ALWAYS_SINGLE_QUOTED)
            public String name;
            @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
            public List<String> aliases;

            public Command() {
            }

            public Command(String name) {
                this(name, Collections.emptyList());
            }

            public Command(String name, List<String> aliases) {
                this.name = name;
                this.aliases = aliases;
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
        }
    }

    public static class DataType {
        public boolean flat;
        public boolean mysql;

        public DataType() {
        }

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
        public int poolSize;

        public MySQL() {
        }

        public MySQL(String hostname, int port, String database, String user, String password, int poolSize) {
            this.hostname = hostname;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
        }
    }
}
