# FunnyGuilds Tribute (#4.8.0)

- **Tag:** 4.8.0
- **Published:** 2020-12-24T12:58:40Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.8.0
- **Jar:** FunnyGuilds-4.8.0.jar
- **Size:** 3970774

---


**Discord:**
* [Discord | Bookkity ~ O wszystkim 🎄](https://discord.gg/CYvyq3u)

**Dodano:**
- 6f401627ed8b2842a04fdf1f517037068d44fe79 c30978c 73f99b5 Wsparcie 1.16.4, 1.16.2, 1.16
- GH-1156 Możliwość zablokowania zabijania zwierząt i villagerów na terenie gildii
- GH-1343 Dodano możliwość blokowania stawiania ender crystali na terenach innych gildii
- GH-1273 Wyświetlanie wiadomości międzygildyjnych w logach serwera
- GH-1354 Możliwość ucieczki z terenu gildii na spawn dla graczy bez gildii
- 02c4a7b Opcję współdzielenia dashboardów między pluginami
- GH-1327 Wsparcie dla zmiany nicku gracza (dotyczy premium) 
- GH-1328 Możliwość ustawienia specjalnego czasu oczekiwania na teleportacje dla graczy z uprawnieniem `funnyguilds.vip.baseTeleportTime`
- GH-1323 Możliwość wyłączenia dummy dla osób z uprawnieniem `funnyguilds.admin.disabledummy` 
- GH-1259 Komendę umożliwiającą zmianę tagu gildii 
- GH-1272 Dodano możliwość ograniczenia maksymalnej ilości sojuszy 
- 92f591a Wsparcie dla wyświetlania aliasów tagów w komendzie `/info`
- 30bea00 Wsparcie dla WorldEdita i WorldGuarda 7.0+
- GH-1372 Możliwość zablokowania niszczenia bloków oraz interakcji po wybuchu tnt na terenie gildii (wcześniej można było zablokować jedynie budowanie)
- **Dla developerów**
  - GH-1341 Nowy event związany z wybuchem na terenie gildii
  - GH-1239 Nowy framework do tworzenia komend: [FunnyCommands](https://github.com/FunnyGuilds/FunnyCommands)
  - GH-1239 Nowy format plików konfiguracyjnych ze wsparciem komentarzy i automatycznymi aktualizacjami [CDN](https://github.com/dzikoysk/cdn)

**Zmiany:**
- GH-1353 Usprawniono działanie TNT
- GH-1284 Poprawiono błąd związany z ignorowaniem konfiguracji przy usuwaniu gildii
- GH-1300 Poprawiono błąd związany z możliwością `zepsucia` jajka gildii
- GH-1270, 99d7a0e Poprawiono możliwe błędy przy wyłączaniu serwera
- GH-1271, 82e1b7f Poprawiono błąd związany z ujemną stratą punktów po śmierci
- GH-1307, d53c5a7 Poprawiono błąd związany ze zbyt późną inicjalizacją hooka do WorldGuarda
- GH-1331 Zmieniono sposób obliczania odległości terenu od spawnu, dzięki czemu wyeliminowano narzut odległości po skosie
- GH-1368 Serce gildii oparte o entity (np. ender crystal) jest teraz spawnowane 2 kratki wyżej niż wcześniej
- GH-1357 Naprawiono zły cooldown po wybuchu tnt na terenie gildii 

Dzięki @error56 za przygotowanie listy zmian

**Inne:**
- Bump zależnosci (GH-1264, GH-1265, GH-1266, GH-1267, GH-1268, GH-1278, GH-1279, GH-1280, GH-1281, GH-1288, GH-1289, GH-1293, GH-1294, GH-1295, GH-1301, GH-1302, GH-1303, GH-1305, GH-1306, GH-1309, GH-1310, GH-1311, GH-1312, GH-1313, GH-1314, GH-1315, GH-1316, GH-1317, GH-1319, GH-1320, GH-1321, GH-1325, GH-1329, GH-1330, GH-1332, GH-1335, GH-1336)

**Wymagania:**
* Spigot 1.8.8 - 1.16.4
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.8.0](https://ci.insertt.dev/job/FunnyGuilds/job/master/194/artifact/target/FunnyGuilds%204.8.0%20(MC%201.8-1.16.4).jar)
* Maven:
```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/</url>
</repository>

<dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>funnyguilds</artifactId>
    <version>4.8.0</version>
</dependency>
```
