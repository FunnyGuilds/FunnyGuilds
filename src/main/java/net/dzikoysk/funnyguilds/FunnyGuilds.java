package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.ScoreboardStack;
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.listener.*;
import net.dzikoysk.funnyguilds.listener.dynamic.DynamicListenerManager;
import net.dzikoysk.funnyguilds.listener.region.*;
import net.dzikoysk.funnyguilds.system.AsynchronouslyRepeater;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.nms.DescriptionChanger;
import net.dzikoysk.funnyguilds.util.nms.EntityUtil;
import net.dzikoysk.funnyguilds.util.nms.PacketExtension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds funnyguilds;
    
    private static String fullVersion;
    private static String mainVersion;

    private ConcurrencyManager concurrencyManager;
    private DynamicListenerManager dynamicListenerManager;
    
    private boolean disabling;
    private boolean forceDisabling;

    public FunnyGuilds() {
        funnyguilds = this;
    }

    @Override
    public void onLoad() {
        try {
           Class.forName("net.md_5.bungee.api.ChatColor"); 
        } catch (Exception spigotNeeded) {
            FunnyGuildsLogger.info("FunnyGuilds requires spigot to work, your server seems to be using something else");
            FunnyGuildsLogger.info("If you think that is not true - contact plugin developers");
            FunnyGuildsLogger.info("https://github.com/FunnyGuilds/FunnyGuilds");
            
            getServer().getPluginManager().disablePlugin(this);
            this.forceDisabling = true;
            
            return;
        }
        
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        DescriptionChanger descriptionChanger = new DescriptionChanger(super.getDescription());
        String[] versions = descriptionChanger.extractVersion();
        
        fullVersion = versions[0];
        mainVersion = versions[1];

        PluginConfig settings = Settings.getConfig();
        descriptionChanger.rename(settings.pluginName);

        this.concurrencyManager = new ConcurrencyManager(settings.concurrencyThreads);
        this.concurrencyManager.printStatus();

        Commands commands = new Commands();
        commands.register();

        this.dynamicListenerManager = new DynamicListenerManager(this);
    }

    @Override
    public void onEnable() {
        new ScoreboardStack(this).start();
        new Manager().start();
        new MetricsCollector(this).start();

        AsynchronouslyRepeater repeater = AsynchronouslyRepeater.getInstance();
        repeater.start();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GuiActionHandler(), this);
        pluginManager.registerEvents(new EntityDamage(), this);
        pluginManager.registerEvents(new EntityInteract(), this);
        pluginManager.registerEvents(new PlayerChat(), this);
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerLogin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);

        this.dynamicListenerManager.registerDynamic(
            () -> Settings.getConfig().regionsEnabled,
            new BlockBreak(),
            new BlockIgnite(),
            new BlockPlace(),
            new BucketAction(),
            new EntityExplode(),
            new HangingBreak(),
            new HangingPlace(),
            new PlayerCommand(),
            new PlayerInteract()
        );

        this.dynamicListenerManager.registerDynamic(() -> Settings.getConfig().regionsEnabled && Settings.getConfig().eventMove, new PlayerMove());
        this.dynamicListenerManager.registerDynamic(() -> Settings.getConfig().regionsEnabled && Settings.getConfig().eventPhysics, new BlockPhysics());
        this.dynamicListenerManager.registerDynamic(() -> Settings.getConfig().regionsEnabled  && Settings.getConfig().respawnInBase, new PlayerRespawn());
        this.dynamicListenerManager.reloadAll();
        this.patch();

        FunnyGuildsVersion.isNewAvailable(getServer().getConsoleSender(), true);
        PluginHook.init();
        
        FunnyGuildsLogger.info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        if (this.forceDisabling) {
            return;
        }
        
        disabling = true;

        this.dynamicListenerManager.unregisterAll();
        EntityUtil.despawn();
        AsynchronouslyRepeater.getInstance().stop();

        Manager.getInstance().stop();
        Manager.getInstance().save();

        funnyguilds = null;
    }

    private void patch() {
        PluginConfig config = Settings.getConfig();

        for (final Player player : this.getServer().getOnlinePlayers()) {
            this.getServer().getScheduler().runTask(this, () -> PacketExtension.registerPlayer(player));

            User user = User.get(player);
            user.getCache().getScoreboard();
            user.getCache().getDummy();
            user.getRank();

            if (config.playerlistEnable) {
                AbstractTablist.createTablist(config.playerList, config.playerListHeader, config.playerListFooter, config.playerListPing, player);
            }
        }

        for (Guild guild : GuildUtils.getGuilds()) {
            if (config.createEntityType != null) {
                EntityUtil.spawn(guild);
            }

            guild.updateRank();
        }
    }

    public boolean isDisabling() {
        return disabling;
    }

    public ConcurrencyManager getConcurrencyManager() {
        return concurrencyManager;
    }

    public DynamicListenerManager getDynamicListenerManager() {
        return dynamicListenerManager;
    }

    public static String getFullVersion() {
        return fullVersion;
    }
    
    public static String getMainVersion() {
        return mainVersion;
    }

    public static FunnyGuilds getInstance() {
        if (funnyguilds == null) {
            return new FunnyGuilds();
        }

        return funnyguilds;
    }

}
