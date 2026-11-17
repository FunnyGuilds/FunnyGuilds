# Rankingi aktualizowane po zmianie statystyki

[English](en/ranking-optimization.md)

Rankingi graczy i gildii są aktualizowane po zmianie danych. Nie ma okresowej przebudowy wszystkich topów, a opcja `ranking-update-interval` została usunięta.

## Zachowanie

- Pierwszy odczyt kolejności danego topu sortuje jego wpisy. Podczas wczytywania danych nie sortujemy po każdym przypisaniu licznika.
- Kolejna zmiana przestawia pojedynczy wpis: wyszukanie miejsca wymaga O(log n) porównań, a przesunięcie elementów listy O(n). Odczyt pozycji korzysta z wyszukiwania binarnego.
- Zmiana statystyki członka odświeża wyłącznie odpowiadające jej, już używane agregaty jego gildii. Dołączenie, odejście, zmiana nazwy i usunięcie również aktualizują odpowiednie wpisy.
- Najwyższa wartość zajmuje pozycję 1. Remisy rozstrzyga nazwa bez uwzględniania wielkości liter, a następnie UUID.
- Wykluczeni gracze i gildie poniżej `guild-min-members` zachowują wartości statystyk, ale nie mają pozycji. Pusta gildia nie uczestniczy w topie.
- `rank-start` nadal określa punkty nowego gracza. Tradycyjne średnie liczników gildii pozostają całkowite. KDR/KDA gildii nadal rozróżniają iloraz sum liczników od średniej indywidualnych ilorazów.

Listy `top.enabled-user-tops` i `top.enabled-guild-tops` są zachowane do odczytu starej konfiguracji, ale nie ograniczają już dostępnych typów. Nie trzeba ich edytować po dodaniu nowej statystyki. `top.enable-legacy-placeholders` nadal steruje obsługą `{PTOP-1}` i `{GTOP-1}`.

Przy włączonym `skip-privileged-players-in-rank-positions` sprawdzanie uprawnienia `funnyguilds.ranking.exempt` odbywa się na wątku serwera. Co 40 ticków sprawdzani są gracze online oraz do 32 kolejnych graczy offline. Duża baza graczy offline nie powoduje jednego ogromnego przebiegu sprawdzeń Vault w tym samym ticku. Zmiana wykluczenia offline może być widoczna po dojściu do gracza w tej kolejce; przeładowanie konfiguracji odświeża wszystkich. To kontrola uprawnień, bez okresowego sortowania topów.

## Dodawanie statystyk przez API

Wystarcza deklaracja klucza; miejsce, które nalicza statystykę, zapisuje jej wartość przez serwis:

```java
public static final StatKey<Integer> ARENA_WINS = StatKey.integerStatKey("arena_wins");

RankService ranks = plugin.getRankService();
ranks.changeUserStatValue(user, ARENA_WINS, current -> current + 1);
ranks.getUserTopPosition(user, ARENA_WINS);
ranks.getGuildStatValue(guild, ARENA_WINS, GuildStatAggregationType.TOTAL_INT);
```

Klucz automatycznie udostępnia topy, pozycje, agregaty gildii i zapis danych. Nie wymaga zmiany komparatorów, list w konfiguracji ani kolejnej kolumny SQL. Nowe liczniki zaczynają od zera. Dla nowych statystyk średnia gildii jest liczbą zmiennoprzecinkową.

Dostępne są klucze `Integer` i `Double`, także z własną przestrzenią nazw, np. `StatKey.doubleStatKey(Key.key("arena", "balance"))`. Jeden identyfikator nie może mieć dwóch różnych typów; wartości `NaN` i nieskończone są odrzucane. Własną strategię agregacji można utworzyć przez `GuildStatAggregationType.create(...)`.

Przykładowe placeholdery:

| Wartość | Placeholder |
| --- | --- |
| Pierwszy gracz | `{PTOP-arena_wins-1}` |
| Pozycja gracza | `{POSITION-arena_wins}` |
| Pierwsza gildia według sumy | `{GTOP-arena_wins-1}` |
| Pierwsza gildia według średniej | `{GTOP-avg_arena_wins-1}` |
| Pozycja gildii | `{G-POSITION-arena_wins}` |
| Statystyka z przestrzenią nazw | `{PTOP-arena:balance-1}` |

Dotychczasowe formaty `top.format.ptop`, `gtop`, `{VALUE}` i `{VALUE-FORMAT}` pozostają dostępne. Nieznana statystyka i brak wpisu używają skonfigurowanego komunikatu zastępczego. Placeholder gildii `{POINTS}` nadal oznacza średnią; `{TOTAL-POINTS}` oznacza sumę.

Dodatkowe statystyki są zapisywane jako JSON z identyfikatorem, typem i wartością, w polu `stats` pliku użytkownika albo kolumnie `stats TEXT` tabeli użytkowników. SQL używa istniejącego mechanizmu dodawania brakujących kolumn. Stare dane bez tego pola nadal się wczytują. Dotychczasowe kolumny liczników pozostają źródłem ich wartości; wyliczone KDR/KDA i agregaty gildii nie są zapisywane jako osobne kopie.

Stare `Rank`, `UserRank` i `GuildRank` są przestarzałymi adapterami. Do nowych integracji należy używać `RankService`. Usunięte managery rankingów nie stanowią już API. Metody `queueSetUserStatValue` i `queueChangeUserStatValue` zwracają `CompletableFuture`, także w przypadku błędu aktualizacji lub odrzucenia zadania. Przy zamykaniu serwisu oczekujące zmiany są wykonywane przed końcowym zapisem danych.

Integracja zmieniająca uprawnienia może wywołać `refreshUser(user)` na wątku serwera, aby natychmiast odświeżyć wykluczenie gracza.

## Test losowy

`RankServiceRandomizedTest` utrzymuje niezależny model wartości i członkostwa. Przez zadany czas wykonuje losowe zapisy, zmiany gildii i nazw, wykluczenia, usuwanie i ponowne tworzenie obiektów. Co 32 operacje porównuje wszystkie wartości, kolejność i pozycje z rankingami obliczonymi od zera. Uwzględnia liczniki wbudowane, KDR/KDA i nowe statystyki `Integer`/`Double`.

Domyślnie test działa 3 sekundy, wykonuje co najmniej 2000 operacji i używa seeda `2596`. Raport wypisuje rzeczywistą liczbę operacji. Awaria podaje seed, numer operacji i ostatnie działania. Dłuższe uruchomienie:

```shell
./gradlew :plugin:test --tests "*RankServiceRandomizedTest" -Dfunnyguilds.rank.stressMillis=30000 -Dfunnyguilds.rank.stressSeed=20261117
```

Pozostałe testy obejmują współbieżne aktualizacje, brak ponownego pełnego sortowania, zapis/odczyt plikowy, dane przekazywane do SQL i odczyt wiersza SQL, kolejkę, wykluczenia oraz placeholdery. Testy SQL używają atrapy JDBC, a nie działającego serwera MySQL/MariaDB.
