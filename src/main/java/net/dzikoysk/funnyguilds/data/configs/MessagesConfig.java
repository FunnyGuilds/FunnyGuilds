package net.dzikoysk.funnyguilds.data.configs;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;

import java.util.Arrays;
import java.util.List;

public class MessagesConfig {
    @CfgComment("<------- Global Date Format -------> #")
    public String dateFormat = "dd.MM.yyyy HH:mm:ss";
    
    @CfgComment("<------- Permission Messages -------> #")
    public String permission = "&cNie masz wystarczajacych uprawnien do uzycia tej komendy!";
    public String blockedWorld = "&cZarzadzanie gildiami jest zablokowane na tym swiecie!";
    public String playerOnly = "&cKomenda dostepna tylko dla graczy!";

    @CfgComment("<------- Rank Messages -------> #")
    public String rankAddressAttacker = "&7Wykryto ten sam &cadres&7, punkty nie zostaja naliczone!";
    public String rankAddressVictim = "&7Wykryto ten sam &cadres&7, punkty nie zostaja odebrane!";
    public String rankLastVictimV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastVictimA = "&7Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!";
    public String rankLastAttackerV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastAttackerA = "&7Ten gracz byl ostatnio zabity przez Ciebie, punkty nie zostaja dodane!";
    @CfgComment("Available variables: {ATTACKER}, {VICTIM}, {-}, {+}, {POINTS}, {VTAG}, {ATAG}, {WEAPON}")
    public String rankDeathMessage = "{ATAG}&b{ATTACKER} &7(&b+{+}&7) zabil {VTAG}&b{VICTIM} &7(&b-{-}&7) uzywajac &b{WEAPON}";

    @CfgComment("<------- Rank Messages -------> #")
    @CfgComment("Available variables: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    public String banMessage = "&7Zostales zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}";

    @CfgComment("<------- Region Messages -------> #")
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String regionEnter = "&7Wkroczyles na teren gildii &c{TAG}&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String regionLeave = "&7Opusciles teren gildii &c{TAG}&7!";
    public String regionOther = "&cTen teren nalezy do innej gildii!";
    public String regionCenter = "&cNie mozesz zniszczyc srodka swojej gildii!";
    @CfgComment("Available variables: {TIME}")
    public String regionExplode = "&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!";
    @CfgComment("Available variables: {TIME}")
    public String regionExplodeInteract = "&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!";
    public String regionCommand = "&cTej komendy nie mozna uzyc na terenie innej gildii!";

    @CfgComment("<------- Bossbar Region Messages -------> #")
    @CfgComment("Available variables: {PLAYER}")
    public String notificationIntruderEnterGuildRegion = "&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String notificationEnterGuildRegion = "&7Wkroczyles na teren gildii &c{TAG}&7!";

    @CfgComment("<------- Title Region Messages -------> #")
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String notificationTitleEnterOnGuildRegion = "";
    public String notificationSubtitleEnterOnGuildRegion = notificationEnterGuildRegion;
    public String notificationTitleIntruderEnteredOnGuildRegion = "";
    public String notificationSubtitleIntruderEnteredOnGuildRegion = notificationIntruderEnterGuildRegion;
    public String notificationTitleLeavedGuildRegion = "";
    public String notificationSubtitleLeavedGuildRegion = regionLeave;

