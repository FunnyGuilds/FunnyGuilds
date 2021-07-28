package net.dzikoysk.funnyguilds.data.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Header("Message formatting syntax documentation: https://docs.adventure.kyori.net/minimessage.html#the-components")
public class MessageConfiguration extends OkaeriConfig {
    @Exclude
    private final MiniMessage miniMessage = MiniMessage.get();

    @Comment("<------- Global Date Format -------> #")
    public String dateFormat = "dd.MM.yyyy HH:mm:ss";

    @Comment("<------- No Value Messages -------> #")
    public String gNameNoValue = "Brak (G-NAME/NAME)";
    public String gTagNoValue = "Brak (G-TAG/TAG)";
    public String gOwnerNoValue = "Brak (G-OWNER)";
    public String gDeputiesNoValue = "Brak (G-DEPUTIES)";
    public String gDeputyNoValue = "Brak (G-DEPUTY)";
    public String gValidityNoValue = "Brak (G-VALIDITY)";
    public String gRegionSizeNoValue = "Brak (G-REGION-SIZE)";
    public String alliesNoValue = "Brak (ALLIES)";
    public String enemiesNoValue = "Brak (ENEMIES)";
    public String gtopNoValue = "Brak (GTOP-x)";
    public String ptopNoValue = "Brak (PTOP-x)";
    public String wgRegionNoValue = "Brak (WG-REGION)";
    public String minMembersToIncludeNoValue = "Brak (guild-min-members w config.yml)";

    @Comment("<------- Permission Messages -------> #")
    public Component permission = this.miniMessage.parse("<red>Nie masz wystarczajacych uprawnien do uzycia tej komendy!");
    public Component blockedWorld = this.miniMessage.parse("<red>Zarzadzanie gildiami jest zablokowane na tym swiecie!");
    public Component playerOnly = this.miniMessage.parse("<red>Komenda dostepna tylko dla graczy!");

