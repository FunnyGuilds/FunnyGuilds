# 🌼 FunnyGuilds Primrose 🌼 (#5.0.0)

- **Tag:** 5.0.0
- **Published:** 2026-11-15T22:32:00Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/5.0.0
- **Jar:** FunnyGuilds.5.0.0.MC.1.21.5.-.26.x.jar
- **Size:** 9321089

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

> [!IMPORTANT]
> **FunnyGuilds 5.0.0 to pierwsze wydanie serii 5.x i nie jest zgodne z 4.x.**
> * Potrzebujesz Papera 1.21.5+ i Javy 21. Spigot oraz wersje starsze niż 1.21 nie są już wspierane.
> * Konfiguracja i wiadomości z 4.x się nie wczytają - zacznij od czystej instalacji i przenieś ustawienia ręcznie.
> * Przed aktualizacją zrób kopię konfiguracji i danych.
>
> Opis wszystkich opcji znajdziesz w nowej dokumentacji: [funnyguilds.dzikoysk.net](https://funnyguilds.dzikoysk.net) :stars:

# Zmiany

### Wersje i wymagania
* GH-2648 Usunięto wsparcie wersji starszych niż 1.21
* GH-2622 Dodano wsparcie wersji nowszych niż 1.21.4
* GH-2673 Dodano wsparcie 1.21.2 i 1.21.3
* GH-2723 Dodano wsparcie wszystkich wersji 26.x
* GH-2727 Dodano wsparcie Papera 26.3
* GH-2722 Dodano obsługę nowych pakietów interakcji i ataku
* GH-2653 Podniesiono wymaganą wersję Javy do 21
* GH-2659 Plugin korzysta teraz z Adventure wbudowanego w Papera, bez relokacji
* GH-2666 Wiadomości przeniesiono na Adventure i MiniMessage (YetAnotherMessagesLibrary 7.x)
* GH-2654 Zaktualizowano okaeri-configs do 6.0.0
* Usunięto stare integracje: WorldEdit/WorldGuard 6, HolographicDisplays, dawne ViaVersion i FunnyTab (#2660)
* GH-2669 Usunięto przestarzały kod
* Plugin nie zmienia już własnej nazwy w `plugin.yml` (#2661)
* GH-2718 Usunięto nieużywane pola konfiguracji komend `top` i `ranking`

### Wojny i podbijanie gildii
* GH-2672 Dodano nowy system wojen: tryby `MODERN` i `LEGACY`, statystyki wojen, zdobywanie żyć za podbicia i wygrane wojny, serie zwycięstw, ochronę przed farmieniem oraz wykup żyć komendą `/kupzycie`
* GH-2698 Dodano HP serca gildii - dopiero zbicie całego HP odbiera gildii życie, a serce odnawia punkty (`war.heart-lives`)
* GH-2701 Zaatakowanie gildii wymaga teraz wypowiedzenia jej wojny

### Tereny i ochrona
* GH-2695 Rozdzielono `explosion-control` na teren gildii i resztę świata, dodano też blokowanie wlewania się cieczy na teren
* GH-2602 Rozszerzono ochronę interakcji wokół serca
* GH-2704 Wiadra przy sercu gildii sprawdzane są teraz po tagach materiałów
* GH-2707 NPC z pluginów typu Citizens czy Shopkeepers nie są już traktowane jak gracze

### Rankingi i statystyki
* GH-1365 Na początku walki gracz widzi przewidywaną zmianę punktów
* GH-1961 Dodano ochronę przed farmieniem rankingu na alt-kontach członków gildii
* GH-2711 Komendy statystyk przyjmują teraz `add` i `remove`

### Placeholdery i tablista
* GH-2077 Dodano placeholder `G-MEMBER-X` z posortowaną listą członków gildii
* GH-2709 Dodano podtypy placeholderów topek oraz obsługę graczy offline w PlaceholderAPI
* GH-2668 Przyspieszono wysyłanie tablisty i ograniczono wyciek pamięci
* GH-2681 Naprawiono renderowanie tablisty osobno dla każdego gracza
* GH-2688 Dodano placeholdery `PTOP` i `GTOP` do domyślnych konfiguracji
* GH-2691 Poprawiono formatowanie placeholderów rankingu w PlaceholderAPI
* GH-2689 Naprawiono placeholdery na czacie

### Przedmioty i GUI
* GH-2713 W `/ga items` można teraz ustawić wiele typów przedmiotów
* GH-2696 Udostępniono konfigurację przedmiotów w API pluginu
* GH-2674 Dodano przycisk powrotu w GUI
* GH-2714, GH-2726 Usunięto domyślną kursywę z nazw i opisów przedmiotów

### Dane
* GH-2700 Przepisano automatyczne migracje schematu bazy SQL
* GH-2728 Dodano wykrywanie konfliktów nicków i UUID przy starcie (`prevent-uuid-conflicts`, domyślnie wyłączone)

### Poprawki
* GH-2694 Naprawiono pobieranie treści wiadomości na czacie gildyjnym
* GH-2705 Nametagi korzystają teraz z gildii obserwowanego gracza, a nie patrzącego
* GH-2721 Porzucono starą metodę kolorowania ampersandem
* Wiele pomniejszych poprawek w ramach istniejących funkcjonalności

### Dokumentacja
* GH-2719 Powstała nowa strona dokumentacji, po polsku i angielsku: [funnyguilds.dzikoysk.net](https://funnyguilds.dzikoysk.net)

**Wymagania:**
* Paper 1.21.5 - 1.21.11, 26.1 - 26.3
* Java 21+

**Pobierz:**
* FunnyGuilds 5.0.0 - plik .jar w załącznikach tego wydania (Assets)
* [FunnyGuilds Nightly 5.x](https://ci.insertt.dev/job/FunnyGuilds/job/5.x/)
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
    <version>5.0.0</version>
    <!-- Snapshots (Nightly) -->
    <version>5.0.1-SNAPSHOT</version>
    <classifier>all</classifier>
    <scope>provided</scope>
</dependency>
```