    @CfgComment("<------- Broadcast Messages -------> #")
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastCreate = "&a{PLAYER} &7zalozyl gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastDelete = "&c{PLAYER} &7rozwiazal gildie &c{TAG}&7!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastJoin = "&a{PLAYER} &7dolaczyl do gildii &a{TAG}&7!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastLeave = "&c{PLAYER} &7opuscil gildie &c{TAG}&7!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastKick = "&c{PLAYER} &7zostal &cwyrzucony &7z gildii &c{TAG}&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}, {REASON}, {TIME}")
    public String broadcastBan = "&7Gildia &c{TAG}&7 zostala zbanowana za &c{REASON}&7, gratulacje!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String broadcastUnban = "&7Gildia &a{TAG}&7 zostala &aodbanowana&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    public String broadcastValidity = "&7Gildia &b{TAG} &7wygasla&b! &7Jej baza znajdowala sie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!";
    @CfgComment("Available variables: {WINNER}, {LOSER}")
    public String broadcastWar = "&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!";

    @CfgComment("<------- Help Messages -------> #")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> helpList = Arrays.asList(
            "&7---------------------&8[ &aGildie &8]&7---------------------",
            "&a/zaloz [tag] [nazwa] &8- &7Tworzy gildie",
            "&a/zapros [gracz] &8- &7Zaprasza gracza do gildii",
            "&a/dolacz [tag] &8- &7Przyjmuje zaproszenie do gildii",
            "&a/info [tag] &8- &7Informacje o danej gildii",
            "&a/baza &8- &7Teleportuje do bazy gildii",
            "&a/powieksz &8- &7Powieksza teren gildii",
            "&a/przedluz &8- &7Przedluza waznosc gildii",
            "&a/lider [gracz] &8- &7Oddaje zalozyciela gildii",
            "&a/zastepca [gracz] &8- &7Nadaje zastepce gildii",
            "&a/sojusz [tag] &8- &7Pozwala nawiazac sojusz",
            "&a/opusc &8- &7Opuszcza gildie",
            "&a/wyrzuc [gracz] &8- &7Wyrzuca gracza z gildii",
            "&a/rozwiaz [tag] &8- &7Rozwiazuje sojusz",
            "&a/usun &8- &7Usuwa gildie",
            "&a/przedmioty &8- &7Pokazuje przedmioty potrzebne do zalozenia gildii",
            "&a/ucieczka &8- &7Rozpoczyna ucieczke z terenu innej gildii");

    @CfgComment("<------- Admin Help Messages -------> #")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> adminHelpList = Arrays.asList(
            "&a/ga dodaj [tag] [nick] &8- &7Dodaje gracza do gildii",
            "&a/ga usun [tag] &8- &7Usuwa gildie",
            "&a/ga wyrzuc [nick] &8- &7Wyrzuca gracza z gildii",
            "&a/ga tp [tag] &8- &7Teleportuje do bazy gildii",
            "&a/ga points [nick] [points] &8- &7Ustawia liczbe punktow gracza",
            "&a/ga kills [nick] [kills] &8- &7Ustawia liczbe zabojstw gracza",
            "&a/ga deaths [nick] [deaths] &8- &7Ustawia liczbe smierci gracza",
            "&a/ga ban [tag] [czas] [powod] &8- &7Banuje gildie na okreslony czas",
            "&a/ga unban [tag] &8- &7Odbanowywuje gildie",
            "&a/ga zycia [tag] [zycia] &8- &7Ustawia liczbe zyc gildii",
            "&a/ga przenies [tag] &8- &7Przenosi teren gildii",
            "&a/ga przedluz [tag] [czas] &8- &7Przedluza waznosc gildii o podany czas",
            "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwe gildii",
            "&a/ga spy &8- &7Szpieguje czat gildii",
            "&a/ga enabled &8- &7Zarzadzanie statusem zakladania gildii",
            "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
            "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastepce gildii");
    
    @CfgComment("<------- Player Info Messages -------> #")
    public String playerInfoExists = "&cTaki gracz nigdy nie byl na serwerze!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {KILLS}, {DEATHS}, {KDR}, {RANK}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> playerInfoList = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&7Zabojstwa: &a{KILLS}",
            "&7Smierci: &a{DEATHS}",
            "&7KDR: &a{KDR}",
            "&8-------------.------------------");

    @CfgComment("<------- Info Messages -------> #")
    public String infoTag = "&cPodaj tag gildii!";
    public String infoExists = "&cGildia o takim tagu nie istnieje!";
    @CfgComment("Available variables: {GUILD}, {TAG}, {OWNER}, {MEMBERS}, ")
    @CfgComment("{POINTS}, {KILLS}, {DEATHS}, {KDR}, {ALLIES}, {RANK}, {VALIDITY},")
    @CfgComment("{LIVES}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> infoList = Arrays.asList(
            "&8-------------------------------",
            "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
            "&7Zalozyciel: &c{OWNER}",
            "&7Punkty: &c{POINTS} &8[&c{RANK}&8]",
            "&7Zycia: &4{LIVES}",
            "&7Waznosc: &c{VALIDITY}",
            "&7Czlonkowie: &7{MEMBERS}",
            "&7Sojusze: &c{ALLIES}",
            "&8-------------------------------");

    @CfgComment("<------- Top Messages -------> #")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> topList = Arrays.asList(
            "&8----------{ &cTOP 10 &8}----------",
            "&71&8. &c{GTOP-1}",
            "&72&8. &c{GTOP-2}",
            "&73&8. &c{GTOP-3}",
            "&74&8. &c{GTOP-4}",
            "&75&8. &c{GTOP-5}",
            "&76&8. &c{GTOP-6}",
            "&77&8. &c{GTOP-7}",
            "&78&8. &c{GTOP-8}",
            "&79&8. &c{GTOP-9}",
            "&710&8. &c{GTOP-10}");

    @CfgComment("<------- Ranking Messages -------> #")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> rankingList = Arrays.asList(
            "&8----------{ &cTOP 10 Graczy &8}----------",
            "&71&8. &c{PTOP-1}",
            "&72&8. &c{PTOP-2}",
            "&73&8. &c{PTOP-3}",
            "&74&8. &c{PTOP-4}",
            "&75&8. &c{PTOP-5}",
            "&76&8. &c{PTOP-6}",
            "&77&8. &c{PTOP-7}",
            "&78&8. &c{PTOP-8}",
            "&79&8. &c{PTOP-9}",
            "&710&8. &c{PTOP-10}");

    @CfgComment("<------- General Messages -------> #")
    public String generalHasGuild = "&cMasz juz gildie!";
    public String generalNoNameGiven = "&cPodaj nazwe gildii!";
    public String generalHasNoGuild = "&cNie masz gildii!";
    public String generalIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String generalNoTagGiven = "&cPodaj tag gildii!";
    public String generalNoNickGiven = "&cPodaj nick gracza!";
    public String generalUserHasGuild = "&cTen gracz ma juz gildie!";
    public String generalNoGuildFound = "&cTaka gildia nie istnieje!";
    public String generalNotPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!";
    @CfgComment("Available variables: {TAG}")
    public String generalGuildNotExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!";
    public String generalIsNotMember = "&cTen gracz nie jest czlonkiem twojej gildii!";
    public String generalPlayerHasNoGuild = "&cTen gracz nie ma gildii!";
    
    @CfgComment("<------- Escape Messages -------> #")
    public String escapeDisabled = "&cPrzykro mi, ucieczki sa wylaczone!";
    @CfgComment("Available variables: {TIME}")
    public String escapeStartedUser = "&aDobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!";
    @CfgComment("Available variables: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    public String escapeStartedOpponents = "&cGracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})";
    public String escapeCancelled = "&cUcieczka zostala przerwana!";
    public String escapeInProgress = "&cUcieczka juz trwa!";
    public String escapeSuccessfulUser = "&aUdalo ci sie uciec!";
    @CfgComment("Available variables: {PLAYER}")
    public String escapeSuccessfulOpponents = "&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!";
    public String escapeNoUserGuild = "&cNie masz gildii do ktorej moglbys uciekac!";
    public String escapeNoNeedToRun = "&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?";
    public String escapeOnYourRegion = "&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?";
    
    @CfgComment("<------- Create Guild Messages -------> #")
    @CfgComment("Available variables: {LENGTH}")
    public String createTagLength = "&7Tag nie moze byc dluzszy niz &c{LENGTH} litery&7!";
    @CfgComment("Available variables: {LENGTH}")
    public String createNameLength = "&cNazwa nie moze byc dluzsza niz &c{LENGTH} litery&7!";
    @CfgComment("Available variables: {LENGTH}")
    public String createTagMinLength = "&7Tag nie moze byc krotszy niz &c{LENGTH} litery&7!";
    @CfgComment("Available variables: {LENGTH}")
    public String createNameMinLength = "&cNazwa nie moze byc krotsza niz &c{LENGTH} litery&7!";
    public String createOLTag = "&cTag gildii moze zawierac tylko litery!";
    public String createOLName = "&cNazwa gildii moze zawierac tylko litery!";
    public String createMore = "&cNazwa gildi nie moze zawierac spacji!";
    public String createNameExists = "&cJest juz gildia z taka nazwa!";
    public String createTagExists = "&cJest juz gildia z takim tagiem!";
    public String restrictedGuildName = "&cPodana nazwa gildii jest niedozwolona.";
    public String restrictedGuildTag = "&cPodany tag gildii jest niedozwolony.";
    @CfgComment("Available variables: {DISTANCE}")
    public String createSpawn = "&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}";
    public String createIsNear = "&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!";
    @CfgComment("Available variables: {POINTS}, {REQUIRED}")
    public String createRank = "&cAby zalozyc gildie, wymagane jest przynajmniej &7{REQUIRED}&cpunktow.";
    @CfgComment("Available variables: {ITEM}, {ITEMS}")
    public String createItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String createGuild = "&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";

    @CfgComment("<------- Delete Guild Messages -------> #")
    public String deleteConfirm = "&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz";
    public String deleteToConfirm = "&cNie masz zadnych dzialan do potwierdzenia!";
    public String deleteSomeoneIsNear = "&cNie mozesz usunac gildii, ktos jest w poblizu!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String deleteSuccessful = "&7Pomyslnie &cusunieto &7gildie!";

    @CfgComment("<------- Invite Messages -------> #")
    public String invitePlayerExists = "&cNie ma takiego gracza na serwerze!";
    @CfgComment("Available variables: {AMOUNT}")
    public String inviteAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT})";
    public String inviteCancelled = "&cCofnieto zaproszenie!";
    @CfgComment("Available variables: {OWNER}, {GUILD}, {TAG}")
    public String inviteCancelledToInvited = "&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!";
    @CfgComment("Available variables: {PLAYER}")
    public String inviteToOwner = "&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!";
    @CfgComment("Available variables: {OWNER}, {GUILD}, {TAG}")
    public String inviteToInvited = "&aOtrzymano zaproszenie do gildii &7{TAG}&a!";

    @CfgComment("<------- Join Messages -------> #")
    public String joinHasNotInvitation = "&cNie masz zaproszenia do gildii!";
    public String joinHasNotInvitationTo = "&cNie otrzymales zaproszenia do tej gildii!";
    public String joinHasGuild = "&cMasz juz gildie!";
    public String joinTagExists = "&cNie ma gildii o takim tagu!";
    @CfgComment("Available variables: {GUILDS}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> joinInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Wpisz &a/dolacz [tag] &7aby dolaczyc do wybranej gildii");

    @CfgComment("Available variables: {ITEM}, {ITEMS}")
    public String joinItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String joinToMember = "&aDolaczyles do gildii &7{GUILD}";
    @CfgComment("Available variables: {PLAYER}")
    public String joinToOwner = "&a{PLAYER} &7dolaczyl do &aTwojej &7gildii!";

    @CfgComment("<------- Leave Messages -------> #")
    public String leaveIsOwner = "&cZalozyciel &7nie moze opuscic gildii!";
    @CfgComment("Available variables: {GUILDS}, {TAG}")
    public String leaveToUser = "&7Opusciles gildie &a{GUILD}&7!";

    @CfgComment("<------- Kick Messages -------> #")
    public String kickOtherGuild = "&cTen gracz nie jest w Twojej gildii!";
    public String kickOwner = "&cNie mozna wyrzucic zalozyciela!";
    @CfgComment("Available variables: {PLAYER}")
    public String kickToOwner = "&c{PLAYER} &7zostal wyrzucony z gildii!";
    @CfgComment("Available variables: {GUILD}")
    public String kickToPlayer = "&cZostales wyrzucony z gildii!";

    @CfgComment("<------- Enlarge Messages -------> #")
    public String enlargeMaxSize = "&cOsiagnieto juz maksymalny rozmiar terenu!";
    public String enlargeIsNear = "&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!";
    @CfgComment("Available variables: {ITEM}")
    public String enlargeItem = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @CfgComment("Available variables: {SIZE}, {LEVEL}")
    public String enlargeDone = "&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)";

    @CfgComment("<------- Base Messages -------> #")
    public String baseTeleportationDisabled = "&cTeleportacja do baz gildyjnych nie jest dostepna";
    public String baseHasNotRegion = "&cTwoja gildia nie posiada terenu!";
    public String baseHasNotCenter = "&cTwoja gildia nie posiada srodka regionu!";
    public String baseIsTeleportation = "&cWlasnie sie teleportujesz!";
    @CfgComment("Available variables: {ITEM}, {ITEMS}")
    public String baseItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    public String baseDontMove = "&7Nie ruszaj sie przez &c{TIME} &7sekund!";
    public String baseMove = "&cRuszyles sie, teleportacja przerwana!";
    public String baseTeleport = "&aTeleportacja&7...";

    @CfgComment("<------- Ally Messages -------> #")
    public String allyHasNotInvitation = "&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]";
    @CfgComment("Available variables: {GUILDS}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> allyInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Aby zaakceptowac uzyj &a/sojusz [tag]");
    @CfgComment("Available variables: {TAG}")
    public String allyAlly = "&cMasz juz sojusz z ta gildia!";
    public String allySame = "&cNie mozesz nawiazac sojuszu z wlasna gildia!";
    @CfgComment("Available variables: {GUILD}")
    public String allyDone = "&7Nawiazano sojusz z gildia &a{GUILD}&7!";
    @CfgComment("Available variables: {GUILD}")
    public String allyIDone = "&7Gildia &a{GUILD} &7przystapila do sojuszu!";
    @CfgComment("Available variables: {GUILD}")
    public String allyReturn = "&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!";
    @CfgComment("Available variables: {GUILD}")
    public String allyIReturn = "&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!";
    @CfgComment("Available variables: {GUILD}")
    public String allyInviteDone = "&7Zaproszono gildie &a{GUILD} &7do sojuszu!";
    @CfgComment("Available variables: {GUILD}")
    public String allyToInvited = "&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!";

    @CfgComment("<------- Break Messages -------> #")
    public String breakHasNotAllies = "&cTwoja gildia nie posiada sojuszy!";
    @CfgComment("Available variables: {GUILDS}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> breakAlliesList = Arrays.asList(
            "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
            "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]");
    
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakAllyExists = "&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7&c{GUILD}&7)!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakDone = "&7Rozwiazano sojusz z gildia &c{GUILD}&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakIDone = "&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!";

    @CfgComment("<------- Validity Messages -------> #")
    @CfgComment("Available variables: {TIME}")
    public String validityWhen = "&7Gildie mozesz przedluzyc dopiero za &c{TIME}&7!";
    @CfgComment("Available variables: {ITEM}")
    public String validityItems = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @CfgComment("Available variables: {DATE}")
    public String validityDone = "&7Waznosc gildii przedluzona do &a{DATE}&7!";

    @CfgComment("<------- Validity Messages -------> #")
    public String warHasNotGuild = "&cMusisz miec gildie, aby zaatkowac inna!";
    public String warAlly = "&cNie mozesz zaatakowac sojusznika!";
    @CfgComment("Available variables: {TIME}")
    public String warWait = "&7Atak na gildie mozliwy za &4{TIME}";
    @CfgComment("Available variables: {ATTACKED}")
    public String warAttacker = "&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!";
    @CfgComment("Available variables: {ATTACKER}")
    public String warAttacked = "&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!";
    @CfgComment("Available variables: {LOSER}")
    public String warWin = "&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!";
    @CfgComment("Available variables: {WINNER}")
    public String warLose = "&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!";

    @CfgComment("<------- Leader Messages -------> #")
    public String leaderMustBeDifferent = "&cNie mozesz sobie oddac zalozyciela!";
    public String leaderSet = "&7Ustanowiono nowego &alidera &7gildii!";
    public String leaderOwner = "&7Zostales nowym &aliderem &7gildii!";
    @CfgComment("Available variables: {PLAYER}")
    public String leaderMembers = "&7{PLAYER} zostal nowym &aliderem &7gildii!";

    @CfgComment("<------- Deputy Messages -------> #")
    public String deputyMustBeDifferent = "&cNie mozesz mianowac siebie zastepca!";
    public String deputyRemove = "&7Zdegradowno gracza z funkcji &czastepcy&7!";
    public String deputyMember = "&7Zdegradowano Cie z funkcji &czastepcy&7!";
    public String deputySet = "&7Ustanowiono nowego &azastepce &7gildii!";
    public String deputyOwner = "&7Zostales nowym &azastepca &7gildii!";
    @CfgComment("Available variables: {PLAYER}")
    public String deputyMembers = "&7{PLAYER} zostal nowym &azastepca &7gildii!";
    @CfgComment("Available variables: {PLAYER}")
    public String deputyNoLongerMembers = "&7{PLAYER} juz nie jest &azastepca &7gildii!";

    @CfgComment("<------- Setbase Messages -------> #")
    public String setbaseOutside = "&cNie mozna ustawic domu gildii poza jej terenem!";
    public String setbaseDone = "&7Przeniesiono &adom &7gildii!";

    @CfgComment("<------- PvP Messages -------> #")
    public String pvpOn = "&cWlaczono &7pvp w gildii!";
    public String pvpOff = "&aWylaczono &7pvp w gildii!";
    
    @CfgComment("<------- Admin Messages -------> #")
    @CfgComment("Available variables: {ADMIN}")
    public String adminGuildBroken = "&cTwoja gildia zostala rozwiazana przez &7{ADMIN}";
    public String adminGuildOwner = "&cTen gracz jest zalozycielem gildii, nie mozna go wyrzucic!";
    public String adminNoRegionFound = "&cGildia nie posiada terenu!";
    
    public String adminNoPointsGiven = "&cPodaj liczbe punktow!";
    @CfgComment("Available variables: {ERROR}")
    public String adminErrorInNumber = "&cNieznana jest liczba: {ERROR}";
    @CfgComment("Available variables: {PLAYER}, {POINTS}")
    public String adminPointsChanged = "&aUstawiono &7{POINTS} &apunktow dla gracza &7{PLAYER}";
    
    public String adminNoKillsGiven = "&cPodaj liczbe zabojstw!";
    @CfgComment("Available variables: {PLAYER}, {KILLS}")
    public String adminKillsChanged = "&aUstawiono &7{KILLS} &azabojstw dla gracza &7{PLAYER}";
    
    public String adminNoDeathsGiven = "&cPodaj liczbe zgonow!";
    @CfgComment("Available variables: {PLAYER}, {DEATHS}")
    public String adminDeathsChanged = "&aUstawiono &7{DEATHS} &azgonow dla gracza &7{PLAYER}";
    
    public String adminNoBanTimeGiven = "&cPodaj czas na jaki ma byc zbanowana gildia!";
    public String adminNoReasonGiven = "&cPodaj powod!";
    public String adminGuildBanned = "&cTa gildia jest juz zbanowana!";
    public String adminTimeError = "&cPodano nieprawidlowy czas!";
    @CfgComment("Available variables: {GUILD}, {TIME}")
    public String adminGuildBan = "&aZbanowano gildie &a{GUILD} &7na okres &a{TIME}&7!";
    
    public String adminGuildNotBanned = "&cTa gildia nie jest zbanowana!";
    @CfgComment("Available variables: {GUILD}")
    public String adminGuildUnban = "&aOdbanowano gildie &7{GUILD}&a!";
    
    public String adminNoLivesGiven = "&cPodaj liczbe zyc!";
    @CfgComment("Available variables: {GUILD}, {LIVES}")
    public String adminLivesChanged = "&aUstawiono &7{LIVES} &azyc dla gildii &7{GUILD}&a!";
    
    @CfgComment("Available variables: {GUILD}")
    public String adminGuildRelocated = "&aPrzeniesiono teren gildii &7{GUILD}&a!";
    
    public String adminNoValidityTimeGiven = "&cPodaj czas o jaki ma byc przedluzona waznosc gildii!";
    @CfgComment("Available variables: {GUILD}, {VALIDITY}")
    public String adminNewValidity = "&aPrzedluzono waznosc gildii &a{GUILD} &7do &a{VALIDITY}&7!";
    
    public String adminNoNewNameGiven = "&cPodaj nowa nazwe!";
    @CfgComment("Available variables: {GUILD}")
    public String adminNameChanged = "&aZmieniono nazwe gildii na &7{GUILD}&a!";
    
    public String adminStopSpy = "&cJuz nie szpiegujesz graczy!";
    public String adminStartSpy = "&aOd teraz szpiegujesz graczy!";

    public String adminGuildsEnabled = "&aZakladanie gildii jest wlaczone!";
    public String adminGuildsDisabled = "&cZakladanie gildii jest wylaczone!";
    
    public String adminUserNotMemberOf = "&cTen gracz nie jest czlonkiem tej gildii!";
    public String adminAlreadyLeader = "&cTen gracz jest juz liderem gildii!";
    public String adminAlreadyDeputy = "&cTen gracz jest juz zastepca gildii!";
}
