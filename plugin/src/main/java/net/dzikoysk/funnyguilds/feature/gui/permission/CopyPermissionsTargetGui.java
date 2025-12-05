package net.dzikoysk.funnyguilds.feature.gui.permission;

import java.util.ArrayList;
import java.util.Comparator;
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
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * GUI for selecting targets to copy permissions to.
 */
public class CopyPermissionsTargetGui {

    private static final int MEMBERS_PER_PAGE = 45;
    
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final Guild guild;
    private final User viewer;
    private final User sourceMember;
    private final int previousPage;
    
    public CopyPermissionsTargetGui(
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
        
        // Get list of members (excluding the owner and source member)
        List<User> members = new ArrayList<>(this.guild.getMembers());
        members.remove(this.guild.getOwner());
        members.remove(this.sourceMember);
        
        // Sort by role (deputies first) then by name
        members.sort(Comparator
                .<User, Integer>comparing(user -> this.guild.isDeputy(user) ? 0 : 1)
                .thenComparing(User::getName));
        
        String title = ChatUtils.colored(panelConfig.copyTargetGui.title.getValue());
        
        GuiWindow gui = new GuiWindow(title, panelConfig.membersRows);
        
        // Fill empty slots
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }
        
        // Add member heads
        GuildPermissionsManager permissionsManager = this.guild.getPermissionsManager();
        
        for (int i = 0; i < members.size() && i < MEMBERS_PER_PAGE; i++) {
            User member = members.get(i);
            
            ItemStack head = createTargetHead(member, permissionsManager, panelConfig);
            
            gui.setItem(i, head, event -> {
                event.setCancelled(true);
                
                // Copy permissions
                permissionsManager.copyPermissions(
                        this.sourceMember.getUUID(),
                        member.getUUID(),
                        this.viewer.getUUID()
                );
                
                this.messageService.getMessage(c -> c.permissionsPanelCopySuccess)
                        .receiver(player)
                        .with("{SOURCE}", this.sourceMember.getName())
                        .with("{TARGET}", member.getName())
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
        }
        
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
    
    private ItemStack createTargetHead(User member, GuildPermissionsManager permissionsManager, PermissionsPanelConfiguration panelConfig) {
        GuildRole role = permissionsManager.getUserRole(member);
        boolean isOnline = member.isOnline();
        
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", member.getName())
                .register("{ROLE}", role.getDisplayName())
                .register("{STATUS}", isOnline ? "&aOnline" : "&cOffline");
        
        List<String> lore = new ArrayList<>();
        for (var line : panelConfig.copyTargetGui.headLore) {
            lore.add(formatter.replace(line.getValue()));
        }
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUUID()));
            meta.displayName(ChatUtils.toComponent("&a" + member.getName()));
            meta.lore(lore.stream().map(ChatUtils::toComponent).toList());
            head.setItemMeta(meta);
        }
        
        return head;
    }
}
