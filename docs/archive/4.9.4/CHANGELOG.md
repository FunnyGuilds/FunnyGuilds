# FunnyGuilds Tribute (#4.9.4)

- **Tag:** 4.9.4
- **Published:** 2021-04-04T13:49:09Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.9.4
- **Jar:** FunnyGuilds-4.9.4.jar
- **Size:** 4679623

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

**Zmiany:**
- Support JDK16
- GH-1452 Add MONEY from Vault
- GH-225 Properly check distance from the border while creating the guild
- GH-1402 Simplify database 
- GH-1455 Update FunnyCommands and implement FunnyGuildsExceptionHandler
- GH-1494 Typo correction in placeholders {FROM} and {TO}
- d7b682aa594e2772a0c48af9d42e6d17b2e30da0 Add 'funnyguilds.war' permission to plugin.yml
- f35c21319b18a828b556ba618fc152d477f9a0e5 Add permission funnyguilds.tnt & rename permission in PlayerInfoCommand
- a82b064b25ee12b68937292855744e845d5a790a Check if player is online before trying to format tablist cells
- a570825a33e6b43e2751153559a754b65b7236bc Properly deserialize guild pvp value
- GH-1466 Fix database update requests for guilds with disabled regions
- 2642724baadef94fd53705a67b69b5bc85738bb0 Protect entity on explosion if explode-should-affect-only-guild is true
- GH-1463 Add information about possible TNT usage hours
- Add GuildPreRenameEvent, oldName to GuildRenameEvent and oldTag to GuildPreTagEvent
- GH-1474 Get the regionOther message out of the util class
- GH-1478 Rework SecuritySystem and unify project codestyle
- GH-1482 Add warning message to reload command
- Bump dependencies

**Wymagania:**
* Spigot 1.8.8 - 1.16.5
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.9.4](https://ci.insertt.dev/job/FunnyGuilds/job/master/279/artifact/target/FunnyGuilds%204.9.4%20(MC%201.8-1.16.5).jar)
* Maven:
```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/</url>
</repository>

<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>funnyguilds</artifactId>
    <version>4.9.4</version>
</dependency>
```
