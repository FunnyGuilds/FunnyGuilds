# Plan: System przedmiotów gildii + GUI

## Pliki konfiguracyjne

### `items.yml` — biblioteka, sety i GUI w jednym miejscu
- Globalna biblioteka przedmiotów pod kluczami (np. `diamonds`, `founders-sword`) — definiujesz raz, reużywasz wszędzie
- Każdy item: `material`, `name`, `lore`, `enchants`, `flags`, `custom-model-data`, `skull-owner`, `armor-color` (wszystko MiniMessage)
- Sety (`default`, `vip`, `premium`, własne) referencjonują itemy z biblioteki przez klucz z ilością: `diamonds: 64` — żadnego `ref:`, żadnych inline itemów w setach
- Każdy set ma: `permission` (null = wszyscy), `priority`, flagi wymagań (`items-enabled`, `money-enabled`, `experience-enabled`, `rank-enabled`), `required-money`, `required-experience`, `required-rank`
- Sekcja `gui:` zawiera: `enabled`, `title`, `rows`, `pattern`, `items` (filler/toggle/summary/close), `item-display` (has-enough / missing), `per-set` (nadpisania per set)
- Bypass admina: `admin-items-bypass-permission: funnyguilds.items.admin.bypass`

### `pl.yml` — wiadomości
- Wszystkie wiadomości systemu itemów w MiniMessage
- Wiadomości tekstowe gdy GUI wyłączone lub `items-enabled: false` (tylko money/exp/rank)
- Wiadomości komend admina

---

## Logika działania

### Wybór setu dla gracza
Sety posortowane malejąco po `priority` → pierwszy, do którego gracz ma permisję. Wynik cachowany przez **Caffeine**.

### Przy `/zaloz`
1. Gracz ma `admin-items-bypass-permission` → pomijamy całość, zakładamy gildię od razu
2. Wybieramy set gracza (Caffeine cache)
3. Jeśli `items-enabled: false` lub `gui.enabled: false` → wiadomość tekstowa z brakującymi wymaganiami (money/exp/rank) z `pl.yml`
4. Jeśli `items-enabled: true` i `gui.enabled: true` → otwieramy GUI

### Sprawdzanie itemów
Dla każdego wymaganego itemu liczymy osobno:
- `{INV}` — ekwipunek gracza
- `{ENDER}` — ender chest gracza
- `{TOTAL}` — suma obu

---

## GUI (InventoryFrameworkAPI v0.11.6)

Budowany na `ChestGui`. Poniżej konkretne klasy IF i do czego służą:

**`PatternPane`** — wzór z `items.yml` (`{item-1}`, `{toggle:item-t}` itp.) trafia dosłownie do `PatternPane`. IF sam rozdziela tokeny na pozycje slotów, zero własnego parsowania numerów. Każdemu tokenowi przypisujemy `ItemProvider`.

**`OutlinePane`** — ramka GUI jedną linijką zamiast ręcznego wypełniania slotów w pętli. Owijamy cały `ChestGui` outlinepane'em z `item-x`, zanim nałożymy `PatternPane`.

**`PaginatedPane` + paging buttons** — gdy set ma więcej itemów niż slotów `{item-N}` w jednym ekranie, zamiast rozwalać layout robimy paginację. IF 0.11.6 ma opcję `always keep backwards/forwards buttons visible` — przyciski stron widoczne zawsze nawet na pierwszej/ostatniej stronie.

**Live refresh** — gracz otwiera GUI, widzi `✘` przy diamentach, idzie do skrzynki, wkłada diamenty. Scheduler co 20 ticków wywołuje `gui.update()` — `✘` zamienia się na `✔` bez zamykania okna.

**`ToggleButton`** — token `{toggle:item-t}` w patternie. Przełącza widok między "wszystkie itemy z setu" a "tylko brakujące". Dwa stany (`all` / `missing-only`) z osobnym `ItemProvider` per stan, zdefiniowane w `items.yml`.

**Placeholdery itemów z setu w GUI:**

| Placeholder | Wartość |
|---|---|
| `{REQUIRED}` | Wymagana ilość |
| `{INV}` | Ilość w ekwipunku |
| `{ENDER}` | Ilość w ender chest |
| `{TOTAL}` | Suma inv + ender |
| `{MISSING}` | Brakująca ilość (0 jeśli ma) |

