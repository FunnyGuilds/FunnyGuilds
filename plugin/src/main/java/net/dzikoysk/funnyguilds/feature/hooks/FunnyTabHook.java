package net.dzikoysk.funnyguilds.feature.hooks;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import org.bukkit.Bukkit;

public final class FunnyTabHook {

    private FunnyTabHook() {}

    public static void initFunnyDisabler() {
        final FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        logger.info("!!!      FUNNYTAB JEST JUZ      !!!");
        logger.info("!!!        PRZESTARZALY         !!!");
        logger.info("!!!                             !!!");
        logger.info("!!!       SKASUJ GO PRZED       !!!");
        logger.info("!!!    UZYWANIEM FUNNYGUILDS    !!!");
        logger.info("!!!                             !!!");
        logger.info("!!!     DO CZASU USUNIECIA      !!!");
        logger.info("!!!     FUNNYGUILDS BEDZIE      !!!");
        logger.info("!!!   AUTOMATYCZNIE WYLACZANE   !!!");
        logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        Bukkit.getPluginManager().disablePlugin(FunnyGuilds.getInstance());
    }
    
}
