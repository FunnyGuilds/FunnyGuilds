# FunnyGuilds Snowdrop (#4.10.0)

- **Tag:** 4.10.0
- **Published:** 2022-03-20T14:54:14Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.10.0
- **Jar:** FunnyGuilds-4.10.0.jar
- **Size:** 4542488

---

**Discord:**
* [Discord | Bookkity ~ O wszystkim 💕](https://discord.gg/CYvyq3u)

---

### ⚠️ Przed aktualizacją pluginu na serwerze zalecane jest zrobienie kopii plików konfiguracyjnych. 
**Może być konieczne przeniesienie starych wartości w konfiguracji pod nowe klucze. W przypadku aktualizacji do tej wersji (4.10.0) trzeba zrobić to manualnie. Zalecamy skorzystanie z nowszych wersji (4.10.1+), gdzie konfiguracja jest aktualizowana automatycznie.**

---

Ta wersja zawiera wiele zmian w kodzie, dlatego zachęcamy twórców do aktualizacji swoich dodatków 🙂 Wprowadzono 239 zmian w ramach 640 plików: https://github.com/FunnyGuilds/FunnyGuilds/compare/4.9.7...4.10.0

**Dodano:**
- GH-1584 ad6997b GH-1861 Wsparcie 1.17.1, 1.18.1 i 1.18.2
- GH-736 Zmienne dot. największej ilości zabójstw, śmierci itd.
- GH-1610 Animowana tablista (oraz przeniesienie jej konfiguracji do tablist.yml)
- GH-1661 Hologramy nad sercem gildii (wsparcie HolographcDisplays)
- GH-1370 Możliwość włączenia stawiania niektórych bloków na terenach gildii
- GH-1593 Możliwość włączenia stawiania TNT od i do danego poziomu
- GH-1480 Wsparcie niektórych pluginów na vanisha i uznawanie graczy na vanishu jako offline
- GH-1812 Wsparcie PlaceholderAPI i MVdWPlaceholderAPI w czatach gildyjnych
- GH-1873 Formatowanie wielkości zmiany punktów przy zabójstwie
- GH-1773 Placeholder wyświetlający życia gildii w formie serduszek

**Zmiany:**
- GH-1552 Naprawienie kryształu gildii, który nie pojawiał się w niektórych sytuacjach
- GH-1870 Usprawnienie tabowania graczy i gildii w komendach
- GH-1849 Ignorowanie wielkości liter tagu w komendach typu np. /info, /dolacz
- GH-1863 Usprawnienie zapisywania lokalizacji bazy gildii - można ją teraz ustawiać precyzyjniej
- GH-1834 Usprawnienie systemu sprawdzającego dostępność aktualizacji
- GH-1826 Usprawnienie systemu wysyłania wiadomości - puste wiadomości nie są teraz wysyłane

**Dla developerów:**
- 765f578 Przeniesienie niektórych paczek
- GH-1816 Usprawnienie kodu używając Option
- 7d6f3a9 Rework systemu rankingu
- b53aaa3 Dodanie GuildManagera i usprawnienia w UserManagerze
- GH-1733 Usprawnienie systemu regionów gildii (m.in. przez dodanie RegionManagera)
- 9addc17 Usprawnienie event'ów dotyczących rankingu i dodanie AsistsChangeEvent
- GH-1769 Nowy event GuildCreateEvent
- GH-1819 Nowy event GuildConquerEvent
- GH-1866 Dodanie UserProfile do API usera. (Metoda User#getPlayer  `@Deprecated`)
- Usprawnienie i dodanie Javadocsów oraz oznaczenie przestarzałych metod jako `@Deprecated`

**Inne:**
- Poprawki i usprawnienia w kodzie (d75bc4243ae2584ff25a0f5a027693cb958cac0f, eeb7063523d184f337288f0bab65d18464768864, 1671cdd2702938397faa24262fada436391c1af8, 078da1e35d1af83c9492aca4c53c23a1d61f8939, 879b3ea42d142351ee6e74f19b146089b7adea66, 790a01cf52acde7c3fa41381883640c238b01827, ca034f9f5f73c48f43d4951c1a8ad71223932279, ab114405672aab294dad9a281155ac7a05165133, 89e819bee9d548e197e46a0a1b0c1b0eee1abd20, f71f06321876b957be933794a4d2274357da4601, f342156e1f09e027ee900ebe8732ebef2ba699a4, 5461a2a55b5a626167386a4af2ceb01b0fc87012, 432d3ce40bb30627e88b610e5e80dbf1c49005bb, a0f831b1cb2b0feafd2af71e2935220b2f991e0f, b19f35814ea364468d63a51a3801da01940e33e2, a20ddd4e4564611b2657e9eee063bfc146e1ca51, 458bb81f74a1906c95004048b124ee3f6da9b827, db17a9964b382e253cd3e346dc914c597fb70e77, e8cffb898f9f8c2c26085b9664671ac0ca800812, fb955e1924fd1d89188b005cc206673a85ff9c93, 39e9c918527240725a4d725889acf5c1793ca5cf, bd9d2f69c0142755e8a122d71822d03d07935038, b6ee593cbfe82b6c8fec3899881290ef7a3b84cb, 32957ac578bb895f4e432bf054c15ced811031e7, f00ab34f5911259812ba65d7e226f728e2958ed5, fd10a6932959ad505a292128345e321ef7407e06, 16d964c49bc42e632ab3f7e7efd9431e14d62689, c44f6358df12c4f4b311ed7a84ac020de504ea40, 82370a9f4664b832ab95de9dc3e0186dad6309e7, 9d11646d673ee3821e8751e280ddf0731b76af3f, 3a33c1c4dc98729260fe1eb1d261a3825745f6db, 6e7da922303e3f0c5cb2fd9aecc26abe00490528, 0ad11a14e2c91dd08fbc05537363db6e9df2deab, 7c85fdcd7e762c0e6acfa4f34fe360e73d702a08, cd9efdf2e830feaf67317341afd541999044afa1, 81aec1e219048c90e849baca45ef48ed74012ea4, 445f933d19b7d39456fd99519675db11a4362de3)
- Bump zależności

**Wymagania:**
* Spigot 1.8.8/1.9.4/1.10.2/1.11.2/1.12.2/1.13.2/1.14.4/1.15.2/1.16.5/1.17.1/1.18.1/1.18.2
* Java 8+

**Pobierz:**
* [FunnyGuilds 4.10.0](https://ci.insertt.dev/job/FunnyGuilds/job/master/529/artifact/plugin/build/libs/FunnyGuilds%204.10.0%20(MC%201.8-1.18).jar)
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
    <version>4.10.0</version>
    <scope>provided</scope>
</dependency>
```
