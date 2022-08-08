package net.dzikoysk.funnyguilds.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.exception.OkaeriException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;

public class MessageConfiguration extends OkaeriConfig {

    @Comment("<------- Global Date Format -------> #")
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //TODO: change to new datetime API

    @Comment("")
    @Comment("<------- No Value Messages -------> #")
    public String gNameNoValue = "Brak (G-NAME/NAME)";
    public String gTagNoValue = "Brak (G-TAG/TAG)";
    public String gOwnerNoValue = "Brak (G-OWNER)";
    public String gDeputiesNoValue = "Brak (G-DEPUTIES)";
    public String gDeputyNoValue = "Brak (G-DEPUTY)";
    public String gValidityNoValue = "Brak (G-VALIDITY)";
    public String gProtectionNoValue = "Brak (G-PROTECTION)";
    public String gRegionSizeNoValue = "Brak (G-REGION-SIZE)";
    public String livesNoValue = "Brak (LIVES-SYMBOL/LIVES-SYMBOL-ALL)";
    public String alliesNoValue = "Brak (ALLIES)";
    public String enemiesNoValue = "Brak (ENEMIES)";
    public String gtopNoValue = "Brak (GTOP-x)";
    public String ptopNoValue = "Brak (PTOP-x)";
    public String wgRegionNoValue = "Brak (WG-REGION)";
    public String minMembersToIncludeNoValue = "Brak (guild-min-members w config.yml)";

    @Comment("")
    @Comment("<------- Permission Messages -------> #")
    public String permission = "&cNie masz wystarczajacych uprawnien do uzycia tej komendy!";
    public String blockedWorld = "&cZarzadzanie gildiami jest zablokowane na tym swiecie!";
    public String playerOnly = "&cKomenda dostepna tylko dla graczy!";

