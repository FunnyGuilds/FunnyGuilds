# FunnyGuilds Snowdrop (#4.10.2)

- **Tag:** 4.10.2
- **Published:** 2022-07-03T22:30:01Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.10.2
- **Jar:** FunnyGuilds-4.10.2.jar
- **Size:** 4962286

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

**Dodano:**
- GH-1995 Wsparcie 1.19
- GH-2009 `/zapros * <zasięg>` zapraszanie wszystkich graczy do gildii w podanym dystansie

**Zmiany:**
- GH-1957 Naprawienie blokowania teleportacji na region wrogiej gildii
- GH-1992 Usunięcie wsparcia dla pluginu LeaderHeads (można uzyskać ten sam efekt dzięki wsparciu PlaceholderAPI)
- GH-1974 Naprawienie formatowania, naprawienie błędu, który powodował, że gracze pozostawali online mimo bycia offline oraz uproszczenie i usprawnienie kodu (zmiany w prawie każdym pliku)
- GH-1318 Przepisanie systemu śledzenia atakujących/ofiar - powinien być on teraz dokładniejszy i mniej pobugowany
- GH-2021 Naprawienie wiadomości błędu przy wklejaniu własnego schematu serca gildii
- GH-2033 Naprawienie CME przy wysyłaniu tablisty dla gracza

**Dla developerów:**
- GH-2010 Oddzielenie wersji Nightly od Releases w repo maven (teraz wersje Nightly są dystrybuowane w formie snapshotów)
- GH-1868 Usprawnienie systemu eventów - opakowywanie nullable zmiennych w Option, usunięcie GuildHeartAtackEvent 
- GH-2004 Dodano GuildChatEvent oraz GuildPreChatEvent (eventy od chatu gildyjnego)

**Inne:**
- Poprawki i usprawnienia w kodzie (49214a26944bc0bf6e8e6138c297b3e324f397c6, 08efa125e6cb4ea9b1627de431a424a42313896f, e111f5b1a35225276c68c1a2c2e6d7b94a3c6d57, e5fbf5ce89e5923a000e3867279efa3081e5b170, 1145ce3ace6ec5f829685c64516da8349dcaa9ad, d4222546c0e433c3b216a22a453cbcec630a2730, 37cef12a138013c45aa037c4221a1f1c915f52d3, ab2b342b28f2902ddce590757c016dd660407ffa, cee83619aed22d4a9108dc614e6ecdcb5e26104f, fe6b0e630adf4466b7bdf6a9de22f9701e4b2407, 3f5e1f4bdc2e7c9d9c29d7154a061cc19044c65a, 5e2040e8babdfe0b52eecf5c3c8cd79257c38c58)
- Bump zależności

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.2/1.19
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.10.2](https://ci.insertt.dev/job/FunnyGuilds/job/master/619/artifact/plugin/build/libs/FunnyGuilds%204.10.2%20(MC%201.8-1.19).jar)
* [FunnyGuilds Nightly](https://ci.insertt.dev/job/FunnyGuilds/job/master/)
* Maven:
```xml
<!-- Releases -->
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/releases</url>
</repository>
<!-- Snapshots -->
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/snapshots</url>
</repository>
```
```xml
<dependency>
    <groupId>net.dzikoysk.funnyguilds</groupId>
    <artifactId>plugin</artifactId>
    <!-- Release -->
    <version>4.10.2</version>
    <!-- Snapshots (Nightly) -->
    <version>4.10.3-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

