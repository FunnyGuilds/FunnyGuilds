package net.dzikoysk.funnyguilds.feature.panel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Member;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess.Permission;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration.PanelScreen;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;
import net.dzikoysk.funnyguilds.module.api.PanelView.Button;
import net.dzikoysk.funnyguilds.guild.permission.GuildPermissionDecision;
import net.dzikoysk.funnyguilds.guild.permission.GuildRolePolicy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

final class GuildRolesPanel {

    private final GuildPanel panel;
    private final PanelModule module;
    private final PanelConfiguration.Screens screens;
    private final Player player;
    private final GuildPanelAccess access;

    GuildRolesPanel(GuildPanel panel, PanelModule module, PanelConfiguration config, Player player, GuildPanelAccess access) {
        this.panel = panel;
        this.module = module;
        this.screens = config.screens;
        this.player = player;
        this.access = access;
    }

    void roles(int page) {
        List<Button> entries = this.roleIds(true).map(role -> this.button(this.screens.roles, "entry",
                Map.of("NAME", this.roleName(role), "DETAIL", ""), () -> this.role(role, 0))).toList();
        this.show(this.label("roles"), entries, page, this.panel::open, this.access.owner()
                ? Map.of("new-role", this::createRole) : Map.of());
    }

    private void members(int page) {
        List<Button> entries = this.access.members().stream().sorted(Comparator.comparing(Member::name, String.CASE_INSENSITIVE_ORDER))
                .map(member -> this.playerHead(member, "member", this.roleName(this.access.selectedRole(member.id())),
                        () -> this.member(member, 0))).toList();
        Map<String, Runnable> controls = new HashMap<>();
        if (this.access.canInvite()) {
            controls.put("invite-player", () -> this.invitations(false, 0));
            controls.put("pending-invites", () -> this.invitations(true, 0));
        }
        this.show(this.label("members"), entries, page, () -> this.roles(0), controls);
    }

    private void invitations(boolean pending, int page) {
        if (!this.access.canInvite()) {
            this.members(0);
            return;
        }
        String key = pending ? "pending-invites" : "invite-player";
        List<Button> entries = this.access.inviteTargets(pending).stream()
                .sorted(Comparator.comparing(Member::name, String.CASE_INSENSITIVE_ORDER))
                .map(user -> this.playerHead(user, "invite-target", this.label(key), () -> {
                    this.access.changeInvitation(user.id(), pending);
                    this.invitations(pending, page);
                })).toList();
        this.show(this.label(pending ? "pending-view" : "invite-view"), entries, page, () -> this.members(0), Map.of());
    }

    private Button playerHead(Member member, String icon, String detail, Runnable action) {
        Button button = this.button(this.screens.roles, icon, Map.of("NAME", member.name(), "DETAIL", detail), action);
        if (button.icon().getItemMeta() instanceof SkullMeta head) {
            head.setPlayerProfile(this.player.getServer().createProfile(member.id(), member.name()));
            button.icon().setItemMeta(head);
        }
        return button;
    }

    private void role(UUID role, int page) {
        boolean editable = !role.equals(GuildRolePolicy.OWNER) && (this.access.owner()
                || role.equals(GuildRolePolicy.MEMBER) && this.access.canManageRoles());
        List<Button> entries = this.access.permissions().stream().map(permission -> this.permissionButton(permission,
                this.roleSetting(role, permission), this.access.roleDecision(role, permission),
                editable ? () -> this.change(() -> this.access.setRolePermission(role, permission,
                        next(this.roleSetting(role, permission))), () -> this.role(role, page)) : null)).toList();
        Map<String, Runnable> controls = new HashMap<>();
        if (this.access.owner()) {
            if (GuildRolePolicy.isCustom(role)) {
                controls.put("delete", () -> this.confirmDelete(role));
            } else if (role.equals(GuildRolePolicy.DEPUTY)) {
                controls.put("delegate", () -> this.delegation(0));
            }
        }
        this.show(this.roleName(role), entries, page, () -> this.roles(0), controls);
    }