    @Comment("<------- Rank Messages -------> #")
    public Component rankLastVictimV = this.miniMessage.parse("<gray>Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!");
    public Component rankLastVictimA = this.miniMessage.parse("<gray>Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!");
    public Component rankLastAttackerV = this.miniMessage.parse("<gray>Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!");
    public Component rankLastAttackerA = this.miniMessage.parse("<gray>Ten gracz byl ostatnio zabity przez Ciebie, punkty nie zostaja dodane!");
    public Component rankIPVictim = this.miniMessage.parse("<gray>Ten gracz ma taki sam adres IP, punkty nie zostaja odjete!");
    public Component rankIPAttacker = this.miniMessage.parse("<gray>Ten gracz ma taki sam adres IP, punkty nie zostaja dodane!");
    @Comment("Dostepne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {WEAPON-NAME}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
    public Component rankDeathMessage = this.miniMessage.parse("{ATAG}<aqua>{ATTACKER} <gray>(<green>+{+}<gray>) zabil {VTAG}<aqua>{VICTIM} <gray>(<red>-{-}<gray>) uzywajac <aqua>{WEAPON} {WEAPON-NAME}");
    public Component rankKillTitle = this.miniMessage.parse("<red>Zabiles gracza {VICTIM}");
    public Component rankKillSubtitle = this.miniMessage.parse("<gray>+{+}");
    @Comment("Zamiast zmiennej {ASSISTS} wstawiane sa kolejne wpisy o asystujacych graczach")
    public Component rankAssistMessage = this.miniMessage.parse("<gray>Asystowali: {ASSISTS}");
    @Comment("Dostepne zmienne: {PLAYER}, {+}, {SHARE}")
    public Component rankAssistEntry = this.miniMessage.parse("<aqua>{PLAYER} <gray>(<green>+{+}<gray>, {SHARE}% dmg)");
    @Comment("Znaki oddzielajace kolejne wpisy o asystujacych graczach")
    public Component rankAssistDelimiter = this.miniMessage.parse("<dark_gray>, ");
    @Comment("Dostepne zmienne: {LAST-RANK}, {CURRENT-RANK}")
    public Component rankResetMessage = this.miniMessage.parse("<gray>Zresetowales swoj ranking z poziomu <red>{LAST-RANK} <gray>do poziomu <red>{CURRENT-RANK}<gray>.");

    @Comment("<------- Ban Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    public Component banMessage = this.miniMessage.parse("<gray>Zostales zbanowany do <aqua>{DATE}{NEWLINE}{NEWLINE}<gray>za: <aqua>{REASON}");

    @Comment("<------- Region Messages -------> #")
    public Component regionOther = this.miniMessage.parse("<red>Ten teren nalezy do innej gildii!");
    public Component regionCenter = this.miniMessage.parse("<red>Nie mozesz zniszczyc srodka swojej gildii!");
    @Comment("Dostepne zmienne: {TIME}")
    public Component regionExplode = this.miniMessage.parse("<red>Budowanie na terenie gildii zablokowane na czas <dark_red>{TIME} sekund<red>!");
    @Comment("Dostepne zmienne: {TIME}")
    public Component regionExplodeInteract = this.miniMessage.parse("<red>Nie mozna budowac jeszcze przez <dark_red>{TIME} sekund<red>!");
    public Component regionCommand = this.miniMessage.parse("<red>Tej komendy nie mozna uzyc na terenie innej gildii!");
    public Component regionExplosionHasProtection = this.miniMessage.parse("<red>Eksplozja nie spowodowala zniszczen na terenie gildii, poniewaz jest ona chroniona!");
    public Component regionsDisabled = this.miniMessage.parse("<red>Regiony gildii sa wylaczone!");

    @Comment("<------- ActionBar Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component notificationActionbarIntruderEnterGuildRegion = this.miniMessage.parse("<gray>Gracz <red>{PLAYER} <gray>wkroczyl na teren <red>Twojej <gray>gildii!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationActionbarEnterGuildRegion = this.miniMessage.parse("<gray>Wkroczyles na teren gildii <red>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationActionbarLeaveGuildRegion = this.miniMessage.parse("<gray>Opusciles teren gildii <red>{TAG}<gray>!");

    @Comment("<------- Bossbar Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component notificationBossbarIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationBossbarEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationBossbarLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Chat Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component notificationChatIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationChatEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationChatLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Title Region Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component notificationTitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component notificationSubtitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationTitleEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationSubtitleEnterGuildRegion = notificationActionbarEnterGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationTitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion;
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component notificationSubtitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion;

    @Comment("<------- Broadcast Messages -------> #")
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component broadcastCreate = this.miniMessage.parse("<green>{PLAYER} <gray>zalozyl gildie o nazwie <green>{GUILD} <gray>i tagu <green>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component broadcastDelete = this.miniMessage.parse("<red>{PLAYER} <gray>rozwiazal gildie <red>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component broadcastJoin = this.miniMessage.parse("<green>{PLAYER} <gray>dolaczyl do gildii <green>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component broadcastLeave = this.miniMessage.parse("<red>{PLAYER} <gray>opuscil gildie <red>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component broadcastKick = this.miniMessage.parse("<red>{PLAYER} <gray>zostal <red>wyrzucony <gray>z gildii <red>{TAG}<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {REASON}, {TIME}")
    public Component broadcastBan = this.miniMessage.parse("<gray>Gildia <red>{TAG}<gray> zostala zbanowana za <red>{REASON}<gray>, gratulacje!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component broadcastUnban = this.miniMessage.parse("<gray>Gildia <green>{TAG}<gray> zostala <green>odbanowana<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    public Component broadcastValidity = this.miniMessage.parse("<gray>Gildia <aqua>{TAG} <gray>wygasla<aqua>! <gray>Jej baza znajdowala sie na x: <aqua>{X} <gray>y: <aqua>{Y} <gray>z: <aqua>{Z}<gray>!");
    @Comment("Dostepne zmienne: {WINNER}, {LOSER}")
    public Component broadcastWar = this.miniMessage.parse("<gray>Gildia <dark_red>{WINNER}<gray> podblila gildie <dark_red>{LOSER}<gray>!!");

    @Comment("<------- Help Messages -------> #")
    public List<Component> helpList = Arrays.asList(
            this.miniMessage.parse("<gray>---------------------<dark_gray>[ <green>Gildie <dark_gray>]<gray>---------------------"),
            this.miniMessage.parse("<green>/zaloz [tag] [nazwa] <dark_gray>- <gray>Tworzy gildie"),
            this.miniMessage.parse("<green>/zapros [gracz] <dark_gray>- <gray>Zaprasza gracza do gildii"),
            this.miniMessage.parse("<green>/dolacz [tag] <dark_gray>- <gray>Przyjmuje zaproszenie do gildii"),
            this.miniMessage.parse("<green>/info [tag] <dark_gray>- <gray>Informacje o danej gildii"),
            this.miniMessage.parse("<green>/baza <dark_gray>- <gray>Teleportuje do bazy gildii"),
            this.miniMessage.parse("<green>/powieksz <dark_gray>- <gray>Powieksza teren gildii"),
            this.miniMessage.parse("<green>/przedluz <dark_gray>- <gray>Przedluza waznosc gildii"),
            this.miniMessage.parse("<green>/lider [gracz] <dark_gray>- <gray>Oddaje zalozyciela gildii"),
            this.miniMessage.parse("<green>/zastepca [gracz] <dark_gray>- <gray>Nadaje zastepce gildii"),
            this.miniMessage.parse("<green>/sojusz [tag] <dark_gray>- <gray>Pozwala nawiazac sojusz"),
            this.miniMessage.parse("<green>/opusc <dark_gray>- <gray>Opuszcza gildie"),
            this.miniMessage.parse("<green>/wyrzuc [gracz] <dark_gray>- <gray>Wyrzuca gracza z gildii"),
            this.miniMessage.parse("<green>/rozwiaz [tag] <dark_gray>- <gray>Rozwiazuje sojusz"),
            this.miniMessage.parse("<green>/usun <dark_gray>- <gray>Usuwa gildie"),
            this.miniMessage.parse("<green>/przedmioty <dark_gray>- <gray>Pokazuje przedmioty potrzebne do zalozenia gildii"),
            this.miniMessage.parse("<green>/ucieczka <dark_gray>- <gray>Rozpoczyna ucieczke z terenu innej gildii"));

    @Comment("<------- Admin Help Messages -------> #")
    public List<Component> adminHelpList = Arrays.asList(
            this.miniMessage.parse("<green>/ga dodaj [tag] [nick] <dark_gray>- <gray>Dodaje gracza do gildii"),
            this.miniMessage.parse("<green>/ga usun [tag] <dark_gray>- <gray>Usuwa gildie"),
            this.miniMessage.parse("<green>/ga wyrzuc [nick] <dark_gray>- <gray>Wyrzuca gracza z gildii"),
            this.miniMessage.parse("<green>/ga tp [tag] <dark_gray>- <gray>Teleportuje do bazy gildii"),
            this.miniMessage.parse("<green>/ga points [nick] [points] <dark_gray>- <gray>Ustawia liczbe punktow gracza"),
            this.miniMessage.parse("<green>/ga kills [nick] [kills] <dark_gray>- <gray>Ustawia liczbe zabojstw gracza"),
            this.miniMessage.parse("<green>/ga deaths [nick] [deaths] <dark_gray>- <gray>Ustawia liczbe smierci gracza"),
            this.miniMessage.parse("<green>/ga ban [tag] [czas] [powod] <dark_gray>- <gray>Banuje gildie na okreslony czas"),
            this.miniMessage.parse("<green>/ga unban [tag] <dark_gray>- <gray>Odbanowywuje gildie"),
            this.miniMessage.parse("<green>/ga zycia [tag] [zycia] <dark_gray>- <gray>Ustawia liczbe zyc gildii"),
            this.miniMessage.parse("<green>/ga przenies [tag] <dark_gray>- <gray>Przenosi teren gildii"),
            this.miniMessage.parse("<green>/ga przedluz [tag] [czas] <dark_gray>- <gray>Przedluza waznosc gildii o podany czas"),
            this.miniMessage.parse("<green>/ga ochrona [tag] [czas] <dark_gray>- <gray>Ustawia date wygasniecia ochrony"),
            this.miniMessage.parse("<green>/ga nazwa [tag] [nazwa] <dark_gray>- <gray>Zmienia nazwe gildii"),
            this.miniMessage.parse("<green>/ga tag [tag] [nowy tag] <dark_gray>- <gray>Zmienia tag gildii"),
            this.miniMessage.parse("<green>/ga spy <dark_gray>- <gray>Szpieguje czat gildii"),
            this.miniMessage.parse("<green>/ga enabled <dark_gray>- <gray>Zarzadzanie statusem zakladania gildii"),
            this.miniMessage.parse("<green>/ga lider [tag] [gracz] <dark_gray>- <gray>Zmienia lidera gildii"),
            this.miniMessage.parse("<green>/ga zastepca [tag] [gracz] <dark_gray>- <gray>Nadaje zastepce gildii"),
            this.miniMessage.parse("<green>/ga baza [gracz] <dark_gray>- <gray>Teleportuje gracza do bazy jego gildii"));

    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<Component> playerInfoList = Arrays.asList(
            this.miniMessage.parse("<dark_gray>--------------.-----------------"),
            this.miniMessage.parse("<gray>Gracz: <green>{PLAYER}"),
            this.miniMessage.parse("<gray>Gildia: <green>{TAG}"),
            this.miniMessage.parse("<gray>Miejsce: <green>{RANK} <dark_gray>(<green>{POINTS}<dark_gray>)"),
            this.miniMessage.parse("<gray>Zabojstwa: <green>{KILLS}"),
            this.miniMessage.parse("<gray>Smierci: <green>{DEATHS}"),
            this.miniMessage.parse("<gray>Asysty: <green>{ASSISTS}"),
            this.miniMessage.parse("<gray>Logouty: <green>{LOGOUTS}"),
            this.miniMessage.parse("<gray>KDR: <green>{KDR}"),
            this.miniMessage.parse("<dark_gray>-------------.------------------"));

    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {RANK}")
    public List<Component> playerRightClickInfo = Arrays.asList(
            this.miniMessage.parse("<dark_gray>--------------.-----------------"),
            this.miniMessage.parse("<gray>Gracz: <green>{PLAYER}"),
            this.miniMessage.parse("<gray>Gildia: <green>{TAG}"),
            this.miniMessage.parse("<gray>Miejsce: <green>{RANK} <dark_gray>(<green>{POINTS}<dark_gray>)"),
            this.miniMessage.parse("<dark_gray>-------------.------------------"));

    @Comment("<------- Info Messages -------> #")
    public Component infoTag = this.miniMessage.parse("<red>Podaj tag gildii!");
    public Component infoExists = this.miniMessage.parse("<red>Gildia o takim tagu nie istnieje!");

    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {ASSISTS}, {LOGOUTS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {GUILD-PROTECTION}")
    public List<Component> infoList = Arrays.asList(
            this.miniMessage.parse("<dark_gray>-------------------------------"),
            this.miniMessage.parse("<gray>Gildia: <red>{GUILD} <dark_gray>[<red>{TAG}<dark_gray>]"),
            this.miniMessage.parse("<gray>Zalozyciel: <red>{OWNER}"),
            this.miniMessage.parse("<gray>Zastepcy: <red>{DEPUTIES}"),
            this.miniMessage.parse("<gray>Punkty: <red>{POINTS} <dark_gray>[<red>{RANK}<dark_gray>]"),
            this.miniMessage.parse("<gray>Ochrona: <red>{GUILD-PROTECTION}"),
            this.miniMessage.parse("<gray>Zycia: <dark_red>{LIVES}"),
            this.miniMessage.parse("<gray>Waznosc: <red>{VALIDITY}"),
            this.miniMessage.parse("<gray>Czlonkowie: <gray>{MEMBERS})"),
            this.miniMessage.parse("<gray>Sojusze: <red>{ALLIES}"),
            this.miniMessage.parse("<gray>Wojny: <red>{ENEMIES}"),
            this.miniMessage.parse("<dark_gray>-------------------------------"));

    @Comment("<------- Top Messages -------> #")
    @Comment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu")
    public List<Component> topList = Arrays.asList(
            this.miniMessage.parse("<dark_gray>----------{ <red>TOP 10 <dark_gray>}----------"),
            this.miniMessage.parse("<gray>1<dark_gray>. <red>{GTOP-1}"),
            this.miniMessage.parse("<gray>2<dark_gray>. <red>{GTOP-2}"),
            this.miniMessage.parse("<gray>3<dark_gray>. <red>{GTOP-3}"),
            this.miniMessage.parse("<gray>4<dark_gray>. <red>{GTOP-4}"),
            this.miniMessage.parse("<gray>5<dark_gray>. <red>{GTOP-5}"),
            this.miniMessage.parse("<gray>6<dark_gray>. <red>{GTOP-6}"),
            this.miniMessage.parse("<gray>7<dark_gray>. <red>{GTOP-7}"),
            this.miniMessage.parse("<gray>8<dark_gray>. <red>{GTOP-8}"),
            this.miniMessage.parse("<gray>9<dark_gray>. <red>{GTOP-9}"),
            this.miniMessage.parse("<gray>10<dark_gray>. <red>{GTOP-10}"));

    @Comment("<------- Ranking Messages -------> #")
    @Comment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu")
    public List<Component> rankingList = Arrays.asList(
            this.miniMessage.parse("<dark_gray>----------{ <red>TOP 10 Graczy <dark_gray>}----------"),
            this.miniMessage.parse("<gray>1<dark_gray>. <red>{PTOP-1}"),
            this.miniMessage.parse("<gray>2<dark_gray>. <red>{PTOP-2}"),
            this.miniMessage.parse("<gray>3<dark_gray>. <red>{PTOP-3}"),
            this.miniMessage.parse("<gray>4<dark_gray>. <red>{PTOP-4}"),
            this.miniMessage.parse("<gray>5<dark_gray>. <red>{PTOP-5}"),
            this.miniMessage.parse("<gray>6<dark_gray>. <red>{PTOP-6}"),
            this.miniMessage.parse( "<gray>7<dark_gray>. <red>{PTOP-7}"),
            this.miniMessage.parse("<gray>8<dark_gray>. <red>{PTOP-8}"),
            this.miniMessage.parse("<gray>9<dark_gray>. <red>{PTOP-9}"),
            this.miniMessage.parse("<gray>10<dark_gray>. <red>{PTOP-10}"));

    @Comment("<------- General Messages -------> #")
    public Component generalHasGuild = this.miniMessage.parse("<red>Masz juz gildie!");
    public Component generalNoNameGiven = this.miniMessage.parse("<red>Podaj nazwe gildii!");
    public Component generalHasNoGuild = this.miniMessage.parse("<red>Nie masz gildii!");
    public Component generalIsNotOwner = this.miniMessage.parse("<red>Nie jestes zalozycielem gildii!");
    public Component generalNoTagGiven = this.miniMessage.parse("<red>Podaj tag gildii!");
    public Component generalNoNickGiven = this.miniMessage.parse("<red>Podaj nick gracza!");
    public Component generalUserHasGuild = this.miniMessage.parse("<red>Ten gracz ma juz gildie!");
    public Component generalNoGuildFound = this.miniMessage.parse("<red>Taka gildia nie istnieje!");
    public Component generalNotPlayedBefore = this.miniMessage.parse("<red>Ten gracz nigdy nie byl na serwerze!");
    public Component generalNotOnline = this.miniMessage.parse("<red>Ten gracz nie jest obecnie na serwerze!");

    @Comment("Dostepne zmienne: {TAG}")
    public Component generalGuildNotExists = this.miniMessage.parse("<gray>Gildia o tagu <red>{TAG} <gray>nie istnieje!");
    public Component generalIsNotMember = this.miniMessage.parse("<red>Ten gracz nie jest czlonkiem twojej gildii!");
    public Component generalPlayerHasNoGuild = this.miniMessage.parse("<red>Ten gracz nie ma gildii!");
    public Component generalCommandDisabled = this.miniMessage.parse("<red>Ta komenda jest wylaczona!");
    public Component generalAllyPvpDisabled = this.miniMessage.parse("<red>PVP pomiedzy sojuszami jest wylaczone w konfiguracji!");

    @Comment("<------- Escape Messages -------> #")
    public Component escapeDisabled = this.miniMessage.parse("<red>Przykro mi, ucieczki sa wylaczone!");
    @Comment("Dostepne zmienne: {TIME}")
    public Component escapeStartedUser = this.miniMessage.parse("<green>Dobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!");
    @Comment("Dostepne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    public Component escapeStartedOpponents = this.miniMessage.parse("<red>Gracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})");
    public Component escapeCancelled = this.miniMessage.parse("<red>Ucieczka zostala przerwana!");
    public Component escapeInProgress = this.miniMessage.parse("<red>Ucieczka juz trwa!");
    public Component escapeSuccessfulUser = this.miniMessage.parse("<green>Udalo ci sie uciec!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component escapeSuccessfulOpponents = this.miniMessage.parse("<red>Graczowi {PLAYER} udalo sie uciec z terenu twojej gildii!");
    public Component escapeNoUserGuild = this.miniMessage.parse("<red>Nie masz gildii do ktorej moglbys uciekac!");
    public Component escapeNoNeedToRun = this.miniMessage.parse("<red>Nie znajdujesz sie na terenie zadnej gildii, po co uciekac?");
    public Component escapeOnYourRegion = this.miniMessage.parse("<red>Znajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?");

    @Comment("<------- Create Guild Messages -------> #")
    @Comment("Dostepne zmienne: {LENGTH}")
    public Component createTagLength = this.miniMessage.parse("<gray>Tag nie moze byc dluzszy niz <red>{LENGTH} litery<gray>!");
    @Comment("Dostepne zmienne: {LENGTH}")
    public Component createNameLength = this.miniMessage.parse("<red>Nazwa nie moze byc dluzsza niz <red>{LENGTH} litery<gray>!");
    @Comment("Dostepne zmienne: {LENGTH}")
    public Component createTagMinLength = this.miniMessage.parse("<gray>Tag nie moze byc krotszy niz <red>{LENGTH} litery<gray>!");
    @Comment("Dostepne zmienne: {LENGTH}")
    public Component createNameMinLength = this.miniMessage.parse("<red>Nazwa nie moze byc krotsza niz <red>{LENGTH} litery<gray>!");
    public Component createOLTag = this.miniMessage.parse("<red>Tag gildii moze zawierac tylko litery!");
    public Component createOLName = this.miniMessage.parse("<red>Nazwa gildii moze zawierac tylko litery!");
    public Component createMore = this.miniMessage.parse("<red>Nazwa gildi nie moze zawierac spacji!");
    public Component createNameExists = this.miniMessage.parse("<red>Jest juz gildia z taka nazwa!");
    public Component createTagExists = this.miniMessage.parse("<red>Jest juz gildia z takim tagiem!");
    public Component restrictedGuildName = this.miniMessage.parse("<red>Podana nazwa gildii jest niedozwolona.");
    public Component restrictedGuildTag = this.miniMessage.parse("<red>Podany tag gildii jest niedozwolony.");
    @Comment("Dostepne zmienne: {DISTANCE}")
    public Component createSpawn = this.miniMessage.parse("<gray>Jestes zbyt blisko spawnu! Minimalna odleglosc to <red>{DISTANCE}");
    public Component createIsNear = this.miniMessage.parse("<red>W poblizu znajduje sie jakas gildia, poszukaj innego miejsca!");
    @Comment("Dostepne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
    public Component createRank = this.miniMessage.parse("<red>Aby zalozyc gildie, wymagane jest przynajmniej <gray>{REQUIRED} <red>punktow.");
    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public Component createItems = this.miniMessage.parse("<red>Nie masz wszystkich przedmiotow! Obecnie brakuje Ci <gray>{ITEM} <red>z <gray>{ITEMS}<red>. Najedz na przedmiot, aby dowiedziec sie wiecej");
    @Comment("Dostepne zmienne: {EXP}")
    public Component createExperience = this.miniMessage.parse("<red>Nie posiadasz wymaganego doswiadczenia do zalozenia gildii: <gray>{EXP}");
    @Comment("Dostepne zmienne: {MONEY}")
    public Component createMoney = this.miniMessage.parse("<red>Nie posiadasz wymaganej ilosci pieniedzy do zalozenia gildii: <gray>{MONEY}");
    @Comment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    public Component createGuild = this.miniMessage.parse("<gray>Zalozono gildie o nazwie <green>{GUILD} <gray>i tagu <green>{TAG}<gray>!");
    public Component createGuildCouldNotPasteSchematic = this.miniMessage.parse("<red>Wystapil blad podczas tworzenia terenu gildii, zglos sie do administracji.");
    @Comment("Dostepne zmienne: {BORDER-MIN-DISTANCE}")
    public Component createNotEnoughDistanceFromBorder = this.miniMessage.parse("<red>Jestes zbyt blisko granicy mapy aby zalozyc gildie! (Minimalna odleglosc: {BORDER-MIN-DISTANCE})");

    @Comment("<------- Delete Guild Messages -------> #")
    public Component deleteConfirm = this.miniMessage.parse("<gray>Aby potwierdzic usuniecie gildii, wpisz: <red>/potwierdz");
    public Component deleteToConfirm = this.miniMessage.parse("<red>Nie masz zadnych dzialan do potwierdzenia!");
    public Component deleteSomeoneIsNear = this.miniMessage.parse("<red>Nie mozesz usunac gildii, ktos jest w poblizu!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component deleteSuccessful = this.miniMessage.parse("<gray>Pomyslnie <red>usunieto <gray>gildie!");

    @Comment("<------- Invite Messages -------> #")
    public Component invitePlayerExists = this.miniMessage.parse("<red>Nie ma takiego gracza na serwerze!");
    @Comment("Dostepne zmienne: {AMOUNT}")
    public Component inviteAmount = this.miniMessage.parse("<gray>Osiagnieto juz <red>maksymalna <gray>liczbe czlonkow w gildii! (<red>{AMOUNT}<gray>)");
    public Component inviteAmountJoin = this.miniMessage.parse("<gray>Ta gildia osiagnela juz <red>maksymalna <gray>liczbe czlonkow! (<red>{AMOUNT}<gray>)");
    public Component inviteAllyAmount = this.miniMessage.parse("<gray>Osiagnieto juz <red>maksymalna <gray>liczbe sojuszy miedzygildyjnych! (<red>{AMOUNT}<gray>)");
    @Comment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public Component inviteAllyTargetAmount = this.miniMessage.parse("<gray>Gildia {TAG} posiada juz maksymalna liczbe sojuszy! (<red>{AMOUNT}<gray>)");
    public Component inviteCancelled = this.miniMessage.parse("<red>Cofnieto zaproszenie!");
    @Comment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    public Component inviteCancelledToInvited = this.miniMessage.parse("<gray>Zaproszenie do gildii <red>{GUILD} <gray>zostalo wycofane!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component inviteToOwner = this.miniMessage.parse("<gray>Gracz <green>{PLAYER} <gray>zostal zaproszony do gildii!");
    @Comment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    public Component inviteToInvited = this.miniMessage.parse("<green>Otrzymano zaproszenie do gildii <gray>{TAG}<green>!");

    @Comment("<------- Join Messages -------> #")
    public Component joinHasNotInvitation = this.miniMessage.parse("<red>Nie masz zaproszenia do gildii!");
    public Component joinHasNotInvitationTo = this.miniMessage.parse("<red>Nie otrzymales zaproszenia do tej gildii!");
    public Component joinHasGuild = this.miniMessage.parse("<red>Masz juz gildie!");
    public Component joinTagExists = this.miniMessage.parse("<red>Nie ma gildii o takim tagu!");
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<Component> joinInvitationList = Arrays.asList(
            this.miniMessage.parse("<gray>Otrzymano zaproszenia od gildii: <green>{GUILDS}"),
            this.miniMessage.parse("<gray>Wpisz <green>/dolacz [tag] <gray>aby dolaczyc do wybranej gildii"));

    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public Component joinItems = this.miniMessage.parse("<red>Nie masz wszystkich przedmiotow! Obecnie brakuje Ci <gray>{ITEM} <red>z <gray>{ITEMS}");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component joinToMember = this.miniMessage.parse("<green>Dolaczyles do gildii <gray>{GUILD}");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component joinToOwner = this.miniMessage.parse("<green>{PLAYER} <gray>dolaczyl do <green>Twojej <gray>gildii!");

    @Comment("<------- Leave Messages -------> #")
    public Component leaveIsOwner = this.miniMessage.parse("<red>Zalozyciel <gray>nie moze opuscic gildii!");
    @Comment("Dostepne zmienne: {GUILDS}, {TAG}")
    public Component leaveToUser = this.miniMessage.parse("<gray>Opusciles gildie <green>{GUILD}<gray>!");

    @Comment("<------- Kick Messages -------> #")
    public Component kickOtherGuild = this.miniMessage.parse("<red>Ten gracz nie jest w Twojej gildii!");
    public Component kickOwner = this.miniMessage.parse("<red>Nie mozna wyrzucic zalozyciela!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}, {PLAYER}")
    public Component kickToOwner = this.miniMessage.parse("<red>{PLAYER} <gray>zostal wyrzucony z gildii!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component kickToPlayer = this.miniMessage.parse("<red>Zostales wyrzucony z gildii!");

    @Comment("<------- Enlarge Messages -------> #")
    public Component enlargeMaxSize = this.miniMessage.parse("<red>Osiagnieto juz maksymalny rozmiar terenu!");
    public Component enlargeIsNear = this.miniMessage.parse("<red>W poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!");
    @Comment("Dostepne zmienne: {ITEM}")
    public Component enlargeItem = this.miniMessage.parse("<gray>Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz <red>{ITEM}");
    @Comment("Dostepne zmienne: {SIZE}, {LEVEL}")
    public Component enlargeDone = this.miniMessage.parse("<gray>Teren <green>Twojej <gray>gildii zostal powiekszony i jego wielkosc wynosi teraz <green>{SIZE} <gray>(poz.<green>{LEVEL}<gray>)");

    @Comment("<------- Base Messages -------> #")
    public Component baseTeleportationDisabled = this.miniMessage.parse("<red>Teleportacja do baz gildyjnych nie jest dostepna");
    public Component baseHasNotRegion = this.miniMessage.parse("<red>Twoja gildia nie posiada terenu!");
    public Component baseHasNotCenter = this.miniMessage.parse("<red>Twoja gildia nie posiada srodka regionu!");
    public Component baseIsTeleportation = this.miniMessage.parse("<red>Wlasnie sie teleportujesz!");
    @Comment("Dostepne zmienne: {ITEM}, {ITEMS}")
    public Component baseItems = this.miniMessage.parse("<red>Nie masz wszystkich przedmiotow! Obecnie brakuje Ci <gray>{ITEM} <red>z <gray>{ITEMS}");
    public Component baseDontMove = this.miniMessage.parse("<gray>Nie ruszaj sie przez <red>{TIME} <gray>sekund!");
    public Component baseMove = this.miniMessage.parse("<red>Ruszyles sie, teleportacja przerwana!");
    public Component baseTeleport = this.miniMessage.parse("<green>Teleportacja<gray>...");

    @Comment("<------- War Messages -------> #")
    public Component enemyCorrectUse = this.miniMessage.parse("<gray>Aby rozpoczac wojne z gildia wpisz <red>/wojna [tag]");
    public Component enemySame = this.miniMessage.parse("<red>Nie mozesz rozpoczac wojny z wlasna gildia!");
    public Component enemyAlly = this.miniMessage.parse("<red>Nie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!");
    public Component enemyAlready = this.miniMessage.parse("<red>Prowadzisz juz wojne z ta gildia!");
    @Comment("Dostepne zmienne: {AMOUNT}")
    public Component enemyMaxAmount = this.miniMessage.parse("<gray>Osiagnieto juz <red>maksymalna <gray>liczbe wojen miedzygildyjnych! (<red>{AMOUNT}<gray>)");
    @Comment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    public Component enemyMaxTargetAmount = this.miniMessage.parse("<gray>Gildia {TAG} posiada juz maksymalna liczbe wojen! (<red>{AMOUNT}<gray>)");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component enemyDone = this.miniMessage.parse("<gray>Wypowiedziano gildii <green>{GUILD}<gray> wojne!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component enemyIDone = this.miniMessage.parse("<gray>Gildia <green>{GUILD} <gray>wypowiedziala twojej gildii wojne!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component enemyEnd = this.miniMessage.parse("<gray>Zakonczono wojne z gildia <green>{GUILD}<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component enemyIEnd = this.miniMessage.parse("<gray>Gildia <green>{GUILD} <gray>zakonczyla wojne z twoja gildia!");

    @Comment("<------- Ally Messages -------> #")
    public Component allyHasNotInvitation = this.miniMessage.parse("<gray>Aby zaprosic gildie do sojuszy wpisz <red>/sojusz [tag]");
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<Component> allyInvitationList = Arrays.asList(
            this.miniMessage.parse("<gray>Otrzymano zaproszenia od gildii: <green>{GUILDS})"),
            this.miniMessage.parse("<gray>Aby zaakceptowac uzyj <green>/sojusz [tag]"));
    @Comment("Dostepne zmienne: {TAG}")
    public Component allyAlly = this.miniMessage.parse("<red>Masz juz sojusz z ta gildia!");
    public Component allyDoesntExist = this.miniMessage.parse("<red>Nie posiadasz sojuszu z ta gildia!");
    public Component allySame = this.miniMessage.parse("<red>Nie mozesz nawiazac sojuszu z wlasna gildia!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyDone = this.miniMessage.parse("<gray>Nawiazano sojusz z gildia <green>{GUILD}<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyIDone = this.miniMessage.parse("<gray>Gildia <green>{GUILD} <gray>przystapila do sojuszu!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyReturn = this.miniMessage.parse("<gray>Wycofano zaproszenie do sojuszu dla gildii <red>{GUILD}!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyIReturn = this.miniMessage.parse("<gray>Gildia <red>{GUILD} <gray>wycofala zaprszenie do sojuszu!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyInviteDone = this.miniMessage.parse("<gray>Zaproszono gildie <green>{GUILD} <gray>do sojuszu!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component allyToInvited = this.miniMessage.parse("<gray>Otrzymano zaproszenie do sojuszu od gildii <green>{GUILD}<gray>!");

    @Comment("<------- Break Messages -------> #")
    public Component breakHasNotAllies = this.miniMessage.parse("<red>Twoja gildia nie posiada sojuszy!");
    @Comment("Dostepne zmienne: {GUILDS}")
    public List<Component> breakAlliesList = Arrays.asList(
            this.miniMessage.parse("<gray>Twoja gildia nawiazala sojusz z <green>{GUILDS}"),
            this.miniMessage.parse("<gray>Aby rozwiazac sojusz wpisz <red>/rozwiaz [tag]"));
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component breakAllyExists = this.miniMessage.parse("<gray>Twoja gildia nie posiada sojuszu z gildia (<red>{TAG}<gray><red>{GUILD}<gray>)!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component breakDone = this.miniMessage.parse("<gray>Rozwiazano sojusz z gildia <red>{GUILD}<gray>!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component breakIDone = this.miniMessage.parse("<gray>Gildia <red>{GUILD} <gray>rozwiazala sojusz z Twoja gildia!");

    @Comment("<------- Validity Messages -------> #")
    @Comment("Dostepne zmienne: {TIME}")
    public Component validityWhen = this.miniMessage.parse("<gray>Gildie mozesz przedluzyc dopiero za <red>{TIME}<gray>!");
    @Comment("Dostepne zmienne: {ITEM}")
    public Component validityItems = this.miniMessage.parse("<gray>Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz <red>{ITEM}");
    @Comment("Dostepne zmienne: {DATE}")
    public Component validityDone = this.miniMessage.parse("<gray>Waznosc gildii przedluzona do <green>{DATE}<gray>!");

    @Comment("<------- War Messages -------> #")
    public Component warDisabled = this.miniMessage.parse("<red>Podbijanie gildii jest wyłączone.");
    public Component warHasNotGuild = this.miniMessage.parse("<red>Musisz miec gildie, aby zaatkowac inna!");
    public Component warAlly = this.miniMessage.parse("<red>Nie mozesz zaatakowac sojusznika!");
    @Comment("Dostepne zmienne: {TIME}")
    public Component warWait = this.miniMessage.parse("<gray>Atak na gildie mozliwy za <dark_red>{TIME}");
    @Comment("Dostepne zmienne: {ATTACKED}")
    public Component warAttacker = this.miniMessage.parse("<gray>Twoja gildia pozbawila gildie <dark_red>{ATTACKED} <gray>z <dark_red>1 zycia<gray>!");
    @Comment("Dostepne zmienne: {ATTACKER}")
    public Component warAttacked = this.miniMessage.parse("<gray>Twoja gildia stracila <dark_red>1 zycie <gray>przez <dark_red>{ATTACKER}<gray>!");
    @Comment("Dostepne zmienne: {LOSER}")
    public Component warWin = this.miniMessage.parse("<gray>Twoja gildia <green>podbila <gray>gildie <green>{LOSER}<gray>! Zyskujecie <red>1 zycie<gray>!");
    @Comment("Dostepne zmienne: {WINNER}")
    public Component warLose = this.miniMessage.parse("<gray>Twoja gildia <dark_red>przegrala <gray>wojne z gildia <dark_red>{WINNER}<gray>! <dark_red>Gildia zostaje zniszona<gray>!");

    @Comment("<------- Leader Messages -------> #")
    public Component leaderMustBeDifferent = this.miniMessage.parse("<red>Nie mozesz sobie oddac zalozyciela!");
    public Component leaderSet = this.miniMessage.parse("<gray>Ustanowiono nowego <green>lidera <gray>gildii!");
    public Component leaderOwner = this.miniMessage.parse("<gray>Zostales nowym <green>liderem <gray>gildii!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component leaderMembers = this.miniMessage.parse("<gray>{PLAYER} zostal nowym <green>liderem <gray>gildii!");

    @Comment("<------- TNT Hours Messages -------> #")
    public Component tntInfo = this.miniMessage.parse("<gray>TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}");
    public Component tntProtectDisable = this.miniMessage.parse("<gray>TNT wybucha o każdej porze.");
    public Component tntNowEnabled = this.miniMessage.parse("<green>TNT aktualnie jest włączone.");
    public Component tntNowDisabled = this.miniMessage.parse("<red>TNT aktualnie jest wyłączone.");

    @Comment("<------- Deputy Messages -------> #")
    public Component deputyMustBeDifferent = this.miniMessage.parse("<red>Nie mozesz mianowac siebie zastepca!");
    public Component deputyRemove = this.miniMessage.parse("<gray>Zdegradowno gracza z funkcji <red>zastepcy<gray>!");
    public Component deputyMember = this.miniMessage.parse("<gray>Zdegradowano Cie z funkcji <red>zastepcy<gray>!");
    public Component deputySet = this.miniMessage.parse("<gray>Ustanowiono nowego <green>zastepce <gray>gildii!");
    public Component deputyOwner = this.miniMessage.parse("<gray>Zostales nowym <green>zastepca <gray>gildii!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component deputyMembers = this.miniMessage.parse("<gray>{PLAYER} zostal nowym <green>zastepca <gray>gildii!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component deputyNoLongerMembers = this.miniMessage.parse("<gray>{PLAYER} juz nie jest <green>zastepca <gray>gildii!");

    @Comment("<------- Setbase Messages -------> #")
    public Component setbaseOutside = this.miniMessage.parse("<red>Nie mozna ustawic domu gildii poza jej terenem!");
    public Component setbaseDone = this.miniMessage.parse("<gray>Przeniesiono <green>dom <gray>gildii!");

    @Comment("<------- PvP Messages -------> #")
    public Component pvpOn = this.miniMessage.parse("<red>Wlaczono pvp w gildii!");
    public Component pvpOff = this.miniMessage.parse("<green>Wylaczono pvp w gildii!");
    @Comment("Dostepne zmienne: {TAG}")
    public Component pvpAllyOn = this.miniMessage.parse("<red>Wlaczono pvp z sojuszem <gray>{TAG}!");
    public Component pvpAllyOff = this.miniMessage.parse("<red>Wylaczono pvp z sojuszem <gray>{TAG}!");

    @Comment("<------- Admin Messages -------> #")
    @Comment("Dostepne zmienne: {ADMIN}")
    public Component adminGuildBroken = this.miniMessage.parse("<red>Twoja gildia zostala rozwiazana przez <gray>{ADMIN}");
    public Component adminGuildOwner = this.miniMessage.parse("<red>Ten gracz jest zalozycielem gildii, nie mozna go wyrzucic!");
    public Component adminNoRegionFound = this.miniMessage.parse("<red>Gildia nie posiada terenu!");

    public Component adminNoPointsGiven = this.miniMessage.parse("<red>Podaj liczbe punktow!");
    @Comment("Dostepne zmienne: {ERROR}")
    public Component adminErrorInNumber = this.miniMessage.parse("<red>Nieznana jest liczba: {ERROR}");
    @Comment("Dostepne zmienne: {PLAYER}, {POINTS}, {POINTS-FORMAT}")
    public Component adminPointsChanged = this.miniMessage.parse("<green>Ustawiono <gray>{POINTS} <green>punktow dla gracza <gray>{PLAYER}");

    public Component adminNoKillsGiven = this.miniMessage.parse("<red>Podaj liczbe zabojstw!");
    @Comment("Dostepne zmienne: {PLAYER}, {KILLS}")
    public Component adminKillsChanged = this.miniMessage.parse("<green>Ustawiono <gray>{KILLS} <green>zabojstw dla gracza <gray>{PLAYER}");

    public Component adminNoDeathsGiven = this.miniMessage.parse("<red>Podaj liczbe zgonow!");
    @Comment("Dostepne zmienne: {PLAYER}, {DEATHS}")
    public Component adminDeathsChanged = this.miniMessage.parse("<green>Ustawiono <gray>{DEATHS} <green>zgonow dla gracza <gray>{PLAYER}");

    public Component adminNoBanTimeGiven = this.miniMessage.parse("<red>Podaj czas na jaki ma byc zbanowana gildia!");
    public Component adminNoReasonGiven = this.miniMessage.parse("<red>Podaj powod!");
    public Component adminGuildBanned = this.miniMessage.parse("<red>Ta gildia jest juz zbanowana!");
    public Component adminTimeError = this.miniMessage.parse("<red>Podano nieprawidlowy czas!");
    @Comment("Dostepne zmienne: {GUILD}, {TIME}")
    public Component adminGuildBan = this.miniMessage.parse("<green>Zbanowano gildie <green>{GUILD} <gray>na okres <green>{TIME}<gray>!");

    public Component adminGuildNotBanned = this.miniMessage.parse("<red>Ta gildia nie jest zbanowana!");
    @Comment("Dostepne zmienne: {GUILD}")
    public Component adminGuildUnban = this.miniMessage.parse("<green>Odbanowano gildie <gray>{GUILD}<green>!");

    public Component adminNoLivesGiven = this.miniMessage.parse("<red>Podaj liczbe zyc!");
    @Comment("Dostepne zmienne: {GUILD}, {LIVES}")
    public Component adminLivesChanged = this.miniMessage.parse("<green>Ustawiono <gray>{LIVES} <green>zyc dla gildii <gray>{GUILD}<green>!");

    @Comment("Dostepne zmienne: {GUILD}")
    public Component adminGuildRelocated = this.miniMessage.parse("<green>Przeniesiono teren gildii <gray>{GUILD}<green>!");

    public Component adminNoValidityTimeGiven = this.miniMessage.parse("<red>Podaj czas o jaki ma byc przedluzona waznosc gildii!");
    @Comment("Dostepne zmienne: {GUILD}, {VALIDITY}")
    public Component adminNewValidity = this.miniMessage.parse("<green>Przedluzono waznosc gildii <green>{GUILD} <gray>do <green>{VALIDITY}<gray>!");

    public Component adminNoNewNameGiven = this.miniMessage.parse("<red>Podaj nowa nazwe!");
    @Comment("Dostepne zmienne: {GUILD}, {TAG}")
    public Component adminNameChanged = this.miniMessage.parse("<green>Zmieniono nazwe gildii na <gray>{GUILD}<green>!");
    public Component adminTagChanged = this.miniMessage.parse("<green>Zmieniono tag gildii na <gray>{TAG}<green>!");

    public Component adminStopSpy = this.miniMessage.parse("<red>Juz nie szpiegujesz graczy!");
    public Component adminStartSpy = this.miniMessage.parse("<green>Od teraz szpiegujesz graczy!");

    public Component adminGuildsEnabled = this.miniMessage.parse("<green>Zakladanie gildii jest wlaczone!");
    public Component adminGuildsDisabled = this.miniMessage.parse("<red>Zakladanie gildii jest wylaczone!");

    public Component adminUserNotMemberOf = this.miniMessage.parse("<red>Ten gracz nie jest czlonkiem tej gildii!");
    public Component adminAlreadyLeader = this.miniMessage.parse("<red>Ten gracz jest juz liderem gildii!");

    public Component adminNoProtectionDateGive = this.miniMessage.parse("<red>Podaj date ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)");
    public Component adminInvalidProtectionDate = this.miniMessage.parse("<red>To nie jest poprawna data! Poprawny format to: yyyy/mm/dd hh:mm:ss");
    public Component adminProtectionSetSuccessfully = this.miniMessage.parse("<green>Pomyslnie ustawiono ochrone dla gildii <gray>{TAG} <green>do <gray>{DATE}");

    public Component adminGuildHasNoHome = this.miniMessage.parse("<red>Gildia gracza nie ma ustawionej bazy!");
    @Comment("Dostepne zmienne: {ADMIN}")
    public Component adminTeleportedToBase = this.miniMessage.parse("<green>Admin <gray>{ADMIN} <green>teleportowal cie do bazy gildii!");
    @Comment("Dostepne zmienne: {PLAYER}")
    public Component adminTargetTeleportedToBase = this.miniMessage.parse("<green>Gracz <gray>{PLAYER} <green>zostal teleportowany do bazy gildii!");

    @Comment("<------- SecuritySystem Messages -------> #")
    @Comment("Przedrostek przed wiadomościami systemu bezpieczeństwa")
    public Component securitySystemPrefix = this.miniMessage.parse("<dark_gray>[<dark_red>Security<dark_gray>] <gray>");
    @Comment("Dostepne zmienne: {PLAYER}, {CHEAT}")
    public Component securitySystemInfo = this.miniMessage.parse("<gray>Gracz <red>{PLAYER}<gray> może używać <red>{CHEAT}<gray> lub innego cheata o podobnym dzialaniu!");
    @Comment("Dostepne zmienne: {NOTE}")
    public Component securitySystemNote = this.miniMessage.parse("Notatka: <gray>{NOTE}");
    @Comment("Dostepne zmienne: {DISTANCE}")
    public Component securitySystemReach = this.miniMessage.parse("<gray>Zaatakowal krysztal z odleglosci <red>{DISTANCE} <gray>kratek!");
    @Comment("Dostepne zmienne: {BLOCKS}")
    public Component securitySystemFreeCam = this.miniMessage.parse("Zaatakowal krysztal przez bloki: <red>{BLOCKS}");

    @Comment("<------- System Messages -------> #")
    public Component reloadWarn = this.miniMessage.parse("<red>Działanie pluginu FunnyGuilds po reloadzie moze byc zaburzone, zalecane jest przeprowadzenie restartu serwera!");

    @Exclude
    public static final Pattern LEGACY_COLOR_CODE_PATTERN = Pattern.compile("(?:\u00a7)([0-9A-Fa-fK-Ok-oRXrx][^\u00a7]*)");

    public static boolean containsLegacyColors(String string) {
        return LEGACY_COLOR_CODE_PATTERN.matcher(string).find();
    }
}