    @Comment("")
    @Comment("<------- Rank Messages -------> #")
    public String rankLastVictimV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastVictimA = "&7Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!";
    public String rankLastAttackerV = "&7Ostatnio zabiles tego samego gracza, punkty nie zostaja odebrane!";
    public String rankLastAttackerA = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja dodane!";
    public String rankIPVictim = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja odjete!";
    public String rankIPAttacker = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja dodane!";
    public String rankMemberVictim = "&7Ten gracz jest czlonkiem twojej gildii, punkty nie zostaja odebrane!";
    public String rankMemberAttacker = "&7Ten gracz jest czlonkiem twojej gildii, punkty nie zostaja dodane!";
    public String rankAllyVictim = "&7Ten gracz jest czlonkiem sojuszniczej gildii, punkty nie zostaja odebrane!";
    public String rankAllyAttacker = "&7Ten gracz jest czlonkiem sojuszniczej gildii, punkty nie zostaja dodane!";
    @Comment("Dostępne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {MINUS-FORMATTED}, {PLUS-FORMATTED}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {WEAPON-NAME}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
    public String rankDeathMessage = "{ATAG}&b{ATTACKER} &7({PLUS-FORMATTED}&7) zabil {VTAG}&b{VICTIM} &7({MINUS-FORMATTED}&7) uzywajac &b{WEAPON} {WEAPON-NAME}";
    public String rankKillTitle = "&cZabiles gracza {VICTIM}";
    public String rankKillSubtitle = "&7+{+}";
    @Comment("Zamiast zmiennej {ASSISTS} wstawiane są kolejne wpisy o asystujących graczach")
    public String rankAssistMessage = "&7Asystowali: {ASSISTS}";
    @Comment("Dostępne zmienne: {PLAYER}, {+}, {PLUS-FORMATTED}, {SHARE}")
    public String rankAssistEntry = "&b{PLAYER} &7({PLUS-FORMATTED}&7, {SHARE}% dmg)";
    @Comment("Znaki oddzielające kolejne wpisy o asystujących graczach")
    public String rankAssistDelimiter = "&8, ";
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public String rankResetItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @Comment("Dostępne zmienne: {LAST-RANK}, {CURRENT-RANK}")
    public String rankResetMessage = "&7Zresetowales swoj ranking z poziomu &c{LAST-RANK} &7do poziomu &c{CURRENT-RANK}&7.";

    @Comment("")
    @Comment("<------- Ban Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    public String banMessage = "&7Zostales zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}";

    @Comment("")
    @Comment("<------- Region Messages -------> #")
    public String regionOther = "&cTen teren nalezy do innej gildii!";
    public String regionCenter = "&cNie mozesz zniszczyc srodka swojej gildii!";
    public String regionInteract = "&cNie mozesz ingerowac w okolice serca swojej gildii!";
    @Comment("Dostępne zmienne: {TIME}")
    public String regionExplode = "&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!";
    @Comment("Dostępne zmienne: {TIME}")
    public String regionExplodeInteract = "&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!";
    public String regionCommand = "&cTej komendy nie mozna uzyc na terenie innej gildii!";
    public String regionTeleport = "&cNie mozesz teleportowac sie na teren innej gildii!";
    public String regionExplosionHasProtection = "&cEksplozja nie spowodowala zniszczen na terenie gildii, poniewaz jest ona chroniona!";
    public String regionsDisabled = "&cRegiony gildii sa wylaczone!";

    @Comment("")
    @Comment("<------- ActionBar Region Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}")
    public String notificationActionbarIntruderEnterGuildRegion = "&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationActionbarEnterGuildRegion = "&7Wkroczyles na teren gildii &c{TAG}&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationActionbarLeaveGuildRegion = "&7Opusciles teren gildii &c{TAG}&7!";

    @Comment("")
    @Comment("<------- Bossbar Region Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}")
    public String notificationBossbarIntruderEnterGuildRegion = this.notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationBossbarEnterGuildRegion = this.notificationActionbarEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationBossbarLeaveGuildRegion = this.notificationActionbarLeaveGuildRegion;

    @Comment("")
    @Comment("<------- Chat Region Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}")
    public String notificationChatIntruderEnterGuildRegion = this.notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationChatEnterGuildRegion = this.notificationActionbarEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationChatLeaveGuildRegion = this.notificationActionbarLeaveGuildRegion;

    @Comment("")
    @Comment("<------- Title Region Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}")
    public String notificationTitleIntruderEnterGuildRegion = this.notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostępne zmienne: {PLAYER}")
    public String notificationSubtitleIntruderEnterGuildRegion = this.notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationTitleEnterGuildRegion = this.notificationActionbarEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationSubtitleEnterGuildRegion = this.notificationActionbarEnterGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationTitleLeaveGuildRegion = this.notificationActionbarLeaveGuildRegion;
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String notificationSubtitleLeaveGuildRegion = this.notificationActionbarLeaveGuildRegion;

    @Comment("")
    @Comment("<------- Broadcast Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastCreate = "&a{PLAYER} &7zalozyl gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastDelete = "&c{PLAYER} &7rozwiazal gildie &c{TAG}&7!";
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastJoin = "&a{PLAYER} &7dolaczyl do gildii &a{TAG}&7!";
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastLeave = "&c{PLAYER} &7opuscil gildie &c{TAG}&7!";
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String broadcastKick = "&c{PLAYER} &7zostal &cwyrzucony &7z gildii &c{TAG}&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {REASON}, {TIME}")
    public String broadcastBan = "&7Gildia &c{TAG}&7 zostala zbanowana za &c{REASON}&7, gratulacje!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String broadcastUnban = "&7Gildia &a{TAG}&7 zostala &aodbanowana&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    public String broadcastValidity = "&7Gildia &b{TAG} &7wygasla&b! &7Jej baza znajdowala sie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!";
    @Comment("Dostępne zmienne: {WINNER}, {LOSER}")
    public String broadcastWar = "&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!";
    public String noInformation = "Brak informacji";

    @Comment("")
    @Comment("<------- Help Messages -------> #")
    public List<String> funnyguildsHelp = Arrays.asList(
            "&aFunnyGuilds Help:",
            "&7/funnyguilds (reload|rl) - przeladuj plugin",
            "&7/funnyguilds (update|check) - sprawdz dostepnosc aktualizacji",
            "&7/funnyguilds save-all - zapisz wszystko",
            "&7/funnyguilds funnybin - zapisz konfigurację online (~ usprawnia pomoc na https://github.com/FunnyGuilds/FunnyGuilds/issues)"
    );

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
            "&a/ucieczka &8- &7Rozpoczyna ucieczke z terenu innej gildii"
    );

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
            "&a/ga ochrona [tag] [czas] &8- &7Ustawia date wygasniecia ochrony",
            "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwe gildii",
            "&a/ga tag [tag] [nowy tag] &8- &7Zmienia tag gildii",
            "&a/ga spy &8- &7Szpieguje czat gildii",
            "&a/ga enabled &8- &7Zarzadzanie statusem zakladania gildii",
            "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
            "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastepce gildii",
            "&a/ga baza [gracz] &8- &7Teleportuje gracza do bazy jego gildii"
    );

    @Comment("")
    @Comment("<------- Info Messages -------> #")
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<String> playerInfoList = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&7Zabojstwa: &a{KILLS}",
            "&7Smierci: &a{DEATHS}",
            "&7Asysty: &a{ASSISTS}",
            "&7Logouty: &a{LOGOUTS}",
            "&7KDR: &a{KDR}",
            "&8-------------.------------------"
    );

    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<String> playerRightClickInfo = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&8-------------.------------------"
    );

    public String infoTag = "&cPodaj tag gildii!";
    public String infoExists = "&cGildia o takim tagu nie istnieje!";

    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {LIVES-SYMBOL}, {LIVES-SYMBOL-ALL}, {GUILD-PROTECTION}")
    public List<String> infoList = Arrays.asList(
            "&8-------------------------------",
            "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
            "&7Zalozyciel: &c{OWNER}",
            "&7Zastepcy: &c{DEPUTIES}",
            "&7Punkty: &c{POINTS} &8[&c{RANK}&8]",
            "&7Ochrona: &c{GUILD-PROTECTION}",
            "&7Zycia: &4{LIVES}",
            "&7Waznosc: &c{VALIDITY}",
            "&7Czlonkowie: &7{MEMBERS}",
            "&7Sojusze: &c{ALLIES}",
            "&7Wojny: &c{ENEMIES}",
            "&8-------------------------------"
    );

    @Comment("")
    @Comment("<------- Top Messages -------> #")
    @Comment("{GTOP-<pozycja>} - gildia na podanej pozycji w rankingu")
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
            "&710&8. &c{GTOP-10}"
    );

    @Comment("")
    @Comment("<------- Ranking Messages -------> #")
    @Comment("{PTOP-<pozycja>} - gracz na podanej pozycji w rankingu")
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
            "&710&8. &c{PTOP-10}"
    );

    @Comment("")
    @Comment("<------- General Messages -------> #")
    public String generalHasGuild = "&cMasz juz gildie!";
    public String generalNoNameGiven = "&cPodaj nazwe gildii!";
    public String generalHasNoGuild = "&cNie masz gildii!";
    public String generalIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String generalNoTagGiven = "&cPodaj tag gildii!";
    public String generalNoNickGiven = "&cPodaj nick gracza!";
    public String generalUserHasGuild = "&cTen gracz ma juz gildie!";
    public String generalNoGuildFound = "&cTaka gildia nie istnieje!";
    public String generalNotPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!";
    public String generalNotOnline = "&cTen gracz nie jest obecnie na serwerze!";

    @Comment("Dostępne zmienne: {TAG}")
    public String generalGuildNotExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!";
    public String generalIsNotMember = "&cTen gracz nie jest czlonkiem twojej gildii!";
    public String generalPlayerHasNoGuild = "&cTen gracz nie ma gildii!";
    public String generalCommandDisabled = "&cTa komenda jest wylaczona!";
    public String generalAllyPvpDisabled = "&cPVP pomiedzy sojuszami jest wylaczone w konfiguracji!";

    @Comment("")
    @Comment("<------- Escape Messages -------> #")
    public String escapeDisabled = "&cPrzykro mi, ucieczki sa wylaczone!";
    @Comment("Dostępne zmienne: {TIME}")
    public String escapeStartedUser = "&aDobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!";
    @Comment("Dostępne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    public String escapeStartedOpponents = "&cGracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})";
    public String escapeCancelled = "&cUcieczka zostala przerwana!";
    public String escapeInProgress = "&cUcieczka juz trwa!";
    public String escapeSuccessfulUser = "&aUdalo ci sie uciec!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String escapeSuccessfulOpponents = "&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!";
    public String escapeNoUserGuild = "&cNie masz gildii do ktorej moglbys uciekac!";
    public String escapeNoNeedToRun = "&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?";
    public String escapeOnYourRegion = "&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?";

    @Comment("")
    @Comment("<------- Create Guild Messages -------> #")
    @Comment("Dostępne zmienne: {LENGTH}")
    public String createTagLength = "&7Tag nie moze byc dluzszy niz &c{LENGTH} litery&7!";
    @Comment("Dostępne zmienne: {LENGTH}")
    public String createNameLength = "&cNazwa nie moze byc dluzsza niz &c{LENGTH} litery&7!";
    @Comment("Dostępne zmienne: {LENGTH}")
    public String createTagMinLength = "&7Tag nie moze byc krotszy niz &c{LENGTH} litery&7!";
    @Comment("Dostępne zmienne: {LENGTH}")
    public String createNameMinLength = "&cNazwa nie moze byc krotsza niz &c{LENGTH} litery&7!";
    public String createOLTag = "&cTag gildii moze zawierac tylko litery!";
    public String createOLName = "&cNazwa gildii moze zawierac tylko litery!";
    public String createMore = "&cNazwa gildi nie moze zawierac spacji!";
    public String createNameExists = "&cJest juz gildia z taka nazwa!";
    public String createTagExists = "&cJest juz gildia z takim tagiem!";
    public String restrictedGuildName = "&cPodana nazwa gildii jest niedozwolona.";
    public String restrictedGuildTag = "&cPodany tag gildii jest niedozwolony.";
    public String invalidGuildLocation = "&cNie mozesz stworzyc gildii w tym miejscu!";
    @Comment("Dostępne zmienne: {DISTANCE}")
    public String createSpawn = "&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}";
    public String createIsNear = "&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!";
    @Comment("Dostępne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
    public String createRank = "&cAby zalozyc gildie, wymagane jest przynajmniej &7{REQUIRED} &cpunktow.";
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public String createItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}&c. Najedz na przedmiot, aby dowiedziec sie wiecej";
    @Comment("Dostępne zmienne: {EXP}")
    public String createExperience = "&cNie posiadasz wymaganego doswiadczenia do zalozenia gildii: &7{EXP}";
    @Comment("Dostępne zmienne: {MONEY}")
    public String createMoney = "&cNie posiadasz wymaganej ilosci pieniedzy do zalozenia gildii: &7{MONEY}";
    public String withdrawError = "&cNie udalo sie pobrac pieniedzy z twojego konta z powodu: &7{ERROR}";
    @Comment("Dostępne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public String createGuild = "&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";
    public String createGuildCouldNotPasteSchematic = "&cWystapil blad podczas tworzenia terenu gildii, zglos sie do administracji.";
    @Comment("Dostępne zmienne: {BORDER-MIN-DISTANCE}")
    public String createNotEnoughDistanceFromBorder = "&cJestes zbyt blisko granicy mapy aby zalozyc gildie! (Minimalna odleglosc: {BORDER-MIN-DISTANCE})";

    @Comment("")
    @Comment("<------- Delete Guild Messages -------> #")
    public String deleteConfirm = "&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz";
    public String deleteToConfirm = "&cNie masz zadnych dzialan do potwierdzenia!";
    public String deleteSomeoneIsNear = "&cNie mozesz usunac gildii, ktos jest w poblizu!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String deleteSuccessful = "&7Pomyslnie &cusunieto &7gildie!";

    @Comment("")
    @Comment("<------- Invite Messages -------> #")
    @Comment("Dostępne zmienne: {AMOUNT}")
    public String inviteAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT}&7)";
    public String inviteAmountJoin = "&7Ta gildia osiagnela juz &cmaksymalna &7liczbe czlonkow! (&c{AMOUNT}&7)";
    public String inviteAllyAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe sojuszy miedzygildyjnych! (&c{AMOUNT}&7)";
    @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public String inviteAllyTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe sojuszy! (&c{AMOUNT}&7)";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String inviteCancelled = "&cCofnieto zaproszenie dla {PLAYER}!";
    @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
    public String inviteCancelledToInvited = "&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String inviteToOwner = "&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!";
    @Comment("Dostępne zmienne: {OWNER}, {GUILD}, {TAG}")
    public String inviteToInvited = "&aOtrzymano zaproszenie do gildii &7{TAG}&a!";

    public String inviteNoOneIsNearby = "&cNikogo nie ma w pobliżu!";

    @Comment("Dostępne zmienne: {RANGE}")
    public String inviteAllCommand = "&aZapraszam wszystkich w promieniu {RANGE} bloków!";

    @Comment("Dostępne zmienne: {MAX_RANGE}")
    public String inviteRangeToBig = "&cPodany zasięg jest zbyt duży! (Maksymalnie: {MAX_RANGE})";

    @Comment("Dostępne zmienne: {MAX_RANGE}")
    public String inviteAllArgumentIsNotNumber = "&cPodany zasięg nie jest poprawną liczbą!";

    @Comment("")
    @Comment("<------- Join Messages -------> #")
    public String joinHasNotInvitation = "&cNie masz zaproszenia do gildii!";
    public String joinHasNotInvitationTo = "&cNie otrzymales zaproszenia do tej gildii!";
    public String joinHasGuild = "&cMasz juz gildie!";
    public String joinTagExists = "&cNie ma gildii o takim tagu!";
    @Comment("Dostępne zmienne: {GUILDS}")
    public List<String> joinInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Wpisz &a/dolacz [tag] &7aby dolaczyc do wybranej gildii"
    );

    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public String joinItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String joinToMember = "&aDolaczyles do gildii &7{GUILD}";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String joinToOwner = "&a{PLAYER} &7dolaczyl do &aTwojej &7gildii!";

    @Comment("")
    @Comment("<------- Leave Messages -------> #")
    public String leaveIsOwner = "&cZalozyciel &7nie moze opuscic gildii!";
    @Comment("Dostępne zmienne: {GUILDS}, {TAG}")
    public String leaveToUser = "&7Opusciles gildie &a{GUILD}&7!";

    @Comment("")
    @Comment("<------- Kick Messages -------> #")
    public String kickOtherGuild = "&cTen gracz nie jest w Twojej gildii!";
    public String kickOwner = "&cNie mozna wyrzucic zalozyciela!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}, {PLAYER}")
    public String kickToOwner = "&c{PLAYER} &7zostal wyrzucony z gildii!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String kickToPlayer = "&cZostales wyrzucony z gildii!";

    @Comment("")
    @Comment("<------- Enlarge Messages -------> #")
    public String enlargeMaxSize = "&cOsiagnieto juz maksymalny rozmiar terenu!";
    public String enlargeIsNear = "&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!";
    @Comment("Dostępne zmienne: {ITEM}")
    public String enlargeItem = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @Comment("Dostępne zmienne: {SIZE}, {LEVEL}")
    public String enlargeDone = "&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)";

    @Comment("")
    @Comment("<------- Base Messages -------> #")
    public String baseTeleportationDisabled = "&cTeleportacja do baz gildyjnych nie jest dostepna";
    public String baseHasNotRegion = "&cTwoja gildia nie posiada terenu!";
    public String baseHasNotCenter = "&cTwoja gildia nie posiada srodka regionu!";
    public String baseIsTeleportation = "&cWlasnie sie teleportujesz!";
    @Comment("Dostępne zmienne: {ITEM}, {ITEMS}")
    public String baseItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    public String baseDontMove = "&7Nie ruszaj sie przez &c{TIME} &7sekund!";
    public String baseMove = "&cRuszyles sie, teleportacja przerwana!";
    public String baseTeleport = "&aTeleportacja&7...";

    @Comment("")
    @Comment("<------- Enemy Messages -------> #")
    public String enemyCorrectUse = "&7Aby rozpoczac wojne z gildia wpisz &c/wojna [tag]";
    public String enemySame = "&cNie mozesz rozpoczac wojny z wlasna gildia!";
    public String enemyAlly = "&cNie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!";
    public String enemyAlready = "&cProwadzisz juz wojne z ta gildia!";
    @Comment("Dostępne zmienne: {AMOUNT}")
    public String enemyMaxAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe wojen miedzygildyjnych! (&c{AMOUNT}&7)";
    @Comment("Dostępne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public String enemyMaxTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe wojen! (&c{AMOUNT}&7)";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String enemyDone = "&7Wypowiedziano gildii &a{GUILD}&7 wojne!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String enemyIDone = "&7Gildia &a{GUILD} &7wypowiedziala twojej gildii wojne!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String enemyEnd = "&7Zakonczono wojne z gildia &a{GUILD}&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String enemyIEnd = "&7Gildia &a{GUILD} &7zakonczyla wojne z twoja gildia!";

    @Comment("")
    @Comment("<------- Ally Messages -------> #")
    public String allyHasNotInvitation = "&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]";
    @Comment("Dostępne zmienne: {GUILDS}")
    public List<String> allyInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Aby zaakceptowac uzyj &a/sojusz [tag]"
    );

    @Comment("Dostępne zmienne: {TAG}")
    public String allyAlly = "&cMasz juz sojusz z ta gildia!";
    public String allyDoesntExist = "&cNie posiadasz sojuszu z ta gildia!";
    public String allySame = "&cNie mozesz nawiazac sojuszu z wlasna gildia!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyDone = "&7Nawiazano sojusz z gildia &a{GUILD}&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyIDone = "&7Gildia &a{GUILD} &7przystapila do sojuszu!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyReturn = "&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyIReturn = "&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyInviteDone = "&7Zaproszono gildie &a{GUILD} &7do sojuszu!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String allyToInvited = "&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!";

    @Comment("")
    @Comment("<------- Break Messages -------> #")
    public String breakHasNotAllies = "&cTwoja gildia nie posiada sojuszy!";
    @Comment("Dostępne zmienne: {GUILDS}")
    public List<String> breakAlliesList = Arrays.asList(
            "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
            "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]"
    );

    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String breakAllyExists = "&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7, &c{GUILD}&7)!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String breakDone = "&7Rozwiazano sojusz z gildia &c{GUILD}&7!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String breakIDone = "&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!";

    @Comment("")
    @Comment("<------- Validity Messages -------> #")
    @Comment("Dostępne zmienne: {TIME}")
    public String validityWhen = "&7Gildie mozesz przedluzyc dopiero za &c{TIME}&7!";
    @Comment("Dostępne zmienne: {ITEM}")
    public String validityItems = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @Comment("Dostępne zmienne: {DATE}")
    public String validityDone = "&7Waznosc gildii przedluzona do &a{DATE}&7!";

    @Comment("")
    @Comment("<------- War Messages -------> #")
    public String warDisabled = "&cPodbijanie gildii jest wyłączone.";
    public String warHasNotGuild = "&cMusisz miec gildie, aby zaatkowac inna!";
    public String warAlly = "&cNie mozesz zaatakowac sojusznika!";
    @Comment("Dostępne zmienne: {TIME}")
    public String warWait = "&7Atak na gildie mozliwy za &4{TIME}";
    @Comment("Dostępne zmienne: {ATTACKED}")
    public String warAttacker = "&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!";
    @Comment("Dostępne zmienne: {ATTACKER}")
    public String warAttacked = "&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!";
    @Comment("Dostępne zmienne: {LOSER}")
    public String warWin = "&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!";
    @Comment("Dostępne zmienne: {WINNER}")
    public String warLose = "&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!";

    @Comment("")
    @Comment("<------- Leader Messages -------> #")
    public String leaderMustBeDifferent = "&cNie mozesz sobie oddac zalozyciela!";
    public String leaderSet = "&7Ustanowiono nowego &alidera &7gildii!";
    public String leaderOwner = "&7Zostales nowym &aliderem &7gildii!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String leaderMembers = "&7{PLAYER} zostal nowym &aliderem &7gildii!";

    @Comment("")
    @Comment("<------- TNT Hours Messages -------> #")
    public String tntInfo = "&7TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}";
    public String tntProtectDisable = "&7TNT wybucha o każdej porze.";
    public String tntNowEnabled = "&aTNT aktualnie jest włączone.";
    public String tntNowDisabled = "&cTNT aktualnie jest wyłączone.";

    @Comment("")
    @Comment("<------- Deputy Messages -------> #")
    public String deputyMustBeDifferent = "&cNie mozesz mianowac siebie zastepca!";
    public String deputyRemove = "&7Zdegradowno gracza z funkcji &czastepcy&7!";
    public String deputyMember = "&7Zdegradowano Cie z funkcji &czastepcy&7!";
    public String deputySet = "&7Ustanowiono nowego &azastepce &7gildii!";
    public String deputyOwner = "&7Zostales nowym &azastepca &7gildii!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String deputyMembers = "&7{PLAYER} zostal nowym &azastepca &7gildii!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String deputyNoLongerMembers = "&7{PLAYER} juz nie jest &azastepca &7gildii!";

    @Comment("")
    @Comment("<------- Setbase Messages -------> #")
    public String setbaseOutside = "&cNie mozna ustawic domu gildii poza jej terenem!";
    public String setbaseDone = "&7Przeniesiono &adom &7gildii!";

    @Comment("")
    @Comment("<------- PvP Messages -------> #")
    public String pvpOn = "&cWlaczono pvp w gildii!";
    public String pvpOff = "&aWylaczono pvp w gildii!";
    @Comment("Dostępne zmienne: {TAG}")
    public String pvpAllyOn = "&cWlaczono pvp z sojuszem &7{TAG}!";
    public String pvpAllyOff = "&cWylaczono pvp z sojuszem &7{TAG}!";

    public String pvpStatusOn = "&aWlaczone";
    public String pvpStatusOff = "&cWylaczone";

    @Comment("")
    @Comment("<------- Admin Messages -------> #")
    @Comment("Dostępne zmienne: {ADMIN}")
    public String adminGuildBroken = "&cTwoja gildia zostala rozwiazana przez &7{ADMIN}";
    public String adminGuildOwner = "&cTen gracz jest zalozycielem gildii, nie mozna go wyrzucic!";
    public String adminNoRegionFound = "&cGildia nie posiada terenu!";

    public String adminNoPointsGiven = "&cPodaj liczbe punktow!";
    @Comment("Dostępne zmienne: {ERROR}")
    public String adminErrorInNumber = "&cNieznana jest liczba: {ERROR}";
    @Comment("Dostępne zmienne: {PLAYER}, {POINTS}, {POINTS-FORMAT}")
    public String adminPointsChanged = "&aUstawiono &7{POINTS} &apunktow dla gracza &7{PLAYER}";

    public String adminNoKillsGiven = "&cPodaj liczbe zabojstw!";
    @Comment("Dostępne zmienne: {PLAYER}, {KILLS}")
    public String adminKillsChanged = "&aUstawiono &7{KILLS} &azabojstw dla gracza &7{PLAYER}";

    public String adminNoDeathsGiven = "&cPodaj liczbe zgonow!";
    @Comment("Dostępne zmienne: {PLAYER}, {DEATHS}")
    public String adminDeathsChanged = "&aUstawiono &7{DEATHS} &azgonow dla gracza &7{PLAYER}";

    public String adminNoBanTimeGiven = "&cPodaj czas na jaki ma byc zbanowana gildia!";
    public String adminNoReasonGiven = "&cPodaj powod!";
    public String adminGuildBanned = "&cTa gildia jest juz zbanowana!";
    public String adminTimeError = "&cPodano nieprawidlowy czas!";
    @Comment("Dostępne zmienne: {GUILD}, {TIME}")
    public String adminGuildBan = "&aZbanowano gildie &a{GUILD} &7na okres &a{TIME}&7!";

    public String adminGuildNotBanned = "&cTa gildia nie jest zbanowana!";
    @Comment("Dostępne zmienne: {GUILD}")
    public String adminGuildUnban = "&aOdbanowano gildie &7{GUILD}&a!";

    public String adminNoLivesGiven = "&cPodaj liczbe zyc!";
    @Comment("Dostępne zmienne: {GUILD}, {LIVES}")
    public String adminLivesChanged = "&aUstawiono &7{LIVES} &azyc dla gildii &7{GUILD}&a!";

    @Comment("Dostępne zmienne: {GUILD}")
    public String adminGuildRelocated = "&aPrzeniesiono teren gildii &7{GUILD}&a!";

    public String adminNoValidityTimeGiven = "&cPodaj czas o jaki ma byc przedluzona waznosc gildii!";
    @Comment("Dostępne zmienne: {GUILD}, {VALIDITY}")
    public String adminNewValidity = "&aPrzedluzono waznosc gildii &a{GUILD} &7do &a{VALIDITY}&7!";

    public String adminNoNewNameGiven = "&cPodaj nowa nazwe!";
    @Comment("Dostępne zmienne: {GUILD}, {TAG}")
    public String adminNameChanged = "&aZmieniono nazwe gildii na &7{GUILD}&a!";
    public String adminTagChanged = "&aZmieniono tag gildii na &7{TAG}&a!";

    public String adminStopSpy = "&cJuz nie szpiegujesz graczy!";
    public String adminStartSpy = "&aOd teraz szpiegujesz graczy!";

    public String adminGuildsEnabled = "&aZakladanie gildii jest wlaczone!";
    public String adminGuildsDisabled = "&cZakladanie gildii jest wylaczone!";

    public String adminUserNotMemberOf = "&cTen gracz nie jest czlonkiem tej gildii!";
    public String adminAlreadyLeader = "&cTen gracz jest juz liderem gildii!";

    public String adminNoProtectionDateGive = "&cPodaj date ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)";
    public String adminInvalidProtectionDate = "&cTo nie jest poprawna data! Poprawny format to: yyyy/mm/dd hh:mm:ss";
    public String adminProtectionSetSuccessfully = "&aPomyslnie ustawiono ochrone dla gildii &7{TAG} &ado &7{DATE}";

    public String adminGuildHasNoHome = "&cGildia gracza nie ma ustawionej bazy!";
    @Comment("Dostępne zmienne: {ADMIN}")
    public String adminTeleportedToBase = "&aAdmin &7{ADMIN} &ateleportowal cie do bazy gildii!";
    @Comment("Dostępne zmienne: {PLAYER}")
    public String adminTargetTeleportedToBase = "&aGracz &7{PLAYER} &azostal teleportowany do bazy gildii!";

    @Comment("")
    @Comment("<------- SecuritySystem Messages -------> #")
    @Comment("Przedrostek przed wiadomościami systemu bezpieczeństwa")
    public String securitySystemPrefix = "&8[&4Security&8] &7";
    @Comment("Dostępne zmienne: {PLAYER}, {CHEAT}")
    public String securitySystemInfo = "&7Gracz &c{PLAYER}&7 może używać &c{CHEAT}&7 lub innego cheata o podobnym dzialaniu!";
    @Comment("Dostępne zmienne: {NOTE}")
    public String securitySystemNote = "Notatka: &7{NOTE}";
    @Comment("Dostępne zmienne: {DISTANCE}")
    public String securitySystemReach = "&7Zaatakowal krysztal z odleglosci &c{DISTANCE} &7kratek!";
    @Comment("Dostępne zmienne: {BLOCKS}")
    public String securitySystemFreeCam = "Zaatakowal krysztal przez bloki: &c{BLOCKS}";

    @Comment("")
    @Comment("<------- FunnyGuilds Version Messages -------> #")
    public List<String> newVersionAvailable = Arrays.asList(
            "",
            "&8-----------------------------------",
            "&7Dostepna jest nowa wersja &bFunnyGuilds {VERSION_TYPE}&7!",
            "&7Obecna: &b{CURRENT_VERSION}",
            "&7Najnowsza: &b{NEWEST_VERSION}",
            "&7GitHub: &b{GITHUB_LINK}",
            "&7Discord: &b{DISCORD_LINK}",
            "&8-----------------------------------",
            ""
    );

    public String funnyguildsVersion = "&7FunnyGuilds &b{VERSION} &7by &bFunnyGuilds Team";

    @Comment("")
    @Comment("<------- FunnyBin Messages -------> #")
    public List<String> funnybinHelp = Arrays.asList(
            "&cUzycie:",
            "&c/fg funnybin - domyslnie wysyla FunnyGuilds/config.yml i logs/latest.log",
            "&c/fg funnybin config - wysyla FunnyGuilds/config.yml",
            "&c/fg funnybin log - wysyla logs/latest.log",
            "&c/fg funnybin custom <path> - wysyla dowolny plik z folderu serwera na funnybina",
            "&c/fg funnybin bundle <file1> <fileN...> - wysyla dowolne pliki z folderu serwera na funnybina"
    );

    public String funnybinSendingFile = "&aWysylam plik: &b{NUM}&a/&b{TOTAL}&a...";
    public String funnybinFileNotFound = "&cPodany plik: {FILE} nie istnieje";
    public String funnybinFileNotOpened = "&cPodany plik: {FILE} nie mogl byc otworzony (szczegoly w konsoli)";
    public String funnybinFileNotSent = "&cPodany plik: {FILE} nie mogl byc wyslany (szczegoly w konsoli)";
    public String funnybinFileSent = "&aPlik wyslany. Link: &b{LINK}";
    public String funnybinBuildingBundle = "&aTworze paczke z wyslanych plikow...";
    public String funnybinBundleSent = "&aPaczka wyslana. Link: &b{LINK}";
    public String funnybinBundleNotBuilt = "&cWystapil blad podczas tworzenia paczki";

    @Comment("")
    @Comment("<------- System Messages -------> #")
    public String reloadWarn = "&cDziałanie pluginu FunnyGuilds po reloadzie moze byc zaburzone, zalecane jest przeprowadzenie restartu serwera!";
    public String reloadTime = "&aFunnyGuilds &7przeladowano! (&b{TIME}s&7)";
    public String reloadReloading = "&7Przeladowywanie...";
    public String saveallSaving = "&7Zapisywanie...";
    public String saveallSaved = "&7Zapisano (&b{TIME}s&7)!";
    public String loginNickTooShort = "&cNick jest za krotki!";
    public String loginNickTooLong = "&cNick jest za dlugi!";
    public String loginNickInvalid = "&cNick zawiera niedozwolone znaki!";

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    field.set(this, ChatUtils.colored((String) field.get(this)));
                }

                if (field.getType().equals(List.class)) {
                    List<String> list = (List<String>) field.get(this);
                    list.replaceAll(ChatUtils::colored);
                }
            }
        }
        catch (Exception ex) {
            FunnyGuilds.getPluginLogger().error("Could not load message configuration", ex);
        }

        return this;
    }

}
