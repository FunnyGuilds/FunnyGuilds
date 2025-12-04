package net.dzikoysk.funnyguilds.feature.gui.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.PermissionsPanelConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissionType;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissions;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildPermissionsManager;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildRole;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for managing individual member's permissions.
 */
public class MemberPermissionsGui {

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final Guild guild;
    private final User viewer;
    private final User targetMember;
    private final int previousPage;
    
    public MemberPermissionsGui(
            PluginConfiguration config,
            MessageService messageService,
            Guild guild,
            User viewer,
            User targetMember,
            int previousPage
    ) {
        this.config = config;
        this.messageService = messageService;
        this.guild = guild;
        this.viewer = viewer;
        this.targetMember = targetMember;
        this.previousPage = previousPage;
    }
    
    public void open(Player player) {
        PermissionsPanelConfiguration panelConfig = this.config.permissionsPanel;
        GuildPermissionsManager permissionsManager = this.guild.getPermissionsManager();
        GuildRole role = permissionsManager.getUserRole(this.targetMember);
        
        // Format title
        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{PLAYER}", this.targetMember.getName())
                .register("{ROLE}", role.getDisplayName())
                .replace(panelConfig.permissionsTitle.getValue());
        title = ChatUtils.colored(title);
        
        GuiWindow gui = new GuiWindow(title, panelConfig.permissionsRows);
        
        // Fill empty slots
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }
        
        // Add permission items
        GuildMemberPermissionType[] permissionTypes = GuildMemberPermissionType.values();
        Optional<GuildMemberPermissions> memberPerms = permissionsManager.getPermissions(this.targetMember.getUUID());
        
        for (int i = 0; i < permissionTypes.length && i < 36; i++) { // Max 4 rows of permissions
            GuildMemberPermissionType permType = permissionTypes[i];
            
            boolean hasOverride = memberPerms.map(perms -> perms.hasOverride(permType)).orElse(false);
            boolean effectiveValue = permissionsManager.hasPermission(this.targetMember, permType);
            
            ItemStack item = createPermissionItem(permType, effectiveValue, hasOverride, role, memberPerms.orElse(null), panelConfig);
            
            gui.setItem(i, item, event -> {
                event.setCancelled(true);
                
                ClickType clickType = event.getClick();
                
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    // Reset to role default
                    if (permissionsManager.resetPermission(this.targetMember.getUUID(), permType)) {
                        this.messageService.getMessage(c -> c.permissionsPanelPermissionReset)
                                .receiver(player)
                                .with("{PLAYER}", this.targetMember.getName())
                                .with("{PERMISSION}", permType.getDisplayName())
                                .send();
                    }
                } else if (clickType == ClickType.LEFT) {
                    // Enable permission
                    permissionsManager.setPermission(
                            this.targetMember.getUUID(),
                            permType,
                            true,
                            this.viewer.getUUID()
                    );
                    this.messageService.getMessage(c -> c.permissionsPanelPermissionEnabled)
                            .receiver(player)
                            .with("{PLAYER}", this.targetMember.getName())
                            .with("{PERMISSION}", permType.getDisplayName())
                            .send();
                } else if (clickType == ClickType.RIGHT) {
                    // Disable permission
                    permissionsManager.setPermission(
                            this.targetMember.getUUID(),
                            permType,
                            false,
                            this.viewer.getUUID()
                    );
                    this.messageService.getMessage(c -> c.permissionsPanelPermissionDisabled)
                            .receiver(player)
                            .with("{PLAYER}", this.targetMember.getName())
                            .with("{PERMISSION}", permType.getDisplayName())
                            .send();
                }
                
                // Refresh the GUI
                this.open(player);
            });
        }
        
        // Action buttons
        PermissionsPanelConfiguration.ActionButtons actions = panelConfig.actionButtons;
        
        // Reset all button
        ItemStack resetAllItem = new ItemBuilder(actions.resetAllMaterial)
                .setName(actions.resetAllName.getValue(), true)
                .setLore(actions.resetAllLore.stream().map(r -> r.getValue()).toList(), true)
                .getItem();
        
        gui.setItem(actions.resetAllSlot, resetAllItem, event -> {
            event.setCancelled(true);
            permissionsManager.resetAllPermissions(this.targetMember.getUUID());
            this.messageService.getMessage(c -> c.permissionsPanelAllReset)
                    .receiver(player)
                    .with("{PLAYER}", this.targetMember.getName())
                    .send();
            this.open(player);
        });
        
        // Copy to others button
        ItemStack copyItem = new ItemBuilder(actions.copyMaterial)
                .setName(actions.copyName.getValue(), true)
                .setLore(actions.copyLore.stream().map(r -> r.getValue()).toList(), true)
                .getItem();
        
        gui.setItem(actions.copySlot, copyItem, event -> {
            event.setCancelled(true);
            // Open copy target selection GUI
            new CopyPermissionsTargetGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.targetMember,
                    this.previousPage
            ).open(player);
        });
        
        // Bulk apply to role button
        ItemStack bulkApplyItem = new ItemBuilder(actions.bulkApplyMaterial)
                .setName(actions.bulkApplyName.getValue(), true)
                .setLore(actions.bulkApplyLore.stream().map(r -> r.getValue()).toList(), true)
                .getItem();
        
        gui.setItem(actions.bulkApplySlot, bulkApplyItem, event -> {
            event.setCancelled(true);
            // Open role selection GUI
            new BulkApplyRoleGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.targetMember,
                    this.previousPage
            ).open(player);
        });
        
        // Navigation - Back button
        PermissionsPanelConfiguration.NavigationItems nav = panelConfig.navigation;
        ItemStack backItem = new ItemBuilder(nav.backMaterial)
                .setName(nav.backName.getValue(), true)
                .getItem();
        
        gui.setItem(nav.backSlot, backItem, event -> {
            event.setCancelled(true);
            new MemberPermissionsListGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.previousPage
            ).open(player);
        });
        
        gui.open(player);
    }
    
    private ItemStack createPermissionItem(
            GuildMemberPermissionType permType,
            boolean effectiveValue,
            boolean hasOverride,
            GuildRole role,
            GuildMemberPermissions memberPerms,
            PermissionsPanelConfiguration panelConfig
    ) {
        PermissionsPanelConfiguration.PermissionItemConfig itemConfig = panelConfig.permissionItem;
        
        // Choose material based on state
        Material material;
        if (hasOverride) {
            material = effectiveValue ? itemConfig.materialOnOverride : itemConfig.materialOffOverride;
        } else {
            material = effectiveValue ? itemConfig.materialOnRole : itemConfig.materialOffRole;
        }
        
        String status = effectiveValue ? itemConfig.statusOn : itemConfig.statusOff;
        String source = hasOverride ? itemConfig.sourceOverride : itemConfig.sourceRole.replace("{ROLE}", role.getDisplayName());
        
        // Get change info if available
        String changedBy = "";
        String changedAt = "";
        if (hasOverride && memberPerms != null) {
            GuildMemberPermissions.PermissionOverride override = memberPerms.getOverrideDetails(permType);
            if (override != null) {
                changedBy = override.getChangedBy() != null ? override.getChangedBy().toString() : "Nieznany";
                changedAt = override.getChangedAt() != null ? this.messageService.get(this.config.defaultLocale, c -> c.dateFormat).format(override.getChangedAt()) : "Nieznany";
            }
        }
        
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PERMISSION_NAME}", permType.getDisplayName())
                .register("{STATUS}", status)
                .register("{SOURCE}", source)
                .register("{CHANGED_BY}", changedBy)
                .register("{CHANGED_AT}", changedAt);
        
        String name = ChatUtils.colored("&e" + permType.getDisplayName());
        
        List<String> lore = new ArrayList<>();
        for (var line : itemConfig.lore) {
            lore.add(formatter.replace(line.getValue()));
        }
        
        return new ItemBuilder(material)
                .setName(name, true)
                .setLore(lore, true)
                .getItem();
    }
}
