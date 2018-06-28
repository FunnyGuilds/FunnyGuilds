package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.concurrency.ConcurrencyManager;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.element.ScoreboardStack;
import net.dzikoysk.funnyguilds.element.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerKick;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.HangingBreak;
import net.dzikoysk.funnyguilds.listener.region.HangingPlace;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.system.AsynchronouslyRepeater;
import net.dzikoysk.funnyguilds.system.event.EventManager;
import net.dzikoysk.funnyguilds.util.DescriptionManager;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FunnyGuilds extends JavaPlugin {

    private static FunnyGuilds funnyguilds;
    
    private static String fullVersion;
    private static String mainVersion;

    private ConcurrencyManager concurrencyManager;
    private EventManager eventManager;
    private boolean disabling;

    public FunnyGuilds() {
        funnyguilds = this;
    }

    @Override
    public void onLoad() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        DescriptionManager descriptionManager = new DescriptionManager(super.getDescription());
        String[] versions = descriptionManager.extractVersion();
        
        fullVersion = versions[0];
        mainVersion = versions[1];

        PluginConfig settings = Settings.getConfig();
        descriptionManager.rename(settings.pluginName);

        this.concurrencyManager = new ConcurrencyManager(settings.concurrencyThreads);
        this.concurrencyManager.printStatus();

        Commands commands = new Commands();
        commands.register();

        this.eventManager = new EventManager();
        this.eventManager.load();
    }

    @Override
    public void onEnable() {
        new ScoreboardStack(this).start();
        new Manager().start();
        new MetricsCollector(this).start();

        AsynchronouslyRepeater repeater = AsynchronouslyRepeater.getInstance();
        repeater.start();

        PluginManager pluginManager = Bukkit.getPluginManager();
        PluginConfig config = Settings.getConfig();
        
        pluginManager.registerEvents(new GuiActionHandler(), this);
        pluginManager.registerEvents(new EntityDamage(), this);
        pluginManager.registerEvents(new EntityInteract(), this);
        pluginManager.registerEvents(new PlayerChat(), this);
        pluginManager.registerEvents(new PlayerDeath(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerKick(), this);
        pluginManager.registerEvents(new PlayerLogin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);

        if (config.regionsEnabled) {
            pluginManager.registerEvents(new BlockBreak(), this);
            pluginManager.registerEvents(new BlockIgnite(), this);
            pluginManager.registerEvents(new BlockPlace(), this);
            pluginManager.registerEvents(new BucketAction(), this);
            pluginManager.registerEvents(new EntityExplode(), this);
            pluginManager.registerEvents(new HangingBreak(), this);
            pluginManager.registerEvents(new HangingPlace(), this);
            pluginManager.registerEvents(new PlayerCommand(), this);
            pluginManager.registerEvents(new PlayerInteract(), this);

            if (config.eventMove) {
                pluginManager.registerEvents(new PlayerMove(), this);
            }
            
            if (config.eventPhysics) {
                pluginManager.registerEvents(new BlockPhysics(), this);
            }
        }

        this.eventManager.enable();
        this.patch();

        Version.isNewAvailable(getServer().getConsoleSender(), true);
        PluginHook.init();
        
        FunnyLogger.info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        disabling = true;

        EntityUtil.despawn();
        this.eventManager.disable();

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
            user.getScoreboard();
            user.getDummy();
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
