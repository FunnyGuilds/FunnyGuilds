# FunnyGuilds Snowdrop (#4.10.1)

- **Tag:** 4.10.1
- **Published:** 2022-05-01T11:52:56Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.10.1
- **Jar:** FunnyGuilds-4.10.1.jar
- **Size:** 6024420

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

**Dodano:**
- GH-1887 Migracje ułatwiające przejście z configu z wersji 4.9.7 (lub starszej) na najnowszą 4.10.1
- GH-1902 GH-1908 Proste wsparcie dla customowych główek na tabliście
- GH-1512 Opcje, aby zablokować teleportacje na teren obcej gildii
- GH-1934 Opcje, aby wyłączyć blokade budowania przez wybuch niektórych entity
- GH-1798 Flagę do WorldGuarda blokującą zakładanie gildii w danym regionie `fg-no-guilds`

**Zmiany:**
- GH-1874 Naprawienie tabowania sojuszników gildii - zamiast tagu była tabowana nazwa
- GH-1879 Poprawienie domyślnej wartość dla `kill-points-change-format` w configu
- GH-1603 Naprawienie błędu z parsowaniem czasu, gdy brakowało `" "` dla godziny
- GH-1886 Naprawienie odświeżanie hologramów, które przy odświeżeniu "skakały"
- GH-1928 Dodanie Multiverse-Core jako softdependa dzięki czemu po restarcie serwera nie psują się gildie stworzone w innych światach
- GH-1939 Usprawnienie systemu chatu gildyjnego
- GH-1944 Naprawienie błędu, który w niektórych przypadkach powodował, że po wyjściu i wróceniu gracza na serwer psuło się wiele rzeczy (uprawnienia, wysyłanie wiadomości etc.)
- GH-1946 Naprawienie serca, które nie pojawiało się po teleportacji gracza w jego okolice
- GH-1949 GH-1952 GH-1953 Naprawienie relacyjnego placeholderu 

**Dla developerów:**
- GH-1894 Dodano RankPlaceholdersService i pozbyto się RankUtils
- GH-1818 Zunifikowano system placeholderów
- GH-1884 Wiadomość o anulowaniu eventu jest teraz wysyłana jedynie wtedy kiedy włączony jest tryb debugowania
- GH-1913 Dodano metody od relacji do klasy Guild takie jak isMember, isAlly etc.
- GH-1935 Dodano GuildHeartInteractEvent (`@Deprecated` GuildHeartAttackEvent)
- GH-1938 Przejście w wielu miejscach z  `System.currentTimeMillis()` na `Instant` i `Duration`

**Inne:**
- Poprawki i usprawnienia w kodzie (7e93a5cba53143fcd7170adca903b9ee5c5a203f, 640681fe2668eacccbb25fbbcb843bcd8e0145a6, fe24537bbb8824233adc86ef4f7dda492edc7bb4, 7e93a5cba53143fcd7170adca903b9ee5c5a203f, a0369850105918c91bf38f24db2cd52bdaadfbd8, 6c5dc23be442ce287f131b9afdd8557272814cc5, 540934bb841af18e5b8fd0faa438895ef0f007d2, 7738afa8da4f45e4a6a665d027a84d320e0dbdd1, 03bc21e5e934c9bd949eff432b4515b7e09b1f0d, a48585e9542c3ca27593cba2e0f0b5c1cfbd40b9)
- Bump zależności

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.1/1.18.2
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.10.1](https://ci.insertt.dev/job/FunnyGuilds/job/master/579/artifact/plugin/build/libs/FunnyGuilds%204.10.1%20(MC%201.8-1.18).jar )
* [FunnyGuilds Nightly](https://ci.insertt.dev/job/FunnyGuilds/job/master/)
* Maven:
```xml
<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/releases</url>
</repository>

<dependency>
    <groupId>net.dzikoysk.funnyguilds</groupId>
    <artifactId>plugin</artifactId>
    <version>4.10.1</version>
    <scope>provided</scope>
</dependency>
```
