# FunnyGuilds

[![Build Status](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml/badge.svg)](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml)
[![Version](https://repo.panda-lang.org/api/badge/latest/releases/net/dzikoysk/funnyguilds/plugin?color=42c611&name=FunnyGuilds&prefix=v)](https://ci.insertt.dev/job/FunnyGuilds/job/master/)
[![Discord Online](https://img.shields.io/discord/254623242914889729.svg)](https://discord.gg/CYvyq3u)
[![Discord](https://img.shields.io/badge/discord-funnyguilds-738bd7.svg?style=square)](https://discord.gg/CYvyq3u)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/FunnyGuilds/FunnyGuilds.svg)](http://isitmaintained.com/project/FunnyGuilds/FunnyGuilds "Percentage of issues still open")

### :clipboard: About

FunnyGuilds is an amazing, fully customizable RPG plugin that allows you to create guilds

Website: [FunnyGuilds](https://funnyguilds.dzikoysk.net)</br>
Community: [Discord](https://discord.gg/CYvyq3u)</br>
Wiki: [Wiki (PL/EN)](https://github.com/FunnyGuilds/FunnyGuilds/wiki)

:poland:&ensp;Potrzebujesz polskiej wersji tego opisu? Znajdziesz ją [tutaj](README-PL.md)&ensp;:poland:

---

### :video_game: Supported MC versions

1.16.5, 1.17.1, 1.18.2, 1.19.2, 1.19.3, 1.19.4, 1.20.1

### :desktop_computer: Supported server platforms

Spigot, Paper, Purpur

---

### :rocket: Download

#### ⚓ Releases (1.8.8-1.19.3)

- get the latest stable version [here](https://github.com/FunnyGuilds/FunnyGuilds/releases) (for now the last FunnyGuilds 4.x release)

#### :hourglass: Branch 4.x (1.8.8-1.20.1)

- support for FunnyGuilds *4.x* has been discontinued and will be completely dropped when FunnyGuilds *5.x* will be released
- only severe security related issues and bugs will likely cause an update for FunnyGuilds *4.x*, no new features will be added
- you can still use [the latest 4.x build](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/)

#### :hourglass_flowing_sand: Branch 5.x (1.16.5-1.20.1)

- ⚠️ nightly versions may be unstable, use them at your own risk - you can check current list of problems related to *5.x* [here](https://github.com/FunnyGuilds/FunnyGuilds/issues/2455)
- ⚠️ FunnyGuilds *5.x* is incompatible with FunnyGuilds *4.x* - use only if you are building your server from the grounds up using FunnyGuilds *5.x* - updating from *4.x* could cause **data corruption or its loss**
- explore the newest features and fixes of the [nightly versions](https://ci.insertt.dev/job/FunnyGuilds/job/5.x/)

---

### :hook: Hooks

FunnyGuilds hooks (optionally) into plugins listed below:

- [WorldEdit](https://dev.bukkit.org/projects/worldedit)
- [WorldGuard](https://dev.bukkit.org/projects/worldguard)
- [Vault](https://dev.bukkit.org/projects/vault)
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders#funnyguilds)
- [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-19-4-papi-support-no-dependencies.96927/)
- [Holographic Displays](https://dev.bukkit.org/projects/holographic-displays)

---

### :gear: Maven

```xml
<!-- Releases -->
<repository>
    <id>reposilite-repository</id>
    <url>https://maven.reposilite.com/releases</url>
</repository>

<!-- Snapshots (nightly) -->
<repository>
    <id>reposilite-snapshots-repository</id>
    <url>https://maven.reposilite.com/snapshots</url>
</repository>
```

```xml
<!-- Release -->
<dependency>
    <groupId>net.dzikoysk.funnyguilds</groupId>
    <artifactId>plugin</artifactId>
    <version>4.12.0</version>
    <scope>provided</scope>
</dependency>

<!-- Snapshot (nightly) -->
<dependency>
    <groupId>net.dzikoysk.funnyguilds</groupId>
    <artifactId>plugin</artifactId>
    <version>4.12.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

---

### :chart_with_upwards_trend: Usage stats

Current statistics (since FunnyGuilds 3.9.x): [BStats](https://bstats.org/plugin/bukkit/FunnyGuilds)</br>
The best reported statistics (on [MCStats](http://mcstats.org/plugin/FunnyGuilds)):

| Servers | Players | Users     | Guilds |
|---------|---------|-----------|--------|
| 1158    | 12 336  | 1 034 312 | 16 973 |
