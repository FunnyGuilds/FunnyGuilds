package net.dzikoysk.funnyguilds;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.command.Commands;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.listener.EntityDamage;
import net.dzikoysk.funnyguilds.listener.EntityInteract;
import net.dzikoysk.funnyguilds.listener.PlayerChat;
import net.dzikoysk.funnyguilds.listener.PlayerDeath;
import net.dzikoysk.funnyguilds.listener.PlayerJoin;
import net.dzikoysk.funnyguilds.listener.PlayerLogin;
import net.dzikoysk.funnyguilds.listener.PlayerQuit;
import net.dzikoysk.funnyguilds.listener.region.BlockBreak;
import net.dzikoysk.funnyguilds.listener.region.BlockIgnite;
import net.dzikoysk.funnyguilds.listener.region.BlockPhysics;
import net.dzikoysk.funnyguilds.listener.region.BlockPlace;
import net.dzikoysk.funnyguilds.listener.region.BucketAction;
import net.dzikoysk.funnyguilds.listener.region.EntityExplode;
import net.dzikoysk.funnyguilds.listener.region.PlayerCommand;
import net.dzikoysk.funnyguilds.listener.region.PlayerInteract;
import net.dzikoysk.funnyguilds.listener.region.PlayerMove;
import net.dzikoysk.funnyguilds.system.event.EventManager;
import net.dzikoysk.funnyguilds.util.FunnyLogger;
import net.dzikoysk.funnyguilds.util.Reloader;
import net.dzikoysk.funnyguilds.util.Version;
import net.dzikoysk.funnyguilds.util.element.gui.GuiActionHandler;
import net.dzikoysk.funnyguilds.util.element.tablist.AbstractTablist;
import net.dzikoysk.funnyguilds.util.hook.PluginHook;
import net.dzikoysk.funnyguilds.util.metrics.MetricsCollector;
import net.dzikoysk.funnyguilds.util.reflect.DescriptionChanger;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.reflect.PacketExtension;
import net.dzikoysk.funnyguilds.util.runnable.AsynchronouslyRepeater;
import net.dzikoysk.funnyguilds.util.runnable.ScoreboardStack;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class FunnyGuilds extends JavaPlugin {
    
    public final static Random RANDOM_INSTANCE = new Random();
    
    private static FunnyGuilds funnyguilds;
    private static String version;
    private boolean disabling;

    public FunnyGuilds() {
        funnyguilds = this;
    }

    public static String getVersion() {
        if (version != null) {
            return version;
        }
        
        String[] array = funnyguilds.getDescription().getVersion().split("-");
        if (array.length != 2) {
            return version = funnyguilds.getDescription().getVersion();
        }

        return version = array[0];
    }

    public static FunnyGuilds getInstance() {
        if (funnyguilds == null) {
            return new FunnyGuilds();
        }
        
        return funnyguilds;
    }

    @Override
    public void onLoad() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        new Reloader().init();
        new DescriptionChanger(getDescription()).name(Settings.getConfig().pluginName);
        new Commands().register();

        EventManager em = EventManager.getEventManager();
        em.load();
    }

    @Override
    public void onEnable() {
        new ScoreboardStack(this).start();
        new IndependentThread().start();
        new Manager().start();
        new AsynchronouslyRepeater(this).start();
        new MetricsCollector(this).start();

        EventManager em = EventManager.getEventManager();
        em.enable();

        PluginManager pm = Bukkit.getPluginManager();
        PluginConfig config = Settings.getConfig();
        
        pm.registerEvents(new GuiActionHandler(), this);
        pm.registerEvents(new EntityDamage(), this);
        pm.registerEvents(new EntityInteract(), this);
        pm.registerEvents(new PlayerChat(this), this);
        pm.registerEvents(new PlayerDeath(), this);
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerLogin(this), this);
        pm.registerEvents(new PlayerQuit(), this);

        if (config.regionsEnabled) {
            pm.registerEvents(new BlockBreak(), this);
            pm.registerEvents(new BlockIgnite(), this);
            pm.registerEvents(new BlockPlace(), this);
            pm.registerEvents(new BucketAction(), this);
            pm.registerEvents(new EntityExplode(this), this);
            pm.registerEvents(new PlayerCommand(), this);
            pm.registerEvents(new PlayerInteract(), this);

            if (config.eventMove) {
                pm.registerEvents(new PlayerMove(), this);
            }
            
            if (config.eventPhysics) {
                pm.registerEvents(new BlockPhysics(), this);
            }
        }
            
        patch();
        Version.isNewAvailable(getServer().getConsoleSender(), true);
        PluginHook.init();

        FunnyLogger.info("~ Created by FunnyGuilds Team ~");
    }

    @Override
    public void onDisable() {
        disabling = true;

        EntityUtil.despawn();
        EventManager.getEventManager().disable();

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

}
