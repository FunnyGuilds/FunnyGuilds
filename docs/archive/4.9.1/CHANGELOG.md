# FunnyGuilds Tribute (#4.9.1)

- **Tag:** 4.9.1
- **Published:** 2021-02-17T23:38:28Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.9.1
- **Jar:** FunnyGuilds-4.9.1.jar
- **Size:** 5506251

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 🎄](https://discord.gg/CYvyq3u)

**Zmiany:**
-  GH-1397 Don't create tablist for non-prepared users (Fix #1397) 
- GH-1308 Fix player-only property for admin commands 
- GH-1405 Fix "bugged-blocks-return" when player has one item 
- GH-1427 Change priority of blocked commands to HIGH
- GH-1403 Add GuildTagChangeEvent
- e8711a973e833e506a4194ed9d1effb7b930ad7f Remove sync save from name and tag change commands
- GH-1382 Fix username update
- GH-1417 Add funnyguilds.guild to default player's permissions (Fixes #1417)
- 7facd369ce45b0a3acb5e1fc5f3d27a383b787bc Restore JAR minification
- GH-1416 Update supported versions in final file name 
- 60cf6721fd3ed309ab3288aba79664a6abd94918 Fix /ga delete command
- GH-1434 Add missing RUNTIME retention in validation annotations.
- GH-1432 Add missing permissions in plugin.yml and remove unused ones
- GH-1414 Fix TNT chain explosions and explosions on protected guilds
- GH-1438 Add possibility to disable system of conquest
- GH-1437 Support global tnt protection time 
- 3941f9ed7260a5c3c233d933c5286cb618aa54c1 Do not update region when checking bounds
- GH-1410 Handle guild terrain explosions properly
- GH-1441 Implement FunnyCommands in admin commands
- GH-1446 Fix interaction between our plugin and others while handling explosion event

**Wymagania:**
* Spigot 1.8.8 - 1.16.5
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.9.1](https://ci.insertt.dev/job/FunnyGuilds/job/master/249/artifact/target/FunnyGuilds%204.9.1%20(MC%201.8-1.16.5).jar)
* Maven:
```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/</url>
</repository>

<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>funnyguilds</artifactId>
    <version>4.9.1</version>
</dependency>
```
