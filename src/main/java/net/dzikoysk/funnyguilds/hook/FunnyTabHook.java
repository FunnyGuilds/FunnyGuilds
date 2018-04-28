package net.dzikoysk.funnyguilds.hook;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyLogger;
import org.bukkit.Bukkit;

public class FunnyTabHook {

    public static void initFunnyDisabler() {
        FunnyLogger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        FunnyLogger.info("!!!      FUNNYTAB JEST JUZ      !!!");
        FunnyLogger.info("!!!        PRZESTARZALY         !!!");
        FunnyLogger.info("!!!                             !!!");
        FunnyLogger.info("!!!       SKASUJ GO PRZED       !!!");
        FunnyLogger.info("!!!    UZYWANIEM FUNNYGUILDS    !!!");
        FunnyLogger.info("!!!                             !!!");
        FunnyLogger.info("!!!     DO CZASU USUNIECIA      !!!");
        FunnyLogger.info("!!!     FUNNYGUILDS BEDZIE      !!!");
        FunnyLogger.info("!!!   AUTOMATYCZNIE WYLACZANE   !!!");
        FunnyLogger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        Bukkit.getPluginManager().disablePlugin(FunnyGuilds.getInstance());
    }
}