**`{summary:item-s}`** — status gracza, linie money/exp/rank. Linia pomijana jeśli wymaganie wyłączone w secie. Odświeżany razem z live refresh.

**`{toggle:item-t}`** — przełącznik widoku all/missing, dwa stany.

**`{close:item-c}`** — `onClick → gui.close()`

**`{x}`** — filler, domyślnie `item-x` z `items.yml`, nadpisywalny per-set przez `per-set.{set}.items.item-x`.

---

## Komendy
- `/ga items <nick> [set]` — podgląd wymagań gracza (admin)

---

## Podgląd konfiguracji

### `items.yml`

```yaml
# =======================================
# BIBLIOTEKA PRZEDMIOTÓW
# =======================================
guild-item-library:
  diamonds:
    material: DIAMOND
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
      - '<gray>Legendarny miecz wymagany do założenia gildii premium.'
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
    permission: null
    priority: 0
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: true
      rank-enabled: true
    items:
      diamonds: 64       # klucz = guild-item-library, wartość = wymagana ilość
      emeralds: 64
      iron: 64
    required-experience: 30
    required-money: 10000.0
    required-rank: 1000

  vip:
    permission: funnyguilds.items.vip
    priority: 10
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: false
      rank-enabled: true
    items:
      diamonds: 32       # te same co default, tylko połowa
      emeralds: 32
      iron: 32
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
      founders-sword: 1
      diamonds: 16
    required-experience: 0
    required-money: 0.0
    required-rank: 0

# =======================================
# BYPASS ADMINISTRACYJNY
# =======================================
admin-items-bypass-permission: funnyguilds.items.admin.bypass

# =======================================
# GUI
# =======================================
gui:
  enabled: true
  title: '<dark_gray><bold>Przedmioty na gildie <gray>({SET})'
  rows: 6

  pattern:
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{item-1}{x}{item-2}{x}{item-3}{x}{item-4}{x}'
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{item-5}{x}{item-6}{x}{item-7}{x}{item-8}{x}'
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{x}{toggle:item-t}{x}{summary:item-s}{x}{x}{x}{close:item-c}'

  items:
    item-x:
      material: BLACK_STAINED_GLASS_PANE
      name: ' '

    item-t:
      all:
        material: LIME_DYE
        name: '<green><bold>Pokaż wszystkie'
      missing-only:
        material: RED_DYE
        name: '<red><bold>Tylko brakujące'

    item-s:
      material: BOOK
      name: '<yellow><bold>Podsumowanie'
      lore:
        - '{MONEY_LINE}'
        - '{EXPERIENCE_LINE}'
        - '{RANK_LINE}'
        - ''
        - '{STATUS}'

    item-c:
      material: BARRIER
      name: '<red><bold>Zamknij'

  item-display:
    has-enough:
      glow: false
      name-prefix: '<green>✔ '
      additional-lore:
        - '<gray>Wymagane: <green>{REQUIRED}'
        - '<gray>Łącznie: <green>{TOTAL} <dark_gray>({INV} + {ENDER})'

    missing:
      glow: false
      name-prefix: '<red>✘ '
      additional-lore:
        - '<gray>Wymagane: <red>{REQUIRED}'
        - '<gray>Brakuje: <red>{MISSING}'
        - '<red>Łącznie: <white>{TOTAL} <dark_gray>({INV} + {ENDER})'

  per-set:
    vip:
      title: '<gold><bold>VIP <dark_gray>- Przedmioty na gildie'
      items:
        item-x:
          material: ORANGE_STAINED_GLASS_PANE
          name: ' '
    premium:
      title: '<aqua><bold>Premium <dark_gray>- Przedmioty na gildie'
      items:
        item-x:
          material: LIGHT_BLUE_STAINED_GLASS_PANE
          name: ' '
```

---

### `pl.yml` (sekcja items)

