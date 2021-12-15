package net.dzikoysk.funnyguilds.feature.hooks;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import org.bukkit.Bukkit;

public class FunnyTabHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    FunnyTabHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void init() {
        FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();
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

        Bukkit.getPluginManager().disablePlugin(plugin);

        super.init();
    }

}
