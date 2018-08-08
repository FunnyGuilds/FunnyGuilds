package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import org.bukkit.Bukkit;

public final class FunnyTabHook {

    public static void initFunnyDisabler() {
        FunnyGuildsLogger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        FunnyGuildsLogger.info("!!!      FUNNYTAB JEST JUZ      !!!");
        FunnyGuildsLogger.info("!!!        PRZESTARZALY         !!!");
        FunnyGuildsLogger.info("!!!                             !!!");
        FunnyGuildsLogger.info("!!!       SKASUJ GO PRZED       !!!");
        FunnyGuildsLogger.info("!!!    UZYWANIEM FUNNYGUILDS    !!!");
        FunnyGuildsLogger.info("!!!                             !!!");
        FunnyGuildsLogger.info("!!!     DO CZASU USUNIECIA      !!!");
        FunnyGuildsLogger.info("!!!     FUNNYGUILDS BEDZIE      !!!");
        FunnyGuildsLogger.info("!!!   AUTOMATYCZNIE WYLACZANE   !!!");
        FunnyGuildsLogger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        Bukkit.getPluginManager().disablePlugin(FunnyGuilds.getInstance());
    }
    
    private FunnyTabHook() {}
    
}
