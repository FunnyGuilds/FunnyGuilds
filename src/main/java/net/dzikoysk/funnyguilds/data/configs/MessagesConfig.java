package net.dzikoysk.funnyguilds.data.configs;

import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgComment;

import java.util.Arrays;
import java.util.List;

public class MessagesConfig {
    @CfgComment("<------- Permission Messages -------> #")
    public String permission = "&cNie masz wystarczajacych uprawnien do uzycia tej komendy!";
    public String blockedWorld = "&cZarzadzanie gildiami jest zablokowane na tym swiecie!";

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

    @CfgComment("<------- Notification Bar Messages -------> #")
    @CfgComment("Available variables: {PLAYER}")
    public String notificationMember = "&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String notificationOther = "&7Wkroczyles na teren gildii &c{TAG}&7!";

    @CfgComment("<------- Region Messages -------> #")
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String regionEnter = "&7Wkroczyles na teren gildii &c{TAG}&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String regionLeave = "&7Opuszczono teren gildii &c{TAG}&7!";
    public String regionOther = "&cTen teren nalezy do innej gildii!";
    public String regionCenter = "&cNie mozesz zniszczyc srodka swojej gildii!";
    @CfgComment("Available variables: {TIME}")
    public String regionExplode = "&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!";
    @CfgComment("Available variables: {TIME}")
    public String regionExplodeInteract = "&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!";
    public String regionCommand = "&cTej komendy nie mozna uzyc na terenie innej gildii!";

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
            "&a/usun &8- &7Usuwa gildie");

    @CfgComment("<------- Player Info Messages -------> #")
    public String playerInfoExists = "&cTaki gracz nigdy nie byl na serwerze!";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {KILLS}, {DEATHS}, {RANK}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> playerInfoList = Arrays.asList(
            "&8--------------.-----------------",
            "&7Gracz: &a{PLAYER}",
            "&7Gildia: &a{TAG}",
            "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
            "&7Zabojstwa: &a{KILLS}",
            "&7Smierci: &a{DEATHS}",
            "&8-------------.------------------");

    @CfgComment("<------- Info Messages -------> #")
    public String infoTag = "&cPodaj tag gildii!";
    public String infoExists = "&cGildia o takim tagu nie istnieje!";
    @CfgComment("Available variables: {GUILD}, {TAG}, {OWNER}, {MEMBERS}, ")
    @CfgComment("{POINTS}, {KILLS}, {DEATHS}, {ALLIES}, {RANK}, {VALIDITY},")
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

    @CfgComment("<------- Create Guild Messages -------> #")
    public String createHasGuild = "&cMasz juz gildie!";
    public String createName = "&cPodaj nazwe gildii!";
    public String createTag = "&cPodaj tag gildii!";
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
    @CfgComment("Available variables: {DISTANCE}")
    public String createSpawn = "&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}";
    public String createIsNear = "&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!";
    @CfgComment("Available variables: {ITEM}, {ITEMS}")
    public String createItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    @CfgComment("Available variables: {PLAYER}, {GUILD}, {TAG}")
    public String createGuild = "&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!";

    @CfgComment("<------- Delete Guild Messages -------> #")
    public String deleteHasNotGuild = "&cNie masz gildii!";
    public String deleteIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String deleteConfirm = "&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz";
    public String deleteToConfirm = "&cNie masz zadnych dzialan do potwierdzenia!";
    public String deleteSomeoneIsNear = "&cNie mozesz usunac gildii, ktos jest w poblizu!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String deleteSuccessful = "&7Pomyslnie &cusunieto &7gildie!";

    @CfgComment("<------- Invite Messages -------> #")
    public String inviteHasNotGuild = "&cNie masz gildii!";
    public String inviteIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String invitePlayer = "&cPodaj gracza!";
    public String invitePlayerExists = "&cNie ma takiego gracza na serwerze!";
    @CfgComment("Available variables: {AMOUNT}")
    public String inviteAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT})";
    public String inviteHasGuild = "&cTen gracz ma juz gildie!";
    public String inviteCancelled = "&cCofnieto zaproszenie!";
    @CfgComment("Available variables: {OWNER}, {GUILD}, {TAG}")
    public String inviteCancelledToInvited = "&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!";
    @CfgComment("Available variables: {PLAYER}")
    public String inviteToOwner = "&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!";
    @CfgComment("Available variables: {OWNER}, {GUILD}, {TAG}")
    public String inviteToInvited = "&aOtrzymano zaproszenie do gildii &7{TAG}&a!";

    @CfgComment("<------- Invite Messages -------> #")
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
    public String leaveHasNotGuild = "&cNie masz gildii!";
    public String leaveIsOwner = "&cZalozyciel &7nie moze opuscic gildii!";
    @CfgComment("Available variables: {GUILDS}, {TAG}")
    public String leaveToUser = "&7Opusciles gildie &a{GUILD}&7!";

    @CfgComment("<------- Kick Messages -------> #")
    public String kickHasNotGuild = "&cNie masz gildii!";
    public String kickIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String kickPlayer = "&cPodaj gracza!";
    public String kickToHasNotGuild = "&cTen gracz nie ma gildii!";
    public String kickOtherGuild = "&cTen gracz nie jest w Twojej gildii!";
    public String kickOwner = "&cNie mozna wyrzucic zalozyciela!";
    @CfgComment("Available variables: {PLAYER}")
    public String kickToOwner = "&c{PLAYER} &7zostal wyrzucony z gildii!";
    @CfgComment("Available variables: {GUILD}")
    public String kickToPlayer = "&cZostales wyrzucony z gildii!";

    @CfgComment("<------- Enlarge Messages -------> #")
    public String enlargeHasNotGuild = "&cNie masz gildii!";
    public String enlargeIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String enlargeMaxSize = "&cOsiagnieto juz maksymalny rozmiar terenu!";
    public String enlargeIsNear = "&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!";
    @CfgComment("Available variables: {ITEM}")
    public String enlargeItem = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}";
    @CfgComment("Available variables: {SIZE}, {LEVEL}")
    public String enlargeDone = "&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)";

    @CfgComment("<------- Base Messages -------> #")
    public String baseTeleportationDisabled = "&cTeleportacja do baz gildyjnych nie jest dostepna";
    public String baseHasNotGuild = "&cNie masz gildii!";
    public String baseHasNotRegion = "&cTwoja gildia nie posiada terenu!";
    public String baseHasNotCenter = "&cTwoja gildia nie posiada srodka regionu!";
    public String baseIsTeleportation = "&cWlasnie sie teleportujesz!";
    @CfgComment("Available variables: {ITEM}, {ITEMS}")
    public String baseItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}";
    public String baseDontMove = "&7Nie ruszaj sie przez &c{TIME} &7sekund!";
    public String baseMove = "&cRuszyles sie, teleportacja przerwana!";
    public String baseTeleport = "&7Teleportacja ...";

    @CfgComment("<------- Ally Messages -------> #")
    public String allyHasNotGuild = "&cNie masz gildii!";
    public String allyIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String allyHasNotInvitation = "&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]";
    @CfgComment("Available variables: {GUILDS}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> allyInvitationList = Arrays.asList(
            "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
            "&7Aby zaakceptowac uzyj &a/sojusz [tag]");
    @CfgComment("Available variables: {TAG}")
    public String allyGuildExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!";
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
    public String breakHasNotGuild = "&cNie masz gildii!";
    public String breakIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String breakHasNotAllies = "&cTwoja gildia nie posiada sojuszy!";
    @CfgComment("Available variables: {GUILDS}")
    @CfgCollectionStyle(CfgCollectionStyle.CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> breakAlliesList = Arrays.asList(
            "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
            "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]");
    @CfgComment("Available variables: {TAG}")
    public String breakGuildExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakAllyExists = "&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7&c{GUILD}&7)!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakDone = "&7Rozwiazano sojusz z gildia &c{GUILD}&7!";
    @CfgComment("Available variables: {GUILD}, {TAG}")
    public String breakIDone = "&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!";

    @CfgComment("<------- Validity Messages -------> #")
    public String validityHasNotGuild = "&cNie masz gildii!";
    public String validityIsNotOwner = "&cNie jestes zalozycielem gildii!";
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
    public String leaderHasNotGuild = "&cNie masz gildii!";
    public String leaderIsNotOwner = "&cNie jestes zalozycielem gildii";
    public String leaderPlayer = "&cPodaj gracza!";
    public String leaderPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!";
    public String leaderIsNotMember = "&cTen gracz nie jest czlonkiem Twojej gildii!";
    public String leaderSet = "&7Ustanowiono nowego &alidera &7gildii!";
    public String leaderOwner = "&7Zostales nowym &aliderem &7gildii!";

    @CfgComment("<------- Deputy Messages -------> #")
    public String deputyHasNotGuild = "&cNie masz gildii!";
    public String deputyIsNotOwner = "&cNie jestes zalozycielem gildii";
    public String deputyPlayer = "&cPodaj gracza!";
    public String deputyPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!";
    public String deputyIsNotMember = "&cTen gracz nie jest czlonkiem Twojej gildii!";
    public String deputyRemove = "&7Zdegradowno gracza z funkcji &czastepcy&7!";
    public String deputyMember = "&7Zdegradowano Cie z funkcji &czastepcy&7!";
    public String deputySet = "&7Ustanowiono nowego &azastepce &7gildii!";
    public String deputyOwner = "&7Zostales nowym &azastepca &7gildii!";

    @CfgComment("<------- Setbase Messages -------> #")
    public String setbaseHasNotGuild = "&cNie masz gildii!";
    public String setbaseIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String setbaseOutside = "&cNie mozna ustawic domu gildii poza jej terenem!";
    public String setbaseDone = "&7Przeniesiono &adom &7gildii!";

    @CfgComment("<------- PvP Messages -------> #")
    public String pvpHasNotGuild = "&cNie masz gildii!";
    public String pvpIsNotOwner = "&cNie jestes zalozycielem gildii!";
    public String pvpOn = "&cWlaczono &7pvp w gildii!";
    public String pvpOff = "&aWylaczono &7pvp w gildii!";
}
