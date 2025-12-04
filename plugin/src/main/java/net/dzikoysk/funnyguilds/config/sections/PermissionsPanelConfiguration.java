package net.dzikoysk.funnyguilds.config.sections;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import java.util.List;
import net.dzikoysk.funnyguilds.config.RawString;
import org.bukkit.Material;

@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PermissionsPanelConfiguration extends OkaeriConfig {

    @Comment("Czy panel uprawnień członków gildii ma być włączony")
    public boolean enabled = true;

    @Comment("")
    @Comment("Tytuł GUI panelu członków (lista członków)")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}, {PAGE}, {TOTAL_PAGES}")
    public RawString membersTitle = new RawString("&b&lCZŁONKOWIE GILDII {TAG} &8[{PAGE}/{TOTAL_PAGES}]");

    @Comment("")
    @Comment("Ilość wierszy w GUI listy członków (1-6)")
    public int membersRows = 6;

    @Comment("")
    @Comment("Tytuł GUI uprawnień pojedynczego członka")
    @Comment("Dostępne zmienne: {TAG}, {GUILD}, {PLAYER}, {ROLE}")
    public RawString permissionsTitle = new RawString("&b&lUPRAWNIENIA: {PLAYER}");

    @Comment("")
    @Comment("Ilość wierszy w GUI uprawnień (1-6)")
    public int permissionsRows = 6;

    @Comment("")
    @Comment("Konfiguracja ikonki w panelu lidera (do otwierania panelu uprawnień)")
    public PanelIconItem panelIcon = new PanelIconItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PanelIconItem extends OkaeriConfig {

        @Comment("Czy ikonka w panelu lidera ma być włączona")
        public boolean enabled = true;

        @Comment("")
        @Comment("Slot ikonki (0-53)")
        public int slot = 20;

        @Comment("")
        @Comment("Typ materiału ikonki")
        public Material material = Material.PLAYER_HEAD;

        @Comment("")
        @Comment("Nazwa ikonki")
        public RawString name = new RawString("&e&lZARZĄDZAJ UPRAWNIENIAMI");

        @Comment("")
        @Comment("Opis ikonki")
        public List<RawString> lore = RawString.listOf(
                "&7Kliknij, aby zarządzać",
                "&7uprawnieniami członków gildii.",
                "",
                "&aKliknij, aby otworzyć!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja główki członka w liście")
    public MemberHeadItem memberHead = new MemberHeadItem();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MemberHeadItem extends OkaeriConfig {

        @Comment("Nazwa główki członka")
        @Comment("Dostępne zmienne: {PLAYER}, {ROLE}, {STATUS}")
        public RawString name = new RawString("&a{PLAYER}");

        @Comment("")
        @Comment("Opis główki członka online")
        @Comment("Dostępne zmienne: {PLAYER}, {ROLE}, {OVERRIDE_COUNT}")
        public List<RawString> loreOnline = RawString.listOf(
                "&7Rola: &a{ROLE}",
                "&7Status: &aOnline",
                "&7Nadpisania: &e{OVERRIDE_COUNT}",
                "",
                "&aKliknij, aby zarządzać uprawnieniami!"
        );

        @Comment("")
        @Comment("Opis główki członka offline")
        @Comment("Dostępne zmienne: {PLAYER}, {ROLE}, {OVERRIDE_COUNT}")
        public List<RawString> loreOffline = RawString.listOf(
                "&7Rola: &a{ROLE}",
                "&7Status: &cOffline",
                "&7Nadpisania: &e{OVERRIDE_COUNT}",
                "",
                "&aKliknij, aby zarządzać uprawnieniami!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja przycisków nawigacji")
    public NavigationItems navigation = new NavigationItems();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class NavigationItems extends OkaeriConfig {

        @Comment("Slot przycisku poprzedniej strony")
        public int previousPageSlot = 45;

        @Comment("")
        @Comment("Materiał przycisku poprzedniej strony")
        public Material previousPageMaterial = Material.ARROW;

        @Comment("")
        @Comment("Nazwa przycisku poprzedniej strony")
        public RawString previousPageName = new RawString("&a« Poprzednia strona");

        @Comment("")
        @Comment("Slot przycisku następnej strony")
        public int nextPageSlot = 53;

        @Comment("")
        @Comment("Materiał przycisku następnej strony")
        public Material nextPageMaterial = Material.ARROW;

        @Comment("")
        @Comment("Nazwa przycisku następnej strony")
        public RawString nextPageName = new RawString("&aNastępna strona »");

        @Comment("")
        @Comment("Slot przycisku powrotu")
        public int backSlot = 49;

        @Comment("")
        @Comment("Materiał przycisku powrotu")
        public Material backMaterial = Material.BARRIER;

        @Comment("")
        @Comment("Nazwa przycisku powrotu")
        public RawString backName = new RawString("&c&lPowrót");
    }

    @Comment("")
    @Comment("Konfiguracja itemu uprawnienia")
    public PermissionItemConfig permissionItem = new PermissionItemConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PermissionItemConfig extends OkaeriConfig {

        @Comment("Materiał gdy uprawnienie jest włączone (z roli)")
        public Material materialOnRole = Material.LIME_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Materiał gdy uprawnienie jest wyłączone (z roli)")
        public Material materialOffRole = Material.RED_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Materiał gdy uprawnienie jest nadpisane na włączone")
        public Material materialOnOverride = Material.LIME_CONCRETE;

        @Comment("")
        @Comment("Materiał gdy uprawnienie jest nadpisane na wyłączone")
        public Material materialOffOverride = Material.RED_CONCRETE;

        @Comment("")
        @Comment("Opis uprawnienia (lore)")
        @Comment("Dostępne zmienne: {PERMISSION_NAME}, {STATUS}, {SOURCE}, {CHANGED_BY}, {CHANGED_AT}")
        public List<RawString> lore = RawString.listOf(
                "&7Status: {STATUS}",
                "&7Źródło: &e{SOURCE}",
                "",
                "&8Kliknij LPM aby włączyć",
                "&8Kliknij PPM aby wyłączyć",
                "&8Kliknij SHIFT+LPM aby zresetować"
        );

        @Comment("")
        @Comment("Tekst statusu gdy włączone")
        public String statusOn = "&aWłączone";

        @Comment("")
        @Comment("Tekst statusu gdy wyłączone")
        public String statusOff = "&cWyłączone";

        @Comment("")
        @Comment("Tekst źródła gdy z roli")
        public String sourceRole = "Rola ({ROLE})";

        @Comment("")
        @Comment("Tekst źródła gdy nadpisane")
        public String sourceOverride = "Nadpisanie";
    }

    @Comment("")
    @Comment("Konfiguracja przycisków akcji")
    public ActionButtons actionButtons = new ActionButtons();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class ActionButtons extends OkaeriConfig {

        @Comment("Slot przycisku resetu wszystkich uprawnień")
        public int resetAllSlot = 46;

        @Comment("")
        @Comment("Materiał przycisku resetu")
        public Material resetAllMaterial = Material.TNT;

        @Comment("")
        @Comment("Nazwa przycisku resetu")
        public RawString resetAllName = new RawString("&c&lRESETUJ WSZYSTKO");

        @Comment("")
        @Comment("Opis przycisku resetu")
        public List<RawString> resetAllLore = RawString.listOf(
                "&7Usuwa wszystkie nadpisania",
                "&7i przywraca uprawnienia z roli.",
                "",
                "&cKliknij, aby zresetować!"
        );

        @Comment("")
        @Comment("Slot przycisku kopiowania uprawnień")
        public int copySlot = 48;

        @Comment("")
        @Comment("Materiał przycisku kopiowania")
        public Material copyMaterial = Material.PAPER;

        @Comment("")
        @Comment("Nazwa przycisku kopiowania")
        public RawString copyName = new RawString("&e&lKOPIUJ DO INNYCH");

        @Comment("")
        @Comment("Opis przycisku kopiowania")
        public List<RawString> copyLore = RawString.listOf(
                "&7Kopiuje nadpisania uprawnień",
                "&7do wybranych członków gildii.",
                "",
                "&eKliknij, aby wybrać odbiorców!"
        );

        @Comment("")
        @Comment("Slot przycisku bulk apply")
        public int bulkApplySlot = 50;

        @Comment("")
        @Comment("Materiał przycisku bulk apply")
        public Material bulkApplyMaterial = Material.EMERALD;

        @Comment("")
        @Comment("Nazwa przycisku bulk apply")
        public RawString bulkApplyName = new RawString("&a&lZASTOSUJ DO ROLI");

        @Comment("")
        @Comment("Opis przycisku bulk apply")
        public List<RawString> bulkApplyLore = RawString.listOf(
                "&7Zastosowuje nadpisania uprawnień",
                "&7do wszystkich członków danej roli.",
                "",
                "&aKliknij, aby wybrać rolę!"
        );
    }

    @Comment("")
    @Comment("Konfiguracja wypełnienia pustych slotów")
    public FillItemConfig fillItem = new FillItemConfig();

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class FillItemConfig extends OkaeriConfig {

        @Comment("Czy puste sloty mają być wypełnione")
        public boolean enabled = true;

        @Comment("")
        @Comment("Typ materiału wypełnienia")
        public Material material = Material.GRAY_STAINED_GLASS_PANE;

        @Comment("")
        @Comment("Nazwa itemu wypełnienia")
        public RawString name = new RawString(" ");
    }

    @Comment("")
    @Comment("Czy archiwizować uprawnienia gdy gracz opuści gildię")
    @Comment("(uprawnienia zostaną przywrócone gdy gracz wróci)")
    public boolean archiveOnLeave = true;

    @Comment("")
    @Comment("Czy resetować nadpisania uprawnień gdy zmieni się rola gracza")
    public boolean resetOverridesOnRoleChange = false;
}