    private void delegation(int page) {
        List<Button> entries = this.access.permissions().stream().filter(Permission::delegable)
                .map(permission -> this.permissionIcon("delegation-rule", Map.of(
                        "NAME", this.permissionName(permission),
                        "SETTING", this.label(this.isDelegated(permission) ? "allow" : "deny")),
                        () -> this.change(() -> this.access.setDelegation(permission, !this.isDelegated(permission)),
                                () -> this.delegation(page)), permission)).toList();
        this.show(this.label("delegate"), entries, page, () -> this.role(GuildRolePolicy.DEPUTY, 0), Map.of());
    }

    private boolean isDelegated(Permission permission) {
        return this.access.roles().delegatedPermissions().contains(permission.policyKey());
    }

    private void member(Member member, int page) {
        boolean editable = !member.owner() && (this.access.owner()
                || this.access.canManageRoles() && !member.deputy());
        List<Button> entries = this.access.permissions().stream().map(permission -> this.permissionButton(permission,
                this.override(member, permission), this.access.memberDecision(member.id(), permission),
                editable ? () -> this.change(() -> this.access.setOverride(member.id(), permission,
                        next(this.override(member, permission))), () -> this.member(member, page)) : null)).toList();
        Map<String, Runnable> controls = new HashMap<>();
        if (editable && !member.deputy()) {
            controls.put("assign", () -> this.assign(member, 0));
        }
        if (!member.owner() && this.access.canKick()) {
            controls.put("kick", () -> this.confirm(this.label("kick"), member.name(), () -> {
                if (this.access.kick(member.id())) {
                    if (this.panel.isActive()) {
                        this.members(0);
                    } else {
                        this.player.closeInventory();
                    }
                }
            }, () -> this.member(member, page)));
        }
        this.show(member.name(), entries, page, () -> this.members(0), controls);
    }

    private void assign(Member member, int page) {
        List<Button> entries = this.roleIds(false).map(role -> this.button(this.screens.roles, "entry", Map.of(
                "NAME", this.roleName(role), "DETAIL", member.name()),
                () -> this.change(() -> this.access.assignRole(member.id(), role), () -> this.member(member, 0)))).toList();
        this.show(this.label("assign"), entries, page, () -> this.member(member, 0), Map.of());
    }

    private void createRole() {
        if (!this.access.owner()) {
            return;
        }
        this.player.closeInventory();
        this.player.sendMessage(this.panel.render(this.screens.roleNamePrompt, Map.of()));
        this.module.nameInput().open(this.player, name -> {
            if (!this.panel.isActive()) {
                return;
            }
            if (name.equalsIgnoreCase("anuluj") || name.equalsIgnoreCase("cancel")) {
                this.roles(0);
                return;
            }
            UUID id = UUID.randomUUID();
            this.change(() -> this.access.createRole(id, name), () -> this.role(id, 0));
        }, this.access::nameInputTimedOut);
    }

    private void confirmDelete(UUID role) {
        this.confirm(this.label("delete"), this.roleName(role),
                () -> this.change(() -> this.access.deleteRole(role), () -> this.roles(0)),
                () -> this.role(role, 0));
    }

