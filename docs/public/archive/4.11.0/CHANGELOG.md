# FunnyGuilds Snowdrop (#4.11.0)

- **Tag:** 4.11.0
- **Published:** 2023-01-20T13:29:10Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.11.0

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

**Dodano:**
- GH-2211 Dodanie wsparcia dla 1.19.3
- GH-2069 Opcje przesunięcia lokalizacji domyślnego domu gildii i pozycje głowy gracza przy zakładaniu gildii
- GH-2073 Opcje wyłączenia dwukierunkowej ochrony przed nabijaniem rankingu
- GH-2060 Opcje ochrony przed nabijaniem rankingu jeśli gracze są w sojuszniczych gildiach
- GH-1970 Opcje wyłączenia interakcji w okolicach serca gildii 
- GH-2106 Opcje wyłączenia blokady przed budowaniem przy wybuchu TNT, jeśli wybuchanie TNT jest wyłączone
- GH-2066 Opcje zmiany `character encoding` dla bazy danych
- GH-2139 Opcje ustawienia własnego regexa do sprawdzania poprawności nicków gracz oraz tagów i nazw gildii
- GH-2219 KDA - (zabójstwa + asysty) / śmierci
- GH-2111 Komendę `/statsreset` od resetowania wszystkich statystyk (punktów/zabójstw/śmierci/...), a nie tylko punktów
- GH-2124 Komendę admina do zmiany asyst oraz wylogowań gracza
- GH-2077 Dodatkowe placeholdery - `{G-PVP}`, `{G-PROTECTION-TIME}` oraz `{G-VALIDITY-TIME}`
- GH-2212 Wsparcie dla DecentHolograms
- GH-2217 Prostszy format do używania hex kolorów - `&#XXXXXX`
- GH-2252 Opcje ustawienia wysokości gildii na `GRAVITY` - gildia będzie zakładana na najwyższym bloku na powierzchni

**Zmiany:**
- GH-2213 GH-2210 GH-2086 GH-2070 GH-1726 GH-1077 Przepisanie systemu NameTagów - naprawienie ich wyświetlania, możliwość dodania suffixu, użycia PlaceholderAPI oraz wiele innych udogodnień
- GH-2220 Dodanie placeholdera `{POS}` do nametagu
- GH-2062 Naprawienie komendy `/zapros * <zasięg>`, która nie zapraszała graczy w podanym dystansie
- GH-2031 Naprawienie ConcurrentModificationException przy wysyłaniu tablisty
- GH-2098 Naprawienie zabijania samego siebie
- GH-2099 Usunięcie wsparcia dla MVdWPlaceholderAPI
- GH-2131 Usunięcie wsparcia dla BungeeTabListPlus
- GH-2090 Wsparcie dla kolorów w placeholderach od pozycji gracza w gildii
- GH-2141 Naprawienie wsparcia pluginów od AntyLogout'ów
- GH-2190 Oddzielne uprawnienie (`funnyguilds.admin.teleport`) na ochronę przed teleportowaniem do czyjejś gildii 
- GH-2216 Naprawienie serca gildii pojawiającego się w innych światach
- GH-2224 Mniejsze użycie NMS'a i użycie metod z API jeśli to możliwe
- GH-2256 Usunięcie przedrostka `player-list` z pól w konfiguracji tablisty
- GH-2268 Naprawienie wyłączania pluginu, gdy wystąpi błąd

**Dla developerów:**
- GH-2079 Dodanie eventu `CombatPointsChangeEvent` pozwalającego na łatwe nasłuchiwanie zmian w punktach graczy i osobach asystujący podczas walki
- GH-2130 Usunięcie przestarzałych metod
- GH-2168 Przeniesienie zarządzania historią obrażeń z `UserCache` do `DamageManager`
- GH-2188 Usprawnienie dokumentacji obiektu Rank
- GH-2114 Przejście z legacy `System.currentTimeMillis()` na Instant i Duration w większości pluginu

**Inne:**
- Poprawki i usprawnienia w kodzie (34483801f3e6b820fd71205d335737f27b8f1d61, 8461223ee8f23f0dc4665257a29d646d7cd03b50, 597d8c69a8154b553f1b02e1ea614369118bfeca, a991f51a151bcf518d8d74dfdb46cba96130043f, 91863b935b92f7e6af419cbcc75da8ab7a1126c6, 1c41d5385672afad272995fa5283b1c418943ad1, cf925d0877ace3bd36eec8e0887c5ec85dfd0d1e, b89c193d29362a2dd4ffd05b85c51d3bdfd59abf, d6a814bd80946b5b6b417360572bab7ce0ac24c8, d0962bfd6f8e4421d03e30b3dd05c0d77e5c2b37, b89c63c7b729e83a5311fc0846113373f509fb48, 066f576c795aba0d37712b806cfe57659f166c38, cf35a446b93adff28e4d40ea01842c5acd052bc1)
- f46a9fdae1e92972bb3e0d48f48699a6335624b9 Użycie `kotlin dsl` to budowania projektu
- ffb71a98befd8344d7a87a128067230832f717b8 Wymiana @dependabot na @renovatebot
- Bump zależności

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.2/1.19.2/1.19.3
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.11.0](https://ci.insertt.dev/job/FunnyGuilds/job/master/713/artifact/plugin/build/libs/FunnyGuilds%204.11.1-SNAPSHOT.1642%20(MC%201.8-1.19).jar)
* [FunnyGuilds Nightly](https://ci.insertt.dev/job/FunnyGuilds/job/master/)
* Maven:
```xml
<!-- Releases -->
<repository>
    <id>reposilite-repository</id>
    <url>https://maven.reposilite.com/releases</url>
</repository>
<!-- Snapshots -->
<repository>
    <id>reposilite-snapshots-repository</id>
    <url>https://maven.reposilite.com/snapshots</url>
</repository>
```
```xml
<dependency>
    <groupId>net.dzikoysk.funnyguilds</groupId>
    <artifactId>plugin</artifactId>
    <!-- Release -->
    <version>4.11.0</version>
    <!-- Snapshots (Nightly) -->
    <version>4.11.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

