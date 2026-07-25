# FunnyGuilds Tribute (#4.7.0)

- **Tag:** 4.7.0
- **Published:** 2020-05-03T13:48:37Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/4.7.0

---

**Zmiany:**
- GH-1117 Od teraz można użyć specjalnego znaku `*` w sekcji `explode-materials`, dzięki któremu każdy blok będzie zaliczany do zniszczenia przy kalkulacjach wybuchów.
- GH-1097 Dodano możliwość wykluczenia graczy z uprawnieniem `funnyguilds.ranking.exempt` z obliczeń rankingu.
- GH-1107 Dodano uprawnienie `funnyguilds.vip.base`, które pozwala na teleportowanie się do bazy gildii bez zdefiniowanych przedmiotów.
- Naprawiono błąd z niepoprawną obsługą użytego typu bazy danych w konfiguracji.
- Naprawiono błąd związany z  tworzeniem/usuwaniem gildii.
- Poprawiono błąd związane z wielowątkowością przy zarządzaniu rankingami.
- Zmiany optymalizacyjne, które wpływają na obliczanie wartości zmiennych listy graczy i nie tylko.
- GH-1149 Dodanie wsparcia dla 1.15
- GH-1225 Dodanie wsparcia flag do parsera przedmiotów
- GH-1135 Dodanie wsparcia dla `*` w sekcji `explode-materials`
- GH-1150 Pominięto ładowanie użytkowników z nieprawidłowymi nickami
- GH-1155 Pominięto przetwarzanie gracza jeśli jest nullem
- b3e71fc Usunięcie nadmiernego cache użytkowników
- GH-1159 Poprawiono ładowanie użytkowników gdy wersja UUID to 2
- 8f03703 Poprawka aktualizatora
- GH-1187 Poprawka systemu wyliczającego ranking
- GH-1227 Poprawka zbugowanych bloków
- GH-1142, GH-1152, GH-1153, GH-1154, GH-1157, GH-1158, GH-1169, GH-1174, GH-1179, GH-1182, GH-1186, GH-1195, GH-1202, GH-1203, GH-1207, GH-1212, GH-1214, GH-1215, GH-1216, GH-1217, GH-1220, GH-1221, GH-1222 - Podbicie wersji zależności
- Oraz kilka/kilkanaście innych, mniejszych zmian (głównie poprawek błędów), które nie zostały tutaj uwzględnione.

Dzięki @error56 za przygotowanie listy zmian

**Uwagi:**
- GH-1218 Schematy na wersji 1.15 nie są jeszcze wspierane z uwagi na duże zmiany w pluginach WorldEdit i WorldGuard

**Wymagania:**
* Spigot 1.8.8 - 1.15
* Java 8+

**Discord:**
* [Discord | FunnyGuilds](https://discord.gg/CYvyq3u)

**Pobierz:**
* [FunnyGuilds 4.7.0](https://ci.insertt.dev/job/FunnyGuilds/job/master/124/artifact/target/FunnyGuilds%204.7.0%20(MC%201.8-1.15).jar)
