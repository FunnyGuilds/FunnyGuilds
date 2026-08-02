# FunnyGuilds Tribute (#4.3.0)

- **Tag:** 4.3.0
- **Published:** 2018-04-02T13:24:53Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.3.0

---

**Zmiany:**
* **Od teraz plugin jest objęty licencją [The Apache License, Version 2.0](https://github.com/FunnyGuilds/FunnyGuilds/blob/master/LICENSE)**
* **Struktura projektu została przemodyfikowana** - od teraz jest czytelniejsza, niestety wiąże się to z nową lokalizacją klas, więc wszystkich deweloperów zachęcamy do aktualizowania swoich dodatków
* **Od teraz projekt jest budowany na następujących wersjach JDK**:
   * 8 (stable)
   * 9 (experimental, [cykl życia został zakończony](http://www.oracle.com/technetwork/java/eol-135779.html))
   * 10 (stable)
   * 11 (experimental, nie ma oficjalnej, stabilnej wersji)
* Zaktualizowano biblioteki w projekcie
* Zwiększenie wydajności i stabilności zapisu danych do bazy danych MySQL (+ od teraz `poolSize` jest generowany w sposób automatyczny, zgodnie ze specyfikacją maszyny na której serwer jest uruchomiony)
* Usprawnienie systemu wielowątkowości, od teraz działa ciut wydajniej i lepiej
* Dodano wsparcie dla pluginu [BungeeTabListPlus](https://www.spigotmc.org/resources/bungeetablistplus.313/), szczegóły w konfiguracji
* Dodano limit osób asystujących
* Dodano możliwość wyłączenia ochrony gildii przez TNT, gdy ona ma tzw. `okres ochronny`
* Dużo poprawek, stabilizujących ogólne działanie pluginu
* Rozpoczęto przygotowania nad nowym systemem zapisu

*Przypominam również o tym, że jako społeczność **NIE MUSICIE** czekać na stabilne wydanie pluginu FunnyGuilds - wystarczy że użyjecie do tego dedykowanego narzędzia o wdzięcznej nazwie [FunnyCreator](https://github.com/FunnyGuilds/FunnyCreator), dzięki czemu możecie się cieszyć wersjami deweloperskimi pluginu w **dowolnym miejscu i czasie**.*

**Wymagania:**
* Spigot 1.8.8+
* Java 8+

**Discord:**
* [Discord | FunnyGuilds](https://discord.gg/CYvyq3u)

**Pobierz:**
* [FunnyGuilds 4.3.0](https://repo.panda-lang.org/net/dzikoysk/funnyguilds/4.3.0/funnyguilds-4.3.0.jar)
