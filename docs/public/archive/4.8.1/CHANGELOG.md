# FunnyGuilds Tribute (#4.8.1)

- **Tag:** 4.8.1
- **Published:** 2021-01-12T19:52:07Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.8.1

---


**Discord:**
* [Discord | Bookkity ~ O wszystkim 🎄](https://discord.gg/CYvyq3u)

**Zmiany:**
- GH-1392 Usprawniono działanie tablisty, która niepoprawnie wyświetlała komórki w przypadku konfliktu z innymi pluginami (np. BungeeTabListPlus)
- GH-1369 Poprawiono błąd z kolizjami graczy bez gildii
- GH-1378 Dodano możliwość zablokowania rozlewu wody i lawy poza terenami gildii
- GH-1374 Dodano możliwość zmiany formatowania wyświetlanych przedmiotów w GUI
- e62977117dbbdf00692e38df0b401c561d3816f7 FunnyGuilds wyświetla teraz błędy związane z niepoprawnym ładowaniem gildii i graczy
- 7fc61ab385f21748b012dd11c529726d6f370069 Zmieniono nazwę wydarzenia GuildCreateEvent na GuildPreCreateEvent
- 040f1f39098bb1a04be59c79209ff4712c0dfbee Poprawiono błędy reportowane przez [LGTM](https://lgtm.com/)

**Wymagania:**
* Spigot 1.8.8 - 1.16.4
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.8.1](https://ci.insertt.dev/job/FunnyGuilds/job/master/210/artifact/target/FunnyGuilds%204.8.1%20(MC%201.8-1.16.4).jar)
* Maven:
```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/</url>
</repository>

<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>funnyguilds</artifactId>
    <version>4.8.1</version>
</dependency>
```
