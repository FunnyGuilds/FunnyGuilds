package net.dzikoysk.funnyguilds.feature.gui.permission;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.RawString;
import net.dzikoysk.funnyguilds.config.message.MessageService;
import net.dzikoysk.funnyguilds.config.sections.PermissionsPanelConfiguration;
import net.dzikoysk.funnyguilds.feature.gui.GuiWindow;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.permission.member.GuildMemberPermissions;
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
 * GUI displaying the list of guild members for permission management.
 */
public class MemberPermissionsListGui {

    private static final int MEMBERS_PER_PAGE = 45; // 5 rows for members
    
    private final PluginConfiguration config;
    private final MessageService messageService;
    private final Guild guild;
    private final User viewer;
    private final int page;
    
    public MemberPermissionsListGui(
            PluginConfiguration config,
            MessageService messageService,
            Guild guild,
            User viewer,
            int page
    ) {
        this.config = config;
        this.messageService = messageService;
        this.guild = guild;
        this.viewer = viewer;
        this.page = page;
    }
    
    public void open(Player player) {
        PermissionsPanelConfiguration panelConfig = this.config.permissionsPanel;
        
        // Get list of members (excluding the owner)
        List<User> members = new ArrayList<>(this.guild.getMembers());
        members.remove(this.guild.getOwner());
        
        // Sort by role (deputies first) then by name
        members.sort(Comparator
                .<User, Integer>comparing(user -> this.guild.isDeputy(user) ? 0 : 1)
                .thenComparing(User::getName));
        
        int totalPages = Math.max(1, (int) Math.ceil((double) members.size() / MEMBERS_PER_PAGE));
        int currentPage = Math.max(0, Math.min(this.page, totalPages - 1));
        
        // Format title
        String title = new FunnyFormatter()
                .register("{TAG}", this.guild.getTag())
                .register("{GUILD}", this.guild.getName())
                .register("{PAGE}", String.valueOf(currentPage + 1))
                .register("{TOTAL_PAGES}", String.valueOf(totalPages))
                .replace(panelConfig.membersTitle.getValue());
        title = ChatUtils.colored(title);
        
        GuiWindow gui = new GuiWindow(title, panelConfig.membersRows);
        
        // Fill empty slots
        if (panelConfig.fillItem.enabled) {
            ItemStack fillItem = new ItemBuilder(panelConfig.fillItem.material)
                    .setName(panelConfig.fillItem.name.getValue(), true)
                    .getItem();
            gui.fillEmpty(fillItem);
        }
        
        // Add member heads
        int startIndex = currentPage * MEMBERS_PER_PAGE;
        int endIndex = Math.min(startIndex + MEMBERS_PER_PAGE, members.size());
        
        GuildPermissionsManager permissionsManager = this.guild.getPermissionsManager();
        
        for (int i = startIndex; i < endIndex; i++) {
            User member = members.get(i);
            int slot = i - startIndex;
            
            ItemStack head = createMemberHead(member, permissionsManager, panelConfig);
            
            gui.setItem(slot, head, event -> {
                event.setCancelled(true);
                
                // Open individual member permissions GUI
                new MemberPermissionsGui(
                        this.config,
                        this.messageService,
                        this.guild,
                        this.viewer,
                        member,
                        currentPage
                ).open(player);
            });
        }
        
        // Navigation buttons
        PermissionsPanelConfiguration.NavigationItems nav = panelConfig.navigation;
        
        // Previous page button
        if (currentPage > 0) {
            ItemStack prevItem = new ItemBuilder(nav.previousPageMaterial)
                    .setName(nav.previousPageName.getValue(), true)
                    .getItem();
            
            int finalCurrentPage = currentPage;
            gui.setItem(nav.previousPageSlot, prevItem, event -> {
                event.setCancelled(true);
                new MemberPermissionsListGui(
                        this.config,
                        this.messageService,
                        this.guild,
                        this.viewer,
                        finalCurrentPage - 1
                ).open(player);
            });
        }
        
        // Next page button
        if (currentPage < totalPages - 1) {
            ItemStack nextItem = new ItemBuilder(nav.nextPageMaterial)
                    .setName(nav.nextPageName.getValue(), true)
                    .getItem();
            
            int finalCurrentPage = currentPage;
            gui.setItem(nav.nextPageSlot, nextItem, event -> {
                event.setCancelled(true);
                new MemberPermissionsListGui(
                        this.config,
                        this.messageService,
                        this.guild,
                        this.viewer,
                        finalCurrentPage + 1
                ).open(player);
            });
        }
        
        // Back button
        ItemStack backItem = new ItemBuilder(nav.backMaterial)
                .setName(nav.backName.getValue(), true)
                .getItem();
        
        gui.setItem(nav.backSlot, backItem, event -> {
            event.setCancelled(true);
            player.closeInventory();
        });
        
        gui.open(player);
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack createMemberHead(User member, GuildPermissionsManager permissionsManager, PermissionsPanelConfiguration panelConfig) {
        GuildRole role = permissionsManager.getUserRole(member);
        boolean isOnline = member.isOnline();
        
        // Count overrides
        int overrideCount = permissionsManager.getPermissions(member.getUUID())
                .map(perms -> perms.getAllOverrides().size())
                .orElse(0);
        
        FunnyFormatter formatter = new FunnyFormatter()
                .register("{PLAYER}", member.getName())
                .register("{ROLE}", role.getDisplayName())
                .register("{OVERRIDE_COUNT}", String.valueOf(overrideCount));
        
        String name = formatter.replace(panelConfig.memberHead.name.getValue());
        
        List<RawString> loreSource = isOnline ? panelConfig.memberHead.loreOnline : panelConfig.memberHead.loreOffline;
        List<String> lore = new ArrayList<>();
        for (var line : loreSource) {
            lore.add(formatter.replace(line.getValue()));
        }
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUUID()));
            meta.setDisplayName(ChatUtils.colored(name));
            meta.setLore(lore.stream().map(ChatUtils::colored).toList());
            head.setItemMeta(meta);
        }
        
        return head;
    }
}
