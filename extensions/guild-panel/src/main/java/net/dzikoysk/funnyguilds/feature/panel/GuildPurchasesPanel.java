package net.dzikoysk.funnyguilds.feature.panel;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Block;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Expansion;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Payment;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Renewal;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.PanelScreen;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;
import net.dzikoysk.funnyguilds.module.api.PanelView.Button;

final class GuildPurchasesPanel {

    private final GuildPanel panel;
    private final PanelConfiguration.Screens screens;
    private final GuildPanelAccess access;

    GuildPurchasesPanel(GuildPanel panel, PanelConfiguration configuration, GuildPanelAccess access) {
        this.panel = panel;
        this.screens = configuration.screens;
        this.access = access;
    }

    void openRenewal(int page) {
        List<Duration> periods = this.access.periods();
        Map<String, String> summary = Map.of("TAG", this.access.tag(), "DATE", this.access.date(this.access.validity()));
        this.openList(this.screens.renewal, page, periods.size(), summary, this::openRenewal,
                index -> this.renewalEntry(periods.get(index), page));
    }

    void openExpansion(int page) {
        Expansion base = this.access.expansion(0);
        String status = base.block() == Block.REGIONS ? this.label("regions")
                : base.current() >= base.maximum() ? this.label("limit") : "";
        Map<String, String> summary = Map.of("TAG", this.access.tag(), "MAX_LEVEL", String.valueOf(base.maximum()), "STATUS", status);
        this.openList(this.screens.expansion, page, Math.max(base.maximum(), base.current()) + 1, summary, this::openExpansion,
                level -> this.expansionEntry(level, page));
    }

    private Button renewalEntry(Duration period, int page) {
        Renewal quote = this.access.renewal(period);
        Payment payment = quote.payment();
        Map<String, String> values = this.values(payment, quote.block());
        String duration = this.access.duration(period);
        String date = this.access.date(quote.expiry());
        values.put("PERIOD", duration);
        values.put("NEW_DATE", date);
        values.put("WAIT", this.access.duration(quote.waitTime().isNegative() ? Duration.ZERO : quote.waitTime()));
        values.put("STATUS", values.get("STATUS").replace("{WAIT}", values.get("WAIT")));
        values.put("PRODUCT", this.label("renew").replace("{PERIOD}", duration));
        values.put("RESULT", date);
        Runnable click = quote.block() == Block.NONE && payment.affordable()
                ? () -> this.confirm(values, payment, () -> this.openRenewal(page))
                : null;
        return button(this.screens.renewal, "entry", values, click);
    }

    private Button expansionEntry(int level, int page) {
        Expansion quote = this.access.expansion(level);
        Payment payment = quote.payment();
        Map<String, String> values = this.values(payment, quote.block());
        values.put("LEVEL", Integer.toString(level));
        values.put("SIZE", Integer.toString(quote.size()));
        values.put("MAX_LEVEL", Integer.toString(quote.maximum()));
        values.put("PRODUCT", this.label("expand").replace("{LEVEL}", values.get("LEVEL")));
        values.put("RESULT", this.label("expand-result").replace("{SIZE}", values.get("SIZE")));
        Runnable click = quote.block() == Block.NONE && payment.affordable()
                ? () -> this.confirm(values, payment, () -> this.openExpansion(page))
                : null;
        String state = switch (quote.block()) {
            case CURRENT -> "current";
            case PURCHASED -> "purchased";
            case NONE -> payment.affordable() ? "available" : "locked";
            default -> "locked";
        };
        return this.expansionIcon("entry", values, click, state);
    }

    private void openList(PanelScreen screen, int page, int entries, Map<String, String> summary, PageOpener reopen, IntFunction<Button> entry) {
        int capacity = (int) screen.pattern.stream().flatMap(row -> GuiPattern.slots(row).stream()).filter("entry"::equals).count();
        int pages = Math.max(1, Math.ceilDiv(entries, capacity));
        int current = Math.min(page, pages - 1);
        var indices = IntStream.range(current * capacity, Math.min(entries, (current + 1) * capacity)).iterator();
        Map<String, String> values = new HashMap<>(summary);
        values.put("PAGE", Integer.toString(current + 1));
        values.put("PAGES", Integer.toString(pages));
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> switch (key) {
            case "entry" -> indices.hasNext() ? entry.apply(indices.nextInt()) : null;
            case "previous" -> current > 0 ? button(screen, key, values, () -> reopen.open(current - 1)) : null;
            case "next" -> current + 1 < pages ? button(screen, key, values, () -> reopen.open(current + 1)) : null;
            case "back" -> button(screen, key, values, this.panel::open);
            case "legend-current" -> this.expansionIcon(key, values, null, "current");
            case "legend-available" -> this.expansionIcon(key, values, null, "available");
            case "legend-locked" -> this.expansionIcon(key, values, null, "locked");
            default -> button(screen, key, values, null);
        });
    }

    private Button expansionIcon(String key, Map<String, String> values, Runnable action, String state) {
        String material = this.screens.expansionMaterials.get(state);
        return new Button(this.panel.icon(this.screens.expansion.items.get(key), values, List.of(), material), action);
    }

    private void confirm(Map<String, String> values, Payment payment, Runnable back) {
        PanelScreen screen = this.screens.purchaseConfirm;
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> {
            Runnable onClick = switch (key) {
                case "buy" -> () -> {
                    if (payment.confirm().getAsBoolean()) {
                        back.run();
                    }
                };
                case "cancel" -> back;
                default -> null;
            };
            return button(screen, key, values, onClick);
        });
    }

    private Map<String, String> values(Payment payment, Block block) {
        String status = block.name().toLowerCase(Locale.ROOT);
        if (block == Block.NONE && !payment.affordable()) {
            status = payment.economyUnavailable() ? "economy" : "funds";
        }
        Map<String, String> values = new HashMap<>();
        values.put("TAG", this.access.tag());
        values.put("STATUS", this.label(status));
        values.put("COST", payment.cost().isEmpty() ? this.label("free") : payment.cost());
        values.put("MISSING", payment.missing().isEmpty() ? this.label("free") : payment.missing());
        return values;
    }

    private String label(String key) {
        return this.screens.purchaseLabels.getOrDefault(key, key);
    }

    private Button button(PanelScreen screen, String key, Map<String, String> values, Runnable action) {
        return new Button(this.panel.icon(screen.items.get(key), values, List.of()), action);
    }

    @FunctionalInterface
    private interface PageOpener {
        void open(int page);
    }
}