```yaml
guild-items:
  admin-bypass: '<yellow>⚡ Posiadasz uprawnienia admina — wymagania zostają pominięte.'

  requirements-not-met-header: '<red>✘ Nie spełniasz wymagań do założenia gildii!'
  requirement-money: '<gray>  Pieniądze: <red>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  requirement-experience: '<gray>  Doświadczenie: <red>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  requirement-rank: '<gray>  Ranking: <red>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  requirement-items-header: '<gray>  Brakujące przedmioty:'
  requirement-item-line: '<gray>    • <white>{ITEM}<gray>: <red>{CURRENT} <dark_gray>/ <white>{REQUIRED}'

  admin-command-usage: '<red>Użycie: <white>/ga items <nick> [set]'
  admin-command-set-not-found: '<red>Set <white>{SET}<red> nie istnieje.'
  admin-command-player-not-found: '<red>Gracz <white>{PLAYER}<red> nie został znaleziony.'
  admin-bypass-grant: '<green>Gracz <white>{PLAYER}<green> otrzymał jednorazowy bypass wymagań.'

  gui-status-ready: '<green>✔ Możesz założyć gildię!'
  gui-status-not-ready: '<red>✘ Brakuje Ci wymagań'
  gui-money-line: '<gray>Pieniądze: <green>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  gui-experience-line: '<gray>Doświadczenie: <green>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  gui-rank-line: '<gray>Ranking: <green>{CURRENT} <dark_gray>/ <white>{REQUIRED}'
  gui-requirement-disabled: '<dark_gray>  ✦ {TYPE} <gray>— nie wymagane'
```

---

## Do uzgodnienia przed implementacją
- Czy przy wszystkich wymaganiach `false` → gildię można założyć bez niczego?
- Czy summary ma pokazywać linie wyłączonych wymagań (`gui-requirement-disabled`) czy je ukrywać?

---

## Przykładowy items.yml

