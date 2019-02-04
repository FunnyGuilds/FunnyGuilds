package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import org.bukkit.Bukkit;

public final class FunnyTabHook {

    public static void initFunnyDisabler() {
        FunnyGuilds.getInstance().getPluginLogger().info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!      FUNNYTAB JEST JUZ      !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!        PRZESTARZALY         !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!                             !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!       SKASUJ GO PRZED       !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!    UZYWANIEM FUNNYGUILDS    !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!                             !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!     DO CZASU USUNIECIA      !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!     FUNNYGUILDS BEDZIE      !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!   AUTOMATYCZNIE WYLACZANE   !!!");
        FunnyGuilds.getInstance().getPluginLogger().info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        Bukkit.getPluginManager().disablePlugin(FunnyGuilds.getInstance());
    }
    
    private FunnyTabHook() {}
    
}