    private void confirm(String action, String target, Runnable accept, Runnable cancel) {
        PanelScreen screen = this.screens.confirm;
        Map<String, String> values = Map.of("ACTION", action, "TARGET", target,
                "TARGET_TAG", this.access.tag(), "RELATION", "");
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> {
            Runnable onClick = switch (key) {
                case "accept" -> accept;
                case "cancel" -> cancel;
                default -> null;
            };
            return this.button(screen, key, values, onClick);
        });
    }

    private void change(BooleanSupplier change, Runnable refresh) {
        if (change.getAsBoolean()) {
            refresh.run();
        }
    }

    private void show(String view, List<Button> entries, int page, Runnable back, Map<String, Runnable> actions) {
        PanelScreen screen = this.screens.roles;
        int capacity = (int) screen.pattern.stream().flatMap(row -> GuiPattern.slots(row).stream()).filter("entry"::equals).count();
        int pages = Math.max(1, Math.ceilDiv(entries.size(), capacity));
        int current = Math.min(page, pages - 1);
        var index = IntStream.range(current * capacity, Math.min(entries.size(), (current + 1) * capacity)).iterator();
        var empty = entries.isEmpty() ? List.of(this.button(screen, "empty-list", Map.of(), null)).iterator() : List.<Button>of().iterator();
        Map<String, Runnable> controls = new HashMap<>(actions);
        controls.put("roles", () -> this.roles(0));
        controls.put("members", () -> this.members(0));
        controls.put("back", back);
        if (current > 0) {
            controls.put("previous", () -> this.show(view, entries, current - 1, back, actions));
        }
        if (current + 1 < pages) {
            controls.put("next", () -> this.show(view, entries, current + 1, back, actions));
        }
        Map<String, String> values = Map.of("VIEW", view, "PAGE", Integer.toString(current + 1), "PAGES", Integer.toString(pages));
        this.panel.show(screen.title, screen.rows, screen.pattern, values, key -> switch (key) {
            case "entry" -> index.hasNext() ? entries.get(index.nextInt()) : empty.hasNext() ? empty.next() : null;
            case "new-role", "assign", "delete", "delegate", "invite-player", "pending-invites", "kick", "previous", "next" ->
                    controls.containsKey(key) ? this.button(screen, key, values, controls.get(key)) : null;
            default -> this.button(screen, key, values, controls.get(key));
        });
    }

    private Button permissionButton(Permission permission, Boolean setting, GuildPermissionDecision decision, Runnable action) {
        return this.permissionIcon("permission", Map.of(
                "NAME", this.permissionName(permission),
                "SETTING", setting == null ? this.label("inherit") : this.label(setting ? "allow" : "deny"),
                "EFFECTIVE", this.label(decision.allowed() ? "allow" : "deny"),
                "SOURCE", this.label("source-" + decision.source().name().toLowerCase(Locale.ROOT))), action, permission);
    }

    private Button permissionIcon(String key, Map<String, String> values, Runnable action, Permission permission) {
        String material = this.screens.permissionMaterials.get(permission.key());
        return new Button(this.panel.icon(this.screens.roles.items.get(key), values, List.of(), material), action);
    }

    private String permissionName(Permission permission) {
        String key = permission.key();
        return this.screens.permissionNames.getOrDefault(key, key);
    }

    private Boolean roleSetting(UUID role, Permission permission) {
        GuildRolePolicy.Role stored = this.access.roles().roles().get(role);
        return role.equals(GuildRolePolicy.OWNER) ? Boolean.TRUE : stored == null ? null : stored.permissions().get(permission.policyKey());
    }

    private Boolean override(Member member, Permission permission) {
        if (member.owner()) {
            return true;
        }
        return this.access.roles().overrides().getOrDefault(member.id(), Map.of()).get(permission.policyKey());
    }

    private Stream<UUID> roleIds(boolean includeLeadership) {
        Stream<UUID> builtIn = includeLeadership ? Stream.of(GuildRolePolicy.OWNER, GuildRolePolicy.DEPUTY, GuildRolePolicy.MEMBER)
                : Stream.of(GuildRolePolicy.MEMBER);
        return Stream.concat(builtIn, this.access.roles().roles().keySet().stream()
                .filter(GuildRolePolicy::isCustom).sorted(Comparator.comparing(this::roleName, String.CASE_INSENSITIVE_ORDER)));
    }

    private String roleName(UUID id) {
        if (id.equals(GuildRolePolicy.OWNER)) {
            return this.label("owner");
        }
        if (id.equals(GuildRolePolicy.DEPUTY)) {
            return this.label("deputy");
        }
        if (id.equals(GuildRolePolicy.MEMBER)) {
            return this.label("member");
        }
        GuildRolePolicy.Role role = this.access.roles().roles().get(id);
        return role == null ? this.label("member") : role.name();
    }

    private String label(String key) {
        return this.screens.roleLabels.getOrDefault(key, key);
    }

    private Button button(PanelScreen screen, String key, Map<String, String> values, Runnable action) {
        return new Button(this.panel.icon(screen.items.get(key), values, List.of()), action);
    }

    private static Boolean next(Boolean setting) {
        if (setting == null) {
            return Boolean.TRUE;
        }
        return setting ? Boolean.FALSE : null;
    }
}
