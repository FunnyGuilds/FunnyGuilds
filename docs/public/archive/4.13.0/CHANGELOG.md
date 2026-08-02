# FunnyGuilds Snowdrop (#4.13.0)

- **Tag:** 4.13.0
- **Published:** 2024-06-01T12:26:08Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.13.0

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

# Dodano:
- Wsparcie dla 1.19.4 (GH-2367)
- Wsparcie dla 1.20.1 (GH-2440)
- Wsparcie dla 1.20.2 (GH-2504)
- Wsparcie dla 1.20.4 (GH-2538, GH-2538)
- Wsparcie dla flagi `friendly-fire` WorldGuarda (GH-2421)
- Wsparcie dla tłumaczeń w placeholderach (GH-2462)
- Wsparcie dla dynmapy (GH-2471)
- Wysłanie informacje o śmierci gracza do konsoli serwera (GH-2480)

# Zmiany:
- Usunięto język angielski z listy domyślnie dostępnych języków (GH-2345)
- Usunięto opcję dotyczącą możliwości powiększenia gildii z pliku konfiguracyjnego (nadal można to zrobić poprzez ustawienie pustych itemów) (GH-2384)
- Poprawiono implementację NMS (GH-2544)
- Naprawiono wsparcie dla MariaDB (GH-2438)

# Poprawki:
- Poprawiono wyświetlanie czasu końca ochrony (GH-2360)
- Poprawiono błąd związany z niepoprawnym wyświetlaniem komunikatów bezpieczeństwa gdy sercem gildii jest blok (GH-2391)
- Poprawiono błąd związany z asynchronicznym spawnowaniem serca gildii (GH-2394)
- Poprawiono placeholdery w niektórych wiadomościach (GH-2393)
- Poprawiono sprawdzanie unikalności w kodzie od scoreboardu (GH-2406)
- Poprawiono błąd arytmetyczny związany z dzieleniem liczb ujemnych (GH-2419)
- Poprawiono wsparcie DecentHolograms (GH-2427)
- Poprawiono błąd związany z bazami danych (GH-2452)
- Poprawiono błąd związany z deserializacją regionów (GH-2335)
- Poprawiono błąd związany z wielokrotnym wywoływaniem eventu `GuildRegionEnterEvent` podczas teleportacji (GH-2368)
- Poprawiono wyświetlanie miejsc po przecinku w topce KDR/KDA (GH-2460)

# Inne:
- Zaktualizowano wiele zależności (GH-2365, GH-2369, GH-2436, GH-2550, GH-2551, GH-2552, GH-2553, GH-2554, GH-2555, GH-2556, GH-2557)

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.2/1.19.2/1.19.3/1.20.4
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.13.0](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/46/artifact/plugin/build/libs/FunnyGuilds%204.13.0.1728%20(MC%201.8-1.20).jar)
* [FunnyGuilds Nightly 4.x](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/)
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
    <!-- Stable Release -->
    <version>4.13.0</version>
    <!-- Snapshots (Nightly) -->
    <version>4.13.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

