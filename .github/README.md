FunnyGuilds  
[![Build Status](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml/badge.svg)](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml) [![Version](https://repo.panda-lang.org/api/badge/latest/releases/net/dzikoysk/funnyguilds/plugin?color=42c611&name=FunnyGuilds&prefix=v)](https://ci.insertt.dev/job/FunnyGuilds/job/master/) [![Discord Online](https://img.shields.io/discord/254623242914889729.svg)](https://discord.gg/CYvyq3u) [![Discord](https://img.shields.io/badge/discord-funnyguilds-738bd7.svg?style=square)](https://discord.gg/CYvyq3u) [![Percentage of issues still open](http://isitmaintained.com/badge/open/FunnyGuilds/FunnyGuilds.svg)](http://isitmaintained.com/project/FunnyGuilds/FunnyGuilds "Percentage of issues still open")
===========

### About
FunnyGuilds is an amazing, fully customizable RPG plugin that allows you to create guilds.

---

Website: <a href="https://funnyguilds.dzikoysk.net">FunnyGuilds</a>
<br>
Community: <a href="https://discord.gg/CYvyq3u">Discord</a>
<br>
Description: <a href="https://github.com/FunnyGuilds/FunnyGuilds/wiki">Wiki (PL/EN)</a>

### Supported Spigot versions
1.16.5, 1.17.1, 1.18.2, 1.19.2, 1.19.3

### Download
#### For MC versions 1.8.8 - 1.15.2:
- support for these versions (and FunnyGuilds 4.x) has been discontinued
- you can still use [the latest 4.x build](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/)
- only severe security related issues will likely cause an update for FunnyGuilds 4.x, no new features will be added

#### For MC versions 1.16.5 - 1.19.3:
- get the latest stable version [here](https://github.com/FunnyGuilds/FunnyGuilds/releases) (for now the last FunnyGuilds 4.x release)
- or explore the newest features and fixes of the [nightly versions](https://ci.insertt.dev/job/FunnyGuilds/job/5.x/)
- be aware - nighly versions may be unstable, use them at your own risk

### Hooks
FunnyGuilds hooks into those plugins:
- [WorldEdit](https://dev.bukkit.org/projects/worldedit)
- [WorldGuard](https://dev.bukkit.org/projects/worldguard)
- [Vault](https://dev.bukkit.org/projects/vault)
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders#funnyguilds)
- [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-19-4-papi-support-no-dependencies.96927/)
- [Holographic Displays](https://dev.bukkit.org/projects/holographic-displays)

### Maven
```xml
<!-- Releases -->
<repository>
    <id>reposilite-repository</id>
    <url>https://maven.reposilite.com/releases</url>
</repository>
<!-- Snapshots (Nightly) -->
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
    <version>5.0.0</version>
    <!-- Snapshots (Nightly) -->
    <version>5.0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

#### Stats
Current statistics (since 3.9.x) - [BStats](https://bstats.org/plugin/bukkit/FunnyGuilds)
<br>
The best reported statistics (on [MCStats](http://mcstats.org/plugin/FunnyGuilds)):

| Servers | Players | Users     | Guilds |
|---------|---------|-----------|--------|
| 1158    | 12 336  | 1 034 312 | 16 973 |
