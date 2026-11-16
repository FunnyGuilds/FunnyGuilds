# FunnyGuilds

[![Build Status](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml/badge.svg)](https://github.com/FunnyGuilds/FunnyGuilds/actions/workflows/gradle.yml) [![Version](https://repo.panda-lang.org/api/badge/latest/releases/net/dzikoysk/funnyguilds/plugin?color=42c611&name=FunnyGuilds&prefix=v)](https://github.com/FunnyGuilds/FunnyGuilds/releases/latest) [![Discord Online](https://img.shields.io/discord/254623242914889729.svg)](https://discord.gg/CYvyq3u)

FunnyGuilds is a configurable guild plugin for Paper servers. Players create guilds, claim territory, form alliances and compete in rankings. Almost every part of it can be tuned from the config files.

## Download

https://funnyguilds.dzikoysk.net/download/

## Requirements

- Paper 1.21.5 - 1.21.11 or 26.1 - 26.3
- Java 21

## Installation

1. Drop the jar into `plugins/`
2. Restart the server
3. Edit `config.yml` and `lang/<locale>.yml`

**Config from 4.x does not load on 5.x, so plan ahead when migrating.**

## Older Minecraft versions

- Below 1.21.5: FunnyGuilds 4.14.0, the last release of the 4.x series
- Full release archive: https://funnyguilds.dzikoysk.net/en/download/

## Features

- **Guilds** - creating and joining, member structure, guild validity, disbanding
- **Territory** - buying and expanding land, area protection, configurable TNT behavior
- **Guild heart** - the physical center of the guild, with protection options and a hologram (DecentHolograms)
- **Alliances & wars** - war modes, buyable extra lives, anti-farming protection
- **Combat & rankings** - points, kills/deaths/assists, top lists, rank farming protection
- **Chat** - guild chat channels, tags in the main chat, nametags above heads
- **Tablist** - header and footer, pagination, ping display, player heads, variables
- **Placeholders** - built-in set, also exposed to other plugins via PlaceholderAPI

## Integrations

Picked up automatically when installed:

- **PlaceholderAPI** - FunnyGuilds placeholders available to every other plugin
- **Vault** - economy and offline-player permission checks
- **WorldGuard & WorldEdit** - custom WorldGuard flags and schematic pasting
- **dynmap** - guild territory markers on the web map
- **DecentHolograms** - hologram above the guild heart

## Links

- Documentation: https://funnyguilds.dzikoysk.net/
- Source and issue tracker: https://github.com/FunnyGuilds/FunnyGuilds
- API for developers (Maven/Gradle): https://funnyguilds.dzikoysk.net/en/api/
- Statistics: https://bstats.org/plugin/bukkit/FunnyGuilds
- Discord: https://discord.gg/CYvyq3u
