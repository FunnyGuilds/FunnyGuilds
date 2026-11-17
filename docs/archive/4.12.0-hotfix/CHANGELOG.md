# FunnyGuilds Snowdrop (#4.12.0)

- **Tag:** 4.12.0-hotfix
- **Published:** 2023-03-10T11:59:45Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.12.0-hotfix
- **Jar:** FunnyGuilds-4.12.0-hotfix.jar
- **Size:** 6508740

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

### ⚠️ UWAGA
- W 4.12.0 został wprowadzony nowy system wiadomości. Mimo, że wszystkie wartości powinny się automatycznie przenieść to zalecamy zrobić **kopię zapasową** całej konfiguracji przed dokonaniem aktualizacji pluginu.
- FunnyGuilds w wersji 4.12.0 jest **ostatnią** wspieraną wersją pluginu z obsługą wersji Minecrafta 1.8-1.15. Od następnej wersji będą wspierane **tylko i wyłącznie** wersje 1.16+, **bez żadnych wyjątków**.
- FunnyGuilds w wersji 4.12.0 jest również **ostatnią** wersją pluginu w wersji `4.X` posiadającą większe zmiany. W najbliższym czasie planowane jest rozpoczęcie prac nad wersją `5.X`, która będzie wprowadzało większe zmiany m.in w konfiguracji (nie będzie kompatybilna z konfiguracją `4.X`). Do czasu wydania `5.X` do wersji `4.X` będą dodawane **tylko i wyłącznie** bug fixy w formie wydań [Nightly](https://ci.insertt.dev/job/FunnyGuilds/job/4.x).

**Zmiany:**
- GH-1837 Przepisanie systemu wiadomości i dodanie wsparcia dla tłumaczeń
- GH-2301 Zoptymalizowanie szukania regionów gildii po lokalizacji przez szukanie ich po chunkach zamiast iterując po całej liście
- GH-2295 GH-2277 Zoptymalizowanie automatycznego odświeżania nametagów/dummy oraz usprawnienia ich kodu
- GH-2275 Naprawienie częstotliwości automatycznego odświeżania nametagów/dummy
- GH-2293 Naprawienie wyświetlanie czasu do zakończenia ochrony/ważności/etc gildii
- GH-2304 Naprawienie problemów związanych z wyświetlaniem tablisty na 1.19.3 podczas używania *ViaVersion*

**Dla developerów:**
- GH-2329 Wymienienie metody Region#getEnlarge na #getEnlargementLevel, przeniesienie metod od zmiany środka i poziomu powiększenia gildii do RegionManager'a (#moveRegionCenter oraz #changeEnlargementLevel) oraz usunięcie dostępu do metody Region#setSize
- GH-2327 Poprawne wywoływanie eventu `GuildRegionLeaveEvent` przy teleportacji poza jej teren

**Inne:**
- Poprawki i usprawnienia w kodzie (df80d1de26b5b8f689c0b17c9735af594d4bb90b, 6a4c6d4be6eb5b689636786d803acb1dcf0250b7, f9fc75635bee5f1c20db0e81998f38283268e803, e320f28e67abdde1ff083025f0526f7e984a5799, 50ae44087cec8e01fa669b76c41828997a6edc72, b2f62a6c0846c01f64cf20280592114dc9e13bff)
- Bump zależności

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.2/1.19.2/1.19.3
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.12.0](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/4/artifact/plugin/build/libs/FunnyGuilds%204.12.1-SNAPSHOT.1681%20(MC%201.8-1.19).jar)
* [FunnyGuilds Nightly 4.0](https://ci.insertt.dev/job/FunnyGuilds/job/4.x/)
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
    <!-- Release -->
    <version>4.12.0</version>
    <!-- Snapshots (Nightly) -->
    <version>4.12.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