```yaml
# =======================================
# ITEMS.YML — System przedmiotów gildii
# =======================================
# Ten plik zawiera:
#   - bibliotekę przedmiotów (guild-item-library)
#   - sety wymagań (guild-item-sets)
#   - konfigurację GUI (gui)
#   - bypass administracyjny
#
# Wiadomości tekstowe → pl.yml
# =======================================

# -------------------------------------------------------
# BIBLIOTEKA PRZEDMIOTÓW
# -------------------------------------------------------
# Każdy item definiujesz tutaj RAZ.
# Klucz (np. "diamonds") używasz potem w sekcji guild-item-sets.
# Chcesz nowy item w secie? Najpierw zdefiniuj go tutaj.
guild-item-library:
  diamonds:
    material: DIAMOND
    name: null            # null = domyślna nazwa z Minecrafta
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null     # format: R_G_B

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

  gold:
    material: GOLD_INGOT
    name: null
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null

  tnt:
    material: TNT
    name: null
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null

  obsidian:
    material: OBSIDIAN
    name: null
    lore: null
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: null
    armor-color: null

  enderpearls:
    material: ENDER_PEARL
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
      - '<yellow>✦ <white>Niezniszczalność III'
    enchants:
      - sharpness:5
      - unbreaking:3
      - fire_aspect:2
    flags:
      - HIDE_ATTRIBUTES
    custom-model-data: null
    skull-owner: null
    armor-color: null

  dragon-head:
    material: PLAYER_HEAD
    name: '<red>Głowa Smoka'
    lore:
      - '<dark_gray>Rzadki artefakt'
      - ''
      - '<gray>Zdobyty z Końca...'
    enchants: null
    flags: null
    custom-model-data: null
    skull-owner: MHF_EnderDragon
    armor-color: null

  guild-book:
    material: ENCHANTED_BOOK
    name: '<light_purple>Księga Gildii'
    lore:
      - '<gray>Starożytna księga zawierająca'
      - '<gray>sekrety zakładania gildii.'
      - ''
      - '<dark_purple>❖ Wymagana do rytuału'
    enchants: null
    flags: null
    custom-model-data: 1001
    skull-owner: null
    armor-color: null

  nether-star:
    material: NETHER_STAR
    name: '<gradient:yellow:gold:yellow>✧ Gwiazda Założenia ✧</gradient>'
    lore:
      - ''
      - '<white>Lśniąca gwiazda z Netheru,'
      - '<white>symbol władzy i potęgi.'
      - ''
      - '<gold>★★★★★ <white>Legendarny'
    enchants:
      - luck:1
    flags:
      - HIDE_ENCHANTS
    custom-model-data: null
    skull-owner: null
    armor-color: null

# -------------------------------------------------------
# SETY PRZEDMIOTÓW
# -------------------------------------------------------
# permission: null = każdy gracz bez specjalnej permisji
# priority: wyższy numer = sprawdzany wcześniej
#   (gracz dostaje pierwszy pasujący set)
# items: klucz musi istnieć w guild-item-library, wartość = wymagana ilość
guild-item-sets:
  default:
    permission: null
    priority: 0
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: true
      rank-enabled: true
    items:
      diamonds: 64
      emeralds: 64
      iron: 64
      gold: 64
      tnt: 64
      obsidian: 64
      enderpearls: 16
    required-experience: 30
    required-money: 10000.0
    required-rank: 1000

  vip:
    permission: funnyguilds.items.vip
    priority: 10
    requirements:
      items-enabled: true
      experience-enabled: true
      money-enabled: false
      rank-enabled: true
    items:
      diamonds: 32
      emeralds: 32
      iron: 32
      gold: 32
      tnt: 32
      obsidian: 32
      enderpearls: 8
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
      founders-sword: 1
      dragon-head: 1
      guild-book: 1
      nether-star: 1
      diamonds: 16
    required-experience: 0
    required-money: 0.0
    required-rank: 0

# -------------------------------------------------------
# BYPASS ADMINISTRACYJNY
# -------------------------------------------------------
# Gracz z tą permisją może założyć gildię bez żadnych wymagań
admin-items-bypass-permission: funnyguilds.items.admin.bypass

# -------------------------------------------------------
# GUI
# -------------------------------------------------------
# Czy GUI jest włączone globalnie?
# false = przy brakujących wymaganiach wyświetla tylko wiadomość z pl.yml
gui:
  enabled: true
  title: '<dark_gray><bold>Przedmioty na gildie <gray>({SET})'
  rows: 6

  # Pattern GUI (9 znaków × rows rzędów)
  # Dostępne tokeny:
  #   {x}              - filler (wypełniacz)
  #   {item-1}..{item-N} - kolejne itemy z setu (auto)
  #   {toggle:klucz}   - przycisk przełączania widoku all/missing
  #   {summary:klucz}  - podsumowanie statusu gracza
  #   {close:klucz}    - zamknięcie GUI
  # Każdy rząd musi mieć dokładnie 9 tokenów.
  pattern:
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{item-1}{x}{item-2}{x}{item-3}{x}{item-4}{x}'
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{item-5}{x}{item-6}{x}{item-7}{x}{item-8}{x}'
    - '{x}{x}{x}{x}{x}{x}{x}{x}{x}'
    - '{x}{x}{toggle:item-t}{x}{summary:item-s}{x}{x}{x}{close:item-c}'

  # Definicje itemów GUI (filler, toggle, summary, close)
  items:
    item-x:
      material: BLACK_STAINED_GLASS_PANE
      name: ' '

    item-t:
      all:
        material: LIME_DYE
        name: '<green><bold>Pokaż wszystkie'
        lore:
          - '<gray>Kliknij aby pokazać'
          - '<gray>tylko brakujące itemy'
      missing-only:
        material: RED_DYE
        name: '<red><bold>Tylko brakujące'
        lore:
          - '<gray>Kliknij aby pokazać'
          - '<gray>wszystkie itemy'

    item-s:
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

    item-c:
      material: BARRIER
      name: '<red><bold>Zamknij'
      lore:
        - '<gray>Kliknij aby zamknąć'

  # Wygląd itemów z setu w GUI
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
  per-set:
    vip:
      title: '<gold><bold>VIP <dark_gray>- Przedmioty na gildie'
      items:
        item-x:
          material: ORANGE_STAINED_GLASS_PANE
          name: ' '
    premium:
      title: '<aqua><bold>Premium <dark_gray>- Przedmioty na gildie'
      items:
        item-x:
          material: LIGHT_BLUE_STAINED_GLASS_PANE
          name: ' '
```
---