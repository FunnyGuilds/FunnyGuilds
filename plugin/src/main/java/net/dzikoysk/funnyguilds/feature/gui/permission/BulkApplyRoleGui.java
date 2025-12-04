package net.dzikoysk.funnyguilds.feature.gui.permission;

import java.util.List;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.PermissionsPanelConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildPermissionsManager;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildRole;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * GUI for selecting a role to bulk apply permissions to.
 */
public class BulkApplyRoleGui {

    private final PluginConfiguration config;
    private final MessageService messageService;
    private final Guild guild;
    private final User viewer;
    private final User sourceMember;
    private final int previousPage;
    
    public BulkApplyRoleGui(
            PluginConfiguration config,
            MessageService messageService,
            Guild guild,
            User viewer,
            User sourceMember,
            int previousPage
    ) {
        this.config = config;
        this.messageService = messageService;
        this.guild = guild;
        this.viewer = viewer;
        this.sourceMember = sourceMember;
        this.previousPage = previousPage;
    }
    
    public void open(Player player) {
        PermissionsPanelConfiguration panelConfig = this.config.permissionsPanel;
        
        String title = ChatUtils.colored("&b&lWYBIERZ ROLĘ");
        
        GuiWindow gui = new GuiWindow(title, 3);
        
        // Fill empty slots
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }
        
        GuildPermissionsManager permissionsManager = this.guild.getPermissionsManager();
        
        // Deputy role button
        ItemStack deputyItem = new ItemBuilder(Material.GOLDEN_HELMET)
                .setName(ChatUtils.colored("&e&lZASTĘPCY"), false)
                .setLore(List.of(
                        ChatUtils.colored("&7Zastosuj uprawnienia do wszystkich"),
                        ChatUtils.colored("&7zastępców gildii."),
                        "",
                        ChatUtils.colored("&eKliknij, aby zastosować!")
                ), false)
                .getItem();
        
        gui.setItem(11, deputyItem, event -> {
            event.setCancelled(true);
            
            permissionsManager.applyToRole(
                    this.sourceMember.getUUID(),
                    GuildRole.DEPUTY,
                    this.viewer.getUUID()
            );
            
            this.messageService.getMessage(c -> c.permissionsPanelBulkApplySuccess)
                    .receiver(player)
                    .with("{SOURCE}", this.sourceMember.getName())
                    .with("{ROLE}", GuildRole.DEPUTY.getDisplayName())
                    .send();
            
            // Return to member permissions GUI
            new MemberPermissionsGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.sourceMember,
                    this.previousPage
            ).open(player);
        });
        
        // Member role button
        ItemStack memberItem = new ItemBuilder(Material.LEATHER_HELMET)
                .setName(ChatUtils.colored("&a&lCZŁONKOWIE"), false)
                .setLore(List.of(
                        ChatUtils.colored("&7Zastosuj uprawnienia do wszystkich"),
                        ChatUtils.colored("&7zwykłych członków gildii."),
                        "",
                        ChatUtils.colored("&aKliknij, aby zastosować!")
                ), false)
                .getItem();
        
        gui.setItem(15, memberItem, event -> {
            event.setCancelled(true);
            
            permissionsManager.applyToRole(
                    this.sourceMember.getUUID(),
                    GuildRole.MEMBER,
                    this.viewer.getUUID()
            );
            
            this.messageService.getMessage(c -> c.permissionsPanelBulkApplySuccess)
                    .receiver(player)
                    .with("{SOURCE}", this.sourceMember.getName())
                    .with("{ROLE}", GuildRole.MEMBER.getDisplayName())
                    .send();
            
            // Return to member permissions GUI
            new MemberPermissionsGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.sourceMember,
                    this.previousPage
            ).open(player);
        });
        
        // Back button
        PermissionsPanelConfiguration.NavigationItems nav = panelConfig.navigation;
        ItemStack backItem = new ItemBuilder(nav.backMaterial)
                .setName(nav.backName.getValue(), true)
                .getItem();
        
        gui.setItem(nav.backSlot, backItem, event -> {
            event.setCancelled(true);
            new MemberPermissionsGui(
                    this.config,
                    this.messageService,
                    this.guild,
                    this.viewer,
                    this.sourceMember,
                    this.previousPage
            ).open(player);
        });
        
        gui.open(player);
    }
}
