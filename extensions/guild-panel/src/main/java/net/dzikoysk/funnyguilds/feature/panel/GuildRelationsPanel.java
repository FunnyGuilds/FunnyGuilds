package net.dzikoysk.funnyguilds.feature.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Relation;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.RelationAction;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.PanelScreen;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;
import net.dzikoysk.funnyguilds.module.api.PanelView.Button;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

final class GuildRelationsPanel {

    private final GuildPanel panel;
    private final PanelConfiguration.Screens screens;
    private final GuildPanelAccess access;

    GuildRelationsPanel(GuildPanel panel, PanelConfiguration configuration, GuildPanelAccess access) {
        this.panel = panel;
        this.screens = configuration.screens;
        this.access = access;
    }

    void open() {
        this.openList(Filter.ALLIES, 0);
    }

    private void openList(Filter filter, int page) {
        PanelScreen screen = this.screens.relations;
        List<Relation> targets = this.access.relations().stream()
                .filter(target -> switch (filter) {
                    case ALLIES -> target.ally();
                    case INVITATIONS -> target.outgoingInvite() || target.incomingInvite();
                    case WARS -> (target.incomingWar() || target.outgoingWar());
                    case BROWSE -> true;
                })
                .sorted(Comparator.comparing(Relation::tag, String.CASE_INSENSITIVE_ORDER))
                .toList();
        int capacity = (int) screen.pattern.stream().flatMap(row -> GuiPattern.slots(row).stream()).filter("entry"::equals).count();
        int pages = Math.max(1, Math.ceilDiv(targets.size(), capacity));
        int current = Math.min(page, pages - 1);
        Map<String, String> values = Map.of(
                "TAG", this.access.tag(),
                "PVP", this.panel.status(this.access.pvp()),
                "PAGE", String.valueOf(current + 1),
                "PAGES", String.valueOf(pages));
        Map<String, Runnable> actions = new HashMap<>();
        actions.put("allies", () -> this.openList(Filter.ALLIES, 0));
        actions.put("invitations", () -> this.openList(Filter.INVITATIONS, 0));
        actions.put("wars", () -> this.openList(Filter.WARS, 0));
        actions.put("browse", () -> this.openList(Filter.BROWSE, 0));
        actions.put("back", this.panel::open);
        actions.put("pvp", () -> {
            if (this.access.togglePvp()) {
                this.openList(filter, current);
            }
        });
        if (current > 0) {
            actions.put("previous", () -> this.openList(filter, current - 1));
        }
        if (current + 1 < pages) {
            actions.put("next", () -> this.openList(filter, current + 1));
        }
        var entries = targets.subList(current * capacity, Math.min(targets.size(), (current + 1) * capacity)).iterator();
        var emptyListIcon = (targets.isEmpty() && screen.items.containsKey("empty-list"))
                ? List.of(button(screen, "empty-list", values, null)).iterator() : List.<Button>of().iterator();
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> {
            if (key.equals("entry")) {
                if (entries.hasNext()) {
                    Relation target = entries.next();
                    return button(screen, key, this.targetValues(target), () -> this.openRelation(target.id(), filter, current));
                }
                return emptyListIcon.hasNext() ? emptyListIcon.next() : null;
            }
            if ((key.equals("previous") || key.equals("next")) && !actions.containsKey(key)) {
                return null;
            }
            return button(screen, key, values, actions.get(key));
        });
    }

    private void openRelation(UUID id, Filter filter, int page) {
        Relation target = this.access.relation(id).orElse(null);
        if (target == null) {
            this.openList(filter, page);
            return;
        }
        PanelScreen screen = this.screens.relation;
        Map<String, String> values = this.targetValues(target);
        Runnable back = () -> this.openList(filter, page);
        Runnable refresh = () -> this.openRelation(id, filter, page);
        Map<String, Runnable> actions = new HashMap<>();
        actions.put("back", back);
        if (target.ally()) {
            actions.put("ally-pvp", () -> this.apply(() -> this.access.changeRelation(RelationAction.ALLY_PVP, target.id()), refresh));
            actions.put("break", () -> this.confirm(target, "break", () -> this.access.changeRelation(RelationAction.BREAK, target.id()), refresh));
        } else {
            if (target.incomingInvite()) {
                actions.put("accept", () -> this.apply(() -> this.access.changeRelation(RelationAction.ACCEPT, target.id()), refresh));
            } else if (target.outgoingInvite()) {
                actions.put("revoke", () -> this.apply(() -> this.access.changeRelation(RelationAction.REVOKE, target.id()), refresh));
            } else {
                actions.put("invite", () -> this.apply(() -> this.access.changeRelation(RelationAction.INVITE, target.id()), refresh));
            }
            if (!target.outgoingWar()) {
                actions.put("declare", () -> this.confirm(target, "declare", () -> this.access.changeRelation(RelationAction.DECLARE, target.id()), refresh));
            }
        }
        if (target.outgoingWar()) {
            actions.put("withdraw", () -> this.confirm(target, "withdraw", () -> this.access.changeRelation(RelationAction.WITHDRAW, target.id()), refresh));
        }
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> switch (key) {
            case "invite", "accept", "revoke", "break", "ally-pvp", "declare", "withdraw" ->
                    actions.containsKey(key) ? button(screen, key, values, actions.get(key)) : null;
            default -> button(screen, key, values, actions.get(key));
        });
    }

    private void confirm(Relation target, String key, BooleanSupplier action, Runnable back) {
        PanelScreen screen = this.screens.confirm;
        Map<String, String> values = this.targetValues(target);
        String actionName = this.screens.relation.items.get(key).name;
        values.put("ACTION", PlainTextComponentSerializer.plainText().serialize(this.panel.render(actionName, values)));
        this.panel.show(screen.title, screen.rows, screen.pattern, values, token -> {
            Runnable onClick = switch (token) {
                case "accept" -> () -> this.apply(action, back);
                case "cancel" -> back;
                default -> null;
            };
            return button(screen, token, values, onClick);
        });
    }

    private void apply(BooleanSupplier action, Runnable refresh) {
        if (action.getAsBoolean()) {
            refresh.run();
        }
    }

    private Map<String, String> targetValues(Relation target) {
        Map<String, String> values = new HashMap<>(this.panel.allyPvpValues(target));
        values.put("TAG", this.access.tag());
        values.put("TARGET", target.name());
        values.put("TARGET_TAG", target.tag());
        List<String> states = new ArrayList<>();
        if (target.ally()) {
            states.add("ally");
        }
        if (target.incomingInvite()) {
            states.add("incoming-invitation");
        }
        if (target.outgoingInvite()) {
            states.add("outgoing-invitation");
        }
        if (target.outgoingWar()) {
            states.add(target.incomingWar() ? "mutual-war" : "outgoing-war");
        } else if (target.incomingWar()) {
            states.add("incoming-war");
        }
        if (states.isEmpty()) {
            states.add("neutral");
        }
        values.put("RELATION", String.join(", ", states.stream().map(key -> this.screens.relationLabels.getOrDefault(key, key)).toList()));
        return values;
    }

    private Button button(PanelScreen screen, String key, Map<String, String> values, Runnable action) {
        return new Button(this.panel.icon(screen.items.get(key), values, List.of()), action);
    }

    private enum Filter {
        ALLIES, INVITATIONS, WARS, BROWSE
    }
}
