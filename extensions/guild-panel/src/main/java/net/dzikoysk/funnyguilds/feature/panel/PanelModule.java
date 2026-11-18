package net.dzikoysk.funnyguilds.feature.panel;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import net.dzikoysk.funnyguilds.config.sections.PanelConfiguration;
import net.dzikoysk.funnyguilds.feature.items.gui.GuiPattern;
import net.dzikoysk.funnyguilds.module.api.ExtensionCommands;
import net.dzikoysk.funnyguilds.module.api.GuildPanelAccess;
import net.dzikoysk.funnyguilds.module.api.PanelView;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class PanelModule {

    public static final String COMMAND_ID = "guild-panel.panel";

    private final Plugin plugin;
    private final GuildRoleNameInput nameInput;
    private final Function<Player, Optional<GuildPanelAccess>> access;
    private final Consumer<Player> onDisabled;
    private final Supplier<MiniMessage> miniMessage;
    private final GuildPanel.BaseIconFactory icons;
    private volatile PanelConfiguration configuration = new PanelConfiguration();

    public PanelModule(Plugin plugin, Function<Player, Optional<GuildPanelAccess>> access, Consumer<Player> onDisabled,
                       Supplier<MiniMessage> miniMessage, GuildPanel.BaseIconFactory icons) {
        this.plugin = plugin;
        this.nameInput = new GuildRoleNameInput(plugin);
        this.access = access;
        this.onDisabled = onDisabled;
        this.miniMessage = miniMessage;
        this.icons = icons;
    }

    public void registerCommands(ExtensionCommands commands) {
        commands.register(new ExtensionCommands.Command(COMMAND_ID, "panel", List.of(), new PanelCommand(this)));
    }

    public void open(Player player) {
        this.access.apply(player).ifPresent(session -> this.open(player, session));
    }

    private void open(Player player, GuildPanelAccess session) {
        if (!this.configuration.enabled) {
            this.onDisabled.accept(player);
            return;
        }
        if (!session.isActive()) {
            return;
        }
        this.nameInput.cancel(player.getUniqueId());
        new GuildPanel(this, player, session, this.miniMessage.get(), this.icons).open();
    }

    public void registerListeners() {
        this.plugin.getServer().getPluginManager().registerEvents(this.nameInput, this.plugin);
    }

    public void reload() {
        try {
            this.configuration = PanelConfigLoader.load(new File(this.plugin.getDataFolder(), "panel.yml"), this.plugin.getLogger());
            this.nameInput.clear();
        } catch (RuntimeException exception) {
            this.plugin.getLogger().log(Level.WARNING, "Could not load panel.yml. Using the previous panel layout.", exception);
        }
    }

    public PanelConfiguration configuration() {
        return this.configuration;
    }

    public GuildRoleNameInput nameInput() {
        return this.nameInput;
    }

    public void show(Player player, PanelView view, BooleanSupplier active) {
        ChestGui gui = new ChestGui(view.rows(), view.title(), this.plugin);
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.setOnGlobalDrag(event -> event.setCancelled(true));
        StaticPane pane = new StaticPane(9, view.rows(), Pane.Priority.NORMAL);
        for (int row = 0; row < view.pattern().size(); row++) {
            List<String> slots = GuiPattern.slots(view.pattern().get(row));
            for (int column = 0; column < slots.size(); column++) {
                String key = slots.get(column);
                PanelView.Button button = key.equals("empty") ? null : view.buttons().apply(key);
                if (button != null) {
                    pane.addItem(new GuiItem(button.icon(), event -> {
                        if (button.action() != null) {
                            this.scheduleClick(player, event.getView().getTopInventory(), active, button.action());
                        }
                    }), column, row);
                }
            }
        }
        gui.addPane(Slot.fromXY(0, 0), pane);
        gui.show(player);
    }

    private void scheduleClick(Player player, Inventory inventory, BooleanSupplier active, Runnable action) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (player.isOnline() && inventory.equals(player.getOpenInventory().getTopInventory())) {
                if (active.getAsBoolean()) {
                    action.run();
                } else {
                    player.closeInventory();
                }
            }
        });
    }
}
