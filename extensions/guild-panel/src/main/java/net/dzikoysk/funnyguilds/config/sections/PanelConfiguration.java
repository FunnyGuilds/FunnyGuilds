package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemDefinition;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PanelConfiguration extends OkaeriConfig {

    @Exclude
    private static final String PREVIOUS_OVERVIEW_ACTIONS =
            "{fill} {fill} {allies} {territory} {base} {validity} {empty} {close} {fill}";
    @Exclude
    private static final String LEGACY_OVERVIEW_ACTIONS =
            "{fill} {fill} {allies} {empty} {base} {empty} {empty} {close} {fill}";

    public boolean enabled = true;
    public Screens screens = new Screens();

    public void upgradeLayout() {
        Overview overview = this.screens.overview;
        overview.pattern = overview.pattern.stream().map(row -> row.replace("{set-base}", "{empty}")).toList();
        overview.items.remove("set-base");
        if (overview.rows == 3 && (overview.pattern.equals(List.of(
                Overview.PREVIOUS_TOP_ROW, LEGACY_OVERVIEW_ACTIONS, Overview.BORDER_ROW)) || overview.pattern.equals(List.of(
                Overview.PREVIOUS_TOP_ROW, PREVIOUS_OVERVIEW_ACTIONS, Overview.BORDER_ROW)))) {
            Map<String, GuildItemDefinition> defaults = Overview.defaultItems();
            defaults.forEach(overview.items::putIfAbsent);
            overview.rows = 5;
            overview.pattern = Overview.defaultPattern();
        }
        List<String> withoutAudit = Overview.defaultPattern().stream().map(row -> row.replace("{audit}", "{fill}")).toList();
        List<String> withoutRoles = withoutAudit.stream().map(row -> row.replace("{roles}", "{empty}")).toList();
        if (overview.rows == 5 && (overview.pattern.equals(withoutAudit) || overview.pattern.equals(withoutRoles))) {
            Overview.defaultItems().forEach(overview.items::putIfAbsent);
            overview.pattern = Overview.defaultPattern();
        }
        List<String> oldList = List.of(
                "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}",
                "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}",
                "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}",
                "{empty} {empty} {empty} {empty} {summary} {empty} {empty} {empty} {empty}",
                "{previous} {empty} {empty} {empty} {page} {empty} {empty} {next} {back}");
        upgradeScreen(this.screens.expansion, 5, oldList, Screens.purchaseList(true));
        upgradeScreen(this.screens.renewal, 5, oldList, Screens.purchaseList(false));
        upgradeScreen(this.screens.purchaseConfirm, 3,
                List.of("{empty} {empty} {empty} {empty} {summary}",
                        "{empty} {empty} {buy} {empty} {empty} {empty} {cancel}"),
                Screens.purchaseConfirm());
        PanelScreen roles = Screens.roles();
        List<String> oldRoles = roles.pattern.stream()
                .map(row -> row.replace("{invite-player}", "{empty}").replace("{pending-invites}", "{empty}").replace("{kick}", "{empty}"))
                .toList();
        upgradeScreen(this.screens.roles, 6, oldRoles, roles);
        roles.items.forEach(this.screens.roles.items::putIfAbsent);
    }

    private static void upgradeScreen(PanelScreen screen, int oldRows, List<String> oldPattern, PanelScreen defaults) {
        if (screen.rows == oldRows && screen.pattern.equals(oldPattern)) {
            defaults.items.forEach(screen.items::putIfAbsent);
            screen.rows = defaults.rows;
            screen.pattern = defaults.pattern;
        }
    }

    public void validateLayout() {
        validateScreen("overview", this.screens.overview.rows, this.screens.overview.pattern, this.screens.overview.items);
        validateScreen("relations", this.screens.relations.rows, this.screens.relations.pattern, this.screens.relations.items);
        validateScreen("relation", this.screens.relation.rows, this.screens.relation.pattern, this.screens.relation.items);
        validateScreen("confirm", this.screens.confirm.rows, this.screens.confirm.pattern, this.screens.confirm.items);
        validateScreen("renewal", this.screens.renewal.rows, this.screens.renewal.pattern, this.screens.renewal.items);
        validateScreen("expansion", this.screens.expansion.rows, this.screens.expansion.pattern, this.screens.expansion.items);
        validateScreen("purchase-confirm", this.screens.purchaseConfirm.rows, this.screens.purchaseConfirm.pattern, this.screens.purchaseConfirm.items);
        requireEntry("relations", this.screens.relations);
        requireEntry("renewal", this.screens.renewal);
        requireEntry("expansion", this.screens.expansion);
        validateScreen("roles", this.screens.roles.rows, this.screens.roles.pattern, this.screens.roles.items);
        requireEntry("roles", this.screens.roles);
        validateScreen("audit", this.screens.audit.rows, this.screens.audit.pattern, this.screens.audit.items);
        requireEntry("audit", this.screens.audit);
    }

    private static void requireEntry(String name, PanelScreen screen) {
        if (screen.pattern.stream().flatMap(row -> GuiPattern.slots(row).stream()).noneMatch("entry"::equals)) {
            throw new IllegalArgumentException(name + " must contain an {entry} slot");
        }
    }

    private static void validateScreen(String name, int rows, List<String> pattern, Map<String, GuildItemDefinition> items) {
        if (rows < 1 || rows > 6 || pattern.size() > rows) {
            throw new IllegalArgumentException(name + " must fit between 1 and 6 rows");
        }
        for (String row : pattern) {
            List<String> slots = GuiPattern.slots(row);
            if (slots.size() > 9 || slots.stream().anyMatch(slot -> !slot.equals("empty") && !items.containsKey(slot))) {
                throw new IllegalArgumentException("Invalid slots in " + name + " row: " + row);
            }
        }
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Screens extends OkaeriConfig {

        public Overview overview = new Overview();
        public PanelScreen relations = relations();
        public PanelScreen relation = relation();
        public PanelScreen confirm = confirm();
        public PanelScreen renewal = purchaseList(false);
        public PanelScreen expansion = purchaseList(true);
        public PanelScreen purchaseConfirm = purchaseConfirm();
        public PanelScreen roles = roles();
        public PanelScreen audit = audit();
        public Map<String, String> auditCategories = new LinkedHashMap<>(Map.of(
                "all", "Wszystkie", "guild", "Gildia", "members", "Członkowie", "permissions", "Uprawnienia",
                "pvp", "PvP", "relations", "Relacje", "purchases", "Zakupy"
        ));
        public Map<String, String> auditDescriptions = new LinkedHashMap<>(Map.ofEntries(
                Map.entry("guild-created", "Utworzono gildię [{TARGET}]"),
                Map.entry("invited", "Wysłano zaproszenie do gracza {TARGET}"),
                Map.entry("invitation-revoked", "Cofnięto zaproszenie do gracza {TARGET}"),
                Map.entry("joined", "Dołączył gracz {TARGET}"),
                Map.entry("left", "Odszedł gracz {TARGET}"),
                Map.entry("kicked", "Wyrzucono gracza {TARGET}"),
                Map.entry("leader-changed", "Nowy lider: {TARGET}"),
                Map.entry("deputy-added", "Nowy zastępca: {TARGET}"),
                Map.entry("deputy-removed", "Odebrano status zastępcy graczowi {TARGET}"),
                Map.entry("permissions-changed", "Zmieniono uprawnienia: {TARGET}"),
                Map.entry("pvp-changed", "PvP w gildii: {STATE}"),
                Map.entry("ally-pvp-changed", "PvP w sojuszu z [{TARGET}]: {STATE} (zmiana w [{ORIGIN}])"),
                Map.entry("alliance-invited", "Gildia [{ORIGIN}] zaprosiła do sojuszu [{DESTINATION}]"),
                Map.entry("alliance-invitation-revoked", "[{ORIGIN}] cofnęła zaproszenie dla [{DESTINATION}]"),
                Map.entry("alliance-formed", "Zawarto sojusz z [{TARGET}]"),
                Map.entry("alliance-broken", "Zerwano sojusz z [{TARGET}]"),
                Map.entry("war-declared", "[{ORIGIN}] wypowiedziała wojnę gildii [{DESTINATION}]"),
                Map.entry("war-withdrawn", "[{ORIGIN}] wycofała deklarację wojny przeciw [{DESTINATION}]"),
                Map.entry("war-won", "[{ORIGIN}] wygrała wojnę z [{DESTINATION}]"),
                Map.entry("territory-expanded", "Rozbudowano teren do poziomu {LEVEL}, promień {SIZE}. Koszt: {COST}"),
                Map.entry("validity-renewed", "Przedłużono ważność o {PERIOD}. Koszt: {COST}")
        ));
        public String roleNamePrompt = "<yellow>Wpisz nazwę nowej roli na czacie. Masz 30 sekund. Wpisz <white>anuluj<yellow>, aby wrócić.";
        public Map<String, String> roleLabels = new LinkedHashMap<>(Map.ofEntries(
                Map.entry("owner", "Lider"), Map.entry("deputy", "Zastępca"), Map.entry("member", "Członek"),
                Map.entry("roles", "Role gildii"), Map.entry("members", "Członkowie"),
                Map.entry("assign", "Przypisz rolę"), Map.entry("delete", "Usuń rolę"), Map.entry("delegate", "Delegowanie"),
                Map.entry("invite-player", "Kliknij, aby zaprosić"), Map.entry("pending-invites", "Kliknij, aby cofnąć zaproszenie"),
                Map.entry("invite-view", "Zaproś gracza"), Map.entry("pending-view", "Oczekujące zaproszenia"),
                Map.entry("kick", "Wyrzuć z gildii"),
                Map.entry("inherit", "Dziedzicz"), Map.entry("allow", "Zezwól"), Map.entry("deny", "Zabroń"),
                Map.entry("source-owner", "Lider"), Map.entry("source-player", "Wyjątek gracza"),
                Map.entry("source-custom_role", "Własna rola"), Map.entry("source-built_in_role", "Rola wbudowana"),
                Map.entry("source-default", "Domyślne reguły")
        ));
        public Map<String, String> permissionNames = permissionNames();
        public Map<String, String> permissionMaterials = new LinkedHashMap<>(Map.ofEntries(
                Map.entry("command.base", "ENDER_PEARL"), Map.entry("command.set-base", "COMPASS"),
                Map.entry("command.extend-validity", "CLOCK"), Map.entry("command.enlarge", "GRASS_BLOCK"),
                Map.entry("command.invite", "NAME_TAG"), Map.entry("command.kick", "IRON_BOOTS"),
                Map.entry("command.ally", "CHAIN"), Map.entry("command.war", "CROSSBOW"),
                Map.entry("command.pvp", "DIAMOND_SWORD"), Map.entry("command.buy-life", "TOTEM_OF_UNDYING"),
                Map.entry("command.manage-permissions", "TRIPWIRE_HOOK"), Map.entry("command.view-audit", "WRITABLE_BOOK"),
                Map.entry("protection.block-break", "DIAMOND_PICKAXE"), Map.entry("protection.block-place", "BRICKS"),
                Map.entry("protection.block-ignite", "FLINT_AND_STEEL"), Map.entry("protection.bucket-fill", "BUCKET"),
                Map.entry("protection.bucket-empty", "LAVA_BUCKET"), Map.entry("protection.hanging-place", "PAINTING"),
                Map.entry("protection.hanging-break", "SHEARS"), Map.entry("protection.entity-place", "ARMOR_STAND"),
                Map.entry("protection.open-container", "CHEST"), Map.entry("protection.use-mechanism", "LEVER")
        ));

        private static PanelScreen audit() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_green>Dziennik gildii {TAG}";
            screen.rows = 6;
            String entries = "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}";
            screen.pattern = List.of("{empty} {empty} {empty} {empty} {filter} {empty} {empty} {empty} {empty}",
                    entries, entries, entries, entries,
                    "{back} {empty} {previous} {empty} {page} {empty} {next} {empty} {empty}");
            screen.items.put("entry", item("PAPER", "<white>{DATE}", "<gray>Wykonawca: <white>{ACTOR}", "<gray>Kategoria: <white>{CATEGORY}"));
            screen.items.put("filter", item("HOPPER", "<yellow>Kategoria: <white>{FILTER}", "<gray>Kliknij, aby przełączyć kategorię"));
            screen.items.put("empty-log", item("BOOK", "<gray>Brak wpisów w tej kategorii"));
            screen.items.put("previous", item("ARROW", "<yellow>Poprzednia strona"));
            screen.items.put("next", item("ARROW", "<yellow>Następna strona"));
            screen.items.put("page", item("BOOK", "<white>Strona {PAGE}/{PAGES}"));
            screen.items.put("back", item("ARROW", "<yellow>Wróć do podsumowania"));
            return screen;
        }

        private static Map<String, String> permissionNames() {
            return new LinkedHashMap<>(Map.ofEntries(
                    Map.entry("command.base", "Teleport do bazy"), Map.entry("command.set-base", "Ustawianie bazy"),
                    Map.entry("command.extend-validity", "Przedłużanie ważności"), Map.entry("command.enlarge", "Rozbudowa terenu"),
                    Map.entry("command.invite", "Zapraszanie graczy"), Map.entry("command.kick", "Wyrzucanie graczy"),
                    Map.entry("command.ally", "Zarządzanie sojuszami"), Map.entry("command.war", "Zarządzanie wojnami"),
                    Map.entry("command.pvp", "Przełączanie PvP"), Map.entry("command.buy-life", "Kupowanie żyć"),
                    Map.entry("command.manage-permissions", "Zarządzanie uprawnieniami"), Map.entry("command.view-audit", "Odczyt dziennika"),
                    Map.entry("protection.block-break", "Niszczenie bloków"), Map.entry("protection.block-place", "Stawianie bloków"),
                    Map.entry("protection.block-ignite", "Podpalanie bloków"), Map.entry("protection.bucket-fill", "Napełnianie wiader"),
                    Map.entry("protection.bucket-empty", "Opróżnianie wiader"), Map.entry("protection.hanging-place", "Wieszanie obiektów"),
                    Map.entry("protection.hanging-break", "Niszczenie wiszących obiektów"), Map.entry("protection.entity-place", "Stawianie obiektów"),
                    Map.entry("protection.open-container", "Otwieranie pojemników"), Map.entry("protection.use-mechanism", "Używanie drzwi i mechanizmów")
            ));
        }

        private static PanelScreen roles() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_green>{VIEW}";
            screen.rows = 6;
            String entries = "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}";
            screen.pattern = List.of("{roles} {members} {invite-player} {delegate} {summary} {pending-invites} {new-role} {assign} {delete}",
                    entries, entries, entries, entries,
                    "{back} {empty} {previous} {empty} {page} {empty} {next} {empty} {kick}");
            screen.items.put("entry", item("PAPER", "<white>{NAME}", "<gray>{DETAIL}"));
            screen.items.put("member", item("PLAYER_HEAD", "<white>{NAME}", "<gray>Rola: {DETAIL}"));
            screen.items.put("invite-target", item("PLAYER_HEAD", "<white>{NAME}", "<gray>{DETAIL}"));
            screen.items.put("permission", item("PAPER", "<white>{NAME}", "<gray>Ustawienie: {SETTING}",
                    "<gray>Wynik: {EFFECTIVE}", "<gray>Źródło: {SOURCE}"));
            screen.items.put("roles", item("BOOK", "<yellow>Role"));
            screen.items.put("members", item("PLAYER_HEAD", "<yellow>Członkowie"));
            screen.items.put("summary", item("WRITABLE_BOOK", "<white>{VIEW}"));
            screen.items.put("new-role", item("NAME_TAG", "<green>Utwórz rolę"));
            screen.items.put("assign", item("NAME_TAG", "<green>Zmień rolę"));
            screen.items.put("delete", item("BARRIER", "<red>Usuń rolę"));
            screen.items.put("invite-player", item("WRITABLE_BOOK", "<green>Zaproś gracza"));
            screen.items.put("pending-invites", item("PAPER", "<yellow>Oczekujące zaproszenia"));
            screen.items.put("kick", item("IRON_BOOTS", "<red>Wyrzuć z gildii"));
            screen.items.put("empty-list", item("BARRIER", "<gray>Brak wpisów"));
            screen.items.put("delegate", item("WRITABLE_BOOK", "<yellow>Zakres delegowania zastępcy"));
            screen.items.put("delegation-rule", item("PAPER", "<white>{NAME}", "<gray>Delegowanie: {SETTING}"));
            screen.items.put("previous", item("ARROW", "<yellow>Poprzednia strona"));
            screen.items.put("next", item("ARROW", "<yellow>Następna strona"));
            screen.items.put("page", item("BOOK", "<white>Strona {PAGE}/{PAGES}"));
            screen.items.put("back", item("ARROW", "<yellow>Wróć"));
            return screen;
        }
        public Map<String, String> expansionMaterials = new LinkedHashMap<>(Map.of(
                "current", "GREEN_CONCRETE",
                "available", "YELLOW_CONCRETE",
                "locked", "RED_CONCRETE",
                "purchased", "GRAY_CONCRETE"));
        public Map<String, String> purchaseLabels = new LinkedHashMap<>(Map.ofEntries(
                Map.entry("none", "Dostępny do zakupu"),
                Map.entry("wait", "Dostępny za {WAIT}"),
                Map.entry("period", "Okres niedostępny"),
                Map.entry("price", "Nie można wyliczyć ceny"),
                Map.entry("regions", "Teren gildii niedostępny"),
                Map.entry("limit", "Osiągnięto maksymalny poziom"),
                Map.entry("purchased", "Zakupiony"),
                Map.entry("current", "Aktualny poziom"),
                Map.entry("previous", "Wymaga poprzedniego poziomu"),
                Map.entry("near", "Inna gildia jest zbyt blisko"),
                Map.entry("economy", "Brak dostawcy ekonomii Vault"),
                Map.entry("funds", "Brakuje środków lub przedmiotów"),
                Map.entry("free", "Brak"),
                Map.entry("expand", "Poziom {LEVEL}"),
                Map.entry("expand-result", "Promień {SIZE}"),
                Map.entry("renew", "Przedłużenie o {PERIOD}")
        ));
        public Map<String, String> relationLabels = new LinkedHashMap<>(Map.of(
                "ally", "Sojusz",
                "incoming-invitation", "Zaproszenie przychodzące",
                "outgoing-invitation", "Zaproszenie wychodzące",
                "incoming-war", "Wojna wypowiedziana przez drugą gildię",
                "outgoing-war", "Wojna wypowiedziana przez naszą gildię",
                "mutual-war", "Obustronna deklaracja wojny",
                "neutral", "Brak relacji"
        ));

        private static PanelScreen relations() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_green>Relacje gildii {TAG}";
            screen.rows = 5;
            String entries = "{entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {entry}";
            screen.pattern = List.of(
                    "{allies} {invitations} {wars} {browse} {pvp} {empty} {empty} {empty} {back}",
                    entries, entries, entries,
                    "{previous} {empty} {empty} {empty} {page} {empty} {empty} {empty} {next}"
            );
            screen.items.put("allies", item("WRITABLE_BOOK", "<green>Sojusze"));
            screen.items.put("invitations", item("PAPER", "<yellow>Zaproszenia do sojuszu"));
            screen.items.put("wars", item("IRON_SWORD", "<red>Wojny"));
            screen.items.put("browse", item("COMPASS", "<aqua>Wszystkie gildie"));
            screen.items.put("pvp", item("DIAMOND_SWORD", "<yellow>PvP w gildii", "<gray>Aktualny stan: {PVP}"));
            screen.items.put("entry", item("NAME_TAG", "<white>{TARGET} [{TARGET_TAG}]", "<gray>{RELATION}"));
            screen.items.put("empty-list", item("BARRIER", "<gray>Brak wpisów"));
            screen.items.put("previous", item("ARROW", "<yellow>Poprzednia strona"));
            screen.items.put("page", item("BOOK", "<white>Strona {PAGE}/{PAGES}"));
            screen.items.put("next", item("ARROW", "<yellow>Następna strona"));
            screen.items.put("back", item("ARROW", "<yellow>Wróć do podsumowania"));
            return screen;
        }

        private static PanelScreen relation() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_green>Gildia {TARGET_TAG}";
            screen.rows = 3;
            screen.pattern = List.of(
                    "{empty} {empty} {empty} {empty} {summary} {empty} {empty} {empty} {back}",
                    "{invite} {accept} {revoke} {break} {ally-pvp} {declare} {withdraw}"
            );
            screen.items.put("summary", item("NAME_TAG", "<white>{TARGET} [{TARGET_TAG}]", "<gray>{RELATION}"));
            screen.items.put("invite", item("PAPER", "<green>Zaproś do sojuszu", "<gray>Własna deklaracja wojny zostanie wycofana."));
            screen.items.put("accept", item("LIME_DYE", "<green>Przyjmij zaproszenie"));
            screen.items.put("revoke", item("GRAY_DYE", "<yellow>Cofnij zaproszenie"));
            screen.items.put("break", item("SHEARS", "<red>Zerwij sojusz"));
            screen.items.put("ally-pvp", item("DIAMOND_SWORD", "<yellow>PvP z sojusznikiem", "<gray>Moja gildia: {OUR_PVP}",
                    "<gray>Sojusznik: {THEIR_PVP}", "<gray>Stan według zasad gildii: {ALLY_PVP}"));
            screen.items.put("declare", item("IRON_SWORD", "<red>Wypowiedz wojnę"));
            screen.items.put("withdraw", item("WHITE_BANNER", "<yellow>Wycofaj deklarację wojny",
                    "<gray>Wojna potrwa dalej, jeśli druga gildia utrzyma deklarację."));
            screen.items.put("back", item("ARROW", "<yellow>Wróć do listy"));
            return screen;
        }

        private static PanelScreen confirm() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_red>Potwierdź działanie";
            screen.rows = 3;
            screen.pattern = List.of(
                    "{empty} {empty} {empty} {empty} {summary}",
                    "{empty} {empty} {accept} {empty} {empty} {empty} {cancel}"
            );
            screen.items.put("summary", item("PAPER", "<white>{ACTION}", "<gray>{TARGET} [{TARGET_TAG}]", "<gray>{RELATION}"));
            screen.items.put("accept", item("LIME_DYE", "<green>Potwierdź"));
            screen.items.put("cancel", item("BARRIER", "<red>Anuluj"));
            return screen;
        }

        private static PanelScreen purchaseList(boolean expansion) {
            PanelScreen screen = new PanelScreen();
            screen.title = expansion ? "<dark_green>Rozbudowa terenu {TAG}" : "<dark_green>Przedłużanie gildii {TAG}";
            screen.rows = 6;
            String entries = "{fill} {entry} {entry} {entry} {entry} {entry} {entry} {entry} {fill}";
            String header = "{fill} {fill} {fill} {fill} {summary} {fill} {fill} {fill} {fill}";
            String space = "{fill} {empty} {empty} {empty} {empty} {empty} {empty} {empty} {fill}";
            String navigation = "{back} {fill} {previous} {fill} {page} {fill} {next} {fill} {fill}";
            screen.pattern = expansion
                    ? List.of(header, "{fill} {empty} {legend-current} {empty} {legend-available} {empty} {legend-locked} {empty} {fill}",
                            entries, entries, entries, navigation)
                    : List.of(header, space, "{fill} {entry} {empty} {entry} {empty} {entry} {empty} {entry} {fill}",
                            space, "{fill} {empty} {entry} {empty} {entry} {empty} {entry} {empty} {fill}", navigation);
            screen.items.put("fill", item("BLACK_STAINED_GLASS_PANE", " "));
            if (expansion) {
                screen.items.put("legend-current", item("GREEN_CONCRETE", "<green>Aktualny poziom"));
                screen.items.put("legend-available", item("YELLOW_CONCRETE", "<yellow>Dostępny do zakupu"));
                screen.items.put("legend-locked", item("RED_CONCRETE", "<red>Zablokowany"));
            }
            screen.items.put("entry", expansion
                    ? item("GRASS_BLOCK", "<green>Poziom {LEVEL}", "<gray>Promień: <white>{SIZE}", "<gray>{STATUS}",
                            "<gray>Koszt: <white>{COST}", "<gray>Brakuje: <white>{MISSING}")
                    : item("CLOCK", "<green>{PERIOD}", "<gray>Nowa data: <white>{NEW_DATE}", "<gray>{STATUS}",
                            "<gray>Koszt: <white>{COST}", "<gray>Brakuje: <white>{MISSING}"));
            screen.items.put("summary", expansion
                    ? item("BOOK", "<white>Rozbudowa terenu", "<gray>Maksymalny poziom: <white>{MAX_LEVEL}", "<gray>{STATUS}")
                    : item("BOOK", "<white>Ważność gildii", "<gray>Obecna data: <white>{DATE}"));
            screen.items.put("previous", item("ARROW", "<yellow>Poprzednia strona"));
            screen.items.put("next", item("ARROW", "<yellow>Następna strona"));
            screen.items.put("page", item("BOOK", "<white>Strona {PAGE}/{PAGES}"));
            screen.items.put("back", item("ARROW", "<yellow>Wróć do podsumowania"));
            return screen;
        }

        private static PanelScreen purchaseConfirm() {
            PanelScreen screen = new PanelScreen();
            screen.title = "<dark_green>Potwierdź zakup";
            screen.rows = 5;
            screen.pattern = List.of(
                    Overview.BORDER_ROW,
                    "{fill} {empty} {empty} {empty} {summary} {empty} {empty} {empty} {fill}",
                    "{fill} {empty} {empty} {empty} {empty} {empty} {empty} {empty} {fill}",
                    "{fill} {empty} {buy} {empty} {empty} {empty} {cancel} {empty} {fill}",
                    Overview.BORDER_ROW
            );
            screen.items.put("fill", item("BLACK_STAINED_GLASS_PANE", " "));
            screen.items.put("summary", item("BOOK", "<white>{PRODUCT}", "<gray>Rezultat: <white>{RESULT}",
                    "<gray>Koszt: <white>{COST}", "<gray>Brakuje: <white>{MISSING}", "<gray>{STATUS}"));
            screen.items.put("buy", item("LIME_DYE", "<green>Kup"));
            screen.items.put("cancel", item("BARRIER", "<red>Anuluj"));
            return screen;
        }
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PanelScreen extends OkaeriConfig {

        public String title = "";
        public int rows = 3;
        public List<String> pattern = List.of();
        public Map<String, GuildItemDefinition> items = new LinkedHashMap<>();
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class Overview extends OkaeriConfig {

        @Exclude
        private static final String PREVIOUS_TOP_ROW = "{fill} {fill} {fill} {fill} {summary} {fill} {fill} {fill} {fill}";
        @Exclude
        private static final String BORDER_ROW = "{fill} {fill} {fill} {fill} {fill} {fill} {fill} {fill} {fill}";

        public String title = "<dark_green>Gildia {TAG}";
        public int rows = 5;
        public List<String> pattern = defaultPattern();
        public String enabledText = "Włączone";
        public String disabledText = "Wyłączone";
        public String emptyText = "Brak";
        public String allianceLine = "<gray>{ALLY} [{ALLY_TAG}] <gray>PvP: {ALLY_PVP} (my: {OUR_PVP}, sojusznik: {THEIR_PVP})";
        public Map<String, GuildItemDefinition> items = defaultItems();

        private static List<String> defaultPattern() {
            return List.of(BORDER_ROW,
                    "{fill} {empty} {empty} {empty} {summary} {empty} {empty} {empty} {fill}",
                    "{fill} {empty} {empty} {empty} {roles} {empty} {empty} {empty} {fill}",
                    "{fill} {base} {empty} {allies} {empty} {territory} {empty} {validity} {fill}",
                    "{audit} {fill} {fill} {fill} {fill} {fill} {fill} {fill} {close}");
        }

        private static Map<String, GuildItemDefinition> defaultItems() {
            Map<String, GuildItemDefinition> items = new LinkedHashMap<>();
            items.put("fill", item("BLACK_STAINED_GLASS_PANE", " "));
            items.put("summary", item("BOOK", "<green>{GUILD} [{TAG}]",
                    "<gray>Lider: <white>{OWNER}",
                    "<gray>Zastępcy: <white>{DEPUTIES}",
                    "<gray>Członkowie online: <white>{ONLINE}/{MEMBERS}",
                    "<gray>Ranking: <white>{RANK} <gray>Punkty: <white>{POINTS}",
                    "<gray>Życia: <white>{LIVES}",
                    "<gray>Ważna do: <white>{EXPIRY}",
                    "<gray>Pozostało: <white>{REMAINING}",
                    "<gray>Teren (poziom / promień): <white>{LEVEL} / {SIZE}",
                    "<gray>PvP: <white>{PVP}"));
            items.put("allies", item("WRITABLE_BOOK", "<green>Sojusze, wojny i PvP",
                    "<gray>Aktywne sojusze: <white>{ALLIES}"));
            items.put("base", item("ENDER_PEARL", "<green>Teleport do bazy",
                    "<gray>Koszt: <white>{BASE_COST}",
                    "<gray>Opóźnienie: <white>{BASE_DELAY}"));
            items.put("territory", item("GRASS_BLOCK", "<green>Rozbudowa terenu"));
            items.put("validity", item("CLOCK", "<green>Przedłuż ważność"));
            items.put("roles", item("NAME_TAG", "<green>Role i uprawnienia"));
            items.put("audit", item("WRITABLE_BOOK", "<green>Dziennik gildii"));
            items.put("close", item("BARRIER", "<red>Zamknij"));
            return items;
        }

    }

    private static GuildItemDefinition item(String material, String name, String... lore) {
        GuildItemDefinition item = new GuildItemDefinition(material);
        item.name = name;
        item.lore = List.of(lore);
        return item;
    }
}
