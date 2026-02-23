# Plan: System przedmiotów gildii + GUI

## Pliki konfiguracyjne

### `config.yml` — przedmioty i sety
- Globalna biblioteka przedmiotów pod kluczami (np. `diamonds`, `founders-sword`)
- Każdy item: `material`, `amount`, `name`, `lore`, `enchants`, `flags`, `custom-model-data`, `skull-owner`, `armor-color` (wszystko MiniMessage)
- Sety (`default`, `vip`, `premium`, własne) referencjonują itemy z biblioteki przez klucz — dzięki temu vip i default mogą współdzielić ten sam item, różniąc się tylko `amount`
- Set może też nadpisać dowolne pole inline bez użycia biblioteki
- Każdy set ma: `permission` (null = wszyscy), `priority`, flagi wymagań (`items-enabled`, `money-enabled`, `experience-enabled`, `rank-enabled`), `required-money`, `required-experience`, `required-rank`
- Bypass admina: `admin-bypass-permission: funnyguilds.items.admin.bypass`

### `gui.yml` — wygląd interfejsu
- `enabled: true/false` — globalne włączenie GUI
- Tytuł, liczba rzędów, wzór slotów (`#` filler, `I` item, `S` summary, `C` close)
- Mapowanie kluczy itemów na sloty (auto lub manualne przez klucz → numer slotu)
- Wygląd fillerów, close-itemu, summary-itemu
- Formaty wyświetlania itemów: prefix i dodatkowe lore gdy gracz **ma** / **nie ma** wystarczająco
- Nadpisania per-set (tytuł, filler) pod `per-set-gui.{set-key}`

### `pl.yml` — wiadomości
- Wszystkie wiadomości GUI, statusy, linie wymagań w MiniMessage
- Wiadomości tekstowe gdy GUI wyłączone lub items-disabled (tylko money/exp/rank)
- Wiadomości komend admina

---

## Logika działania

### Wybór setu dla gracza
Sety posortowane malejąco po `priority` → pierwszy, do którego gracz ma permisję. Wynik cachowany przez **Caffeine**.

### Przy `/guild create`
1. Gracz ma `admin-bypass-permission` → pomijamy całość, zakładamy gildię
2. Wybieramy set gracza
3. Jeśli `items-enabled: false` (lub GUI `enabled: false`) → wiadomość tekstowa z brakującymi wymaganiami (money/exp/rank)
4. Jeśli `items-enabled: true` i GUI enabled → otwieramy GUI z itemami i summary

### Sprawdzanie itemów
Dla każdego wymaganego itemu liczymy osobno:
- `{INV}` — ekwipunek gracza
- `{ENDER}` — ender chest gracza
- `{TOTAL}` — suma obu

### GUI (InventoryFrameworkAPI v0.11.6)

Budowany na `ChestGui`. Poniżej konkretne klasy IF i do czego służą:

**`PatternPane`** — wzór z `gui.yml` (`#I#I#S#C` itp.) trafia dosłownie do `PatternPane`. IF sam rozdziela znaki na pozycje slotów, zero własnego parsowania numerów. Każdemu znakowi przypisujemy `ItemProvider` — `#` dostaje filler, `I` item z setu, `S` summary, `C` close.

**`OutlinePane`** — ramka GUI jedną linijką zamiast ręcznego wypełniania slotów w pętli. Owijamy cały `ChestGui` outlinepane'em z filler-itemem, zanim nałożymy `PatternPane` ze środkiem.

**`PaginatedPane` + paging buttons** — gdy set ma więcej itemów niż slotów `I` w jednym ekranie (np. premium z 10+ itemami), zamiast rozwalać layout robimy paginację. IF 0.11.6 ma opcję `always keep backwards/forwards buttons visible` — przyciski stron widoczne zawsze, nawet na pierwszej/ostatniej stronie, żeby gracz wiedział że może przełączać.

**Live refresh** — gracz otwiera GUI, widzi `✘` przy diamentach, idzie do skrzynki obok, wkłada diamenty. Scheduler co 20 ticków wywołuje `gui.update()` — `✘` zamienia się na `✔` bez zamykania okna. IF obsługuje update w locie bez przebudowywania całego GUI.

**`ToggleButton`** — przycisk przełączający widok między "wszystkie itemy z setu" a "tylko brakujące". Przydatne przy dużych setach żeby gracz szybko widział co jeszcze musi zebrać. IF ma natywne `ToggleItem` obsługujące dwa stany z własnym `ItemProvider` per stan.

**Placeholdery itemów w GUI:**

| Placeholder | Wartość |
|---|---|
| `{REQUIRED}` | Wymagana ilość |
| `{INV}` | Ilość w ekwipunku |
| `{ENDER}` | Ilość w ender chest |
| `{TOTAL}` | Suma inv + ender |
| `{MISSING}` | Brakująca ilość (0 jeśli ma) |

**Slot `S` (summary)** — status gracza, linie money/exp/rank. Linia pomijana jeśli wymaganie wyłączone w secie. Odświeżany razem z live refresh.

**Slot `T` (toggle)** — nowy znak w patternе, przełącznik widoku all/missing.

**Slot `C` (close)** — `onClick → gui.close()`

---

## Komendy
- `/ga items <nick> [set]` — podgląd wymagań gracza (admin)

---

---

## Podgląd konfiguracji

### `config.yml` (fragment — sekcja itemów gildii)

```yaml
# =======================================
# BIBLIOTEKA PRZEDMIOTÓW
# =======================================
# Definicje reużywalne między setami - zmieniasz raz, działa wszędzie
guild-item-library:
  diamonds:
    material: DIAMOND
    name: null          # null = domyślna nazwa z Minecrafta
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null   # format: R_G_B

  emeralds:
    material: EMERALD
    name: null
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null

  iron:
    material: IRON_INGOT
    name: null
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null

  founders-sword:
    material: DIAMOND_SWORD
    name: '<gradient:gold:yellow><bold>Miecz Założyciela</bold></gradient>'
    lore:
      - ''
      - '<gray>Legendarny miecz wymagany'
      - '<gray>do założenia gildii premium.'
      - ''
      - '<yellow>✦ <white>Ostrość V'
    enchants:
      - sharpness:5
      - unbreaking:3
    flags:
      - HIDE_ATTRIBUTES
    custom-model-data: null
    skull-owner: null
    armor-color: null

# =======================================
# SETY PRZEDMIOTÓW
# =======================================
guild-item-sets:
  default:
    permission: null    # null = każdy gracz bez specjalnej permisji
    priority: 0         # wyższy = sprawdzany wcześniej
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: true
      rank-enabled: true
    items:
      diamonds:
        ref: diamonds   # klucz z guild-item-library
        amount: 64
      emeralds:
        ref: emeralds
        amount: 64
      iron:
        ref: iron
        amount: 64
    required-experience: 30
    required-money: 10000.0
    required-rank: 1000

  vip:
    permission: funnyguilds.items.vip
    priority: 10
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: false  # VIP nie płaci kasy
      rank-enabled: true
    items:
      diamonds:
        ref: diamonds
        amount: 32      # te same diamenty co default, tylko połowa
      emeralds:
        ref: emeralds
        amount: 32
      iron:
        ref: iron
        amount: 32
    required-experience: 20
    required-money: 0.0
    required-rank: 800

  premium:
    permission: funnyguilds.items.premium
    priority: 20
    requirements:
      items-enabled: true
      experience-enabled: false
      money-enabled: false
      rank-enabled: false
    items:
      founders-sword:
        ref: founders-sword
        amount: 1
      # Przykład inline (bez ref) - unikatowy item tylko w tym secie
      guild-scroll:
        material: PAPER
        amount: 5
        name: '<light_purple>Zwój Gildii</light_purple>'
        lore:
          - '<gray>Starożytny zwój...'
        enchants: null
        flags: null
        custom-model-data: 1337
        skull-owner: null
        armor-color: null
    required-experience: 0
    required-money: 0.0
    required-rank: 0

# =======================================
# BYPASS ADMINISTRACYJNY
# =======================================
admin-items-bypass-permission: funnyguilds.items.admin.bypass
```

---

### `gui.yml`

```yaml
# Czy GUI jest włączone globalnie
# false = przy brakujących wymaganiach wyświetla tylko wiadomość tekstową
enabled: true

gui:
  title: '<dark_gray><bold>Przedmioty na gildie <gray>({SET})'
  rows: 6

  # Wzór GUI (9 znaków × rows rzędów)
  # # = filler   I = slot na item   S = summary   C = zamknij   T = toggle (all/missing)
  pattern:
    - '#########'
    - '#I#I#I#I#'
    - '#########'
    - '#I#I#I#I#'
    - '#########'
    - '##T#S###C'

  item-slots:
    # true = itemy wstawiane kolejno w sloty 'I' (w kolejności z config.yml)
    auto: true
    # Manualne mapowanie klucz_itemu -> numer_slotu (używane gdy auto: false)
    slots:
      diamonds: 10
      emeralds: 12
      iron: 14

  toggle-item:
    all:
      material: LIME_DYE
      name: '<green>Pokaż wszystkie'
      lore:
        - '<gray>Kliknij aby pokazać'
        - '<gray>tylko brakujące itemy'
    missing-only:
      material: RED_DYE
      name: '<red>Tylko brakujące'
      lore:
        - '<gray>Kliknij aby pokazać'
        - '<gray>wszystkie itemy'

  filler-item:
    material: BLACK_STAINED_GLASS_PANE
    name: ' '

  close-item:
    material: BARRIER
    name: '<red><bold>Zamknij'
    lore:
      - '<gray>Kliknij aby zamknąć'

  summary-item:
    material: BOOK
    name: '<yellow><bold>Podsumowanie'
    lore:
      - '<gray>Twój status:'
      - ''
      - '{MONEY_LINE}'
      - '{EXPERIENCE_LINE}'
      - '{RANK_LINE}'
      - ''
      - '{STATUS}'

# Wygląd itemów w GUI
item-display:
  has-enough:
    glow: false
    name-prefix: '<green>✔ '
    additional-lore:
      - ''
      - '<gray>Wymagane: <green>{REQUIRED}'
      - '<gray>Ekwipunek: <green>{INV}'
      - '<gray>Ender chest: <green>{ENDER}'
      - '<gray>Łącznie: <green>{TOTAL}'

  missing:
    glow: false
    name-prefix: '<red>✘ '
    additional-lore:
      - ''
      - '<gray>Wymagane: <red>{REQUIRED}'
      - '<gray>Brakuje: <red>{MISSING}'
      - ''
      - '<red>Ekwipunek: <white>{INV}'
      - '<red>Ender chest: <white>{ENDER}'
      - '<red>Łącznie: <white>{TOTAL}'

# Nadpisania per-set (opcjonalne — brak wpisu = używa globalnego)
per-set-gui:
  vip:
    title: '<gold><bold>VIP <dark_gray>- Przedmioty na gildie'
    filler-item:
      material: ORANGE_STAINED_GLASS_PANE
      name: ' '
  premium:
    title: '<aqua><bold>Premium <dark_gray>- Przedmioty na gildie'
    filler-item:
      material: LIGHT_BLUE_STAINED_GLASS_PANE
      name: ' '
```

---

### `pl.yml` (nowe wiadomości — sekcja items/gui)

```yaml
guild-items:
  # Admin bypass
  admin-bypass: '<yellow>⚡ Posiadasz uprawnienia admina — wymagania pominięte.'

  # Wiadomość tekstowa gdy GUI wyłączone lub items-enabled: false
  requirements-not-met-header: '<red>✘ Nie spełniasz wymagań do założenia gildii!'
  requirement-money: '<gray>  Pieniądze: <red>{CURRENT} <gray>/ <white>{REQUIRED}'
  requirement-experience: '<gray>  Doświadczenie: <red>{CURRENT} <gray>/ <white>{REQUIRED}'
  requirement-rank: '<gray>  Ranking: <red>{CURRENT} <gray>/ <white>{REQUIRED}'
  requirement-items-header: '<gray>  Brakujące przedmioty:'
  requirement-item-line: '<gray>    • <white>{ITEM}: <red>{CURRENT} <gray>/ <white>{REQUIRED}'

  # Komendy admina
  admin-command-usage: '<red>Użycie: /ga items <nick> [set]'
  admin-command-set-not-found: '<red>Set <white>{SET}<red> nie istnieje.'
  admin-command-player-not-found: '<red>Gracz <white>{PLAYER}<red> nie został znaleziony.'
  admin-bypass-grant: '<green>Gracz <white>{PLAYER}<green> otrzymał jednorazowy bypass wymagań.'

  # GUI — summary item
  gui-status-ready: '<green>✔ Możesz założyć gildię!'
  gui-status-not-ready: '<red>✘ Brakuje Ci wymagań'
  gui-money-line: '<gray>Pieniądze: <green>{CURRENT} <gray>/ <white>{REQUIRED}'
  gui-experience-line: '<gray>Doświadczenie: <green>{CURRENT} <gray>/ <white>{REQUIRED}'
  gui-rank-line: '<gray>Ranking: <green>{CURRENT} <gray>/ <white>{REQUIRED}'
  # Wyświetlane gdy dane wymaganie jest wyłączone w secie
  gui-requirement-disabled: '<dark_gray>  ✦ {TYPE} — nie wymagane'
```

---

## Do uzgodnienia przed implementacją
- Czy przy `items-enabled: false` + wszystkie wymagania false → gildię można założyć bez niczego?
- Czy summary ma pokazywać linie wyłączonych wymagań (np. *"money — nie wymagane"*) czy je ukrywać?