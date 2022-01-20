package net.dzikoysk.funnyguilds.feature.hooks.funnytab;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.FunnyGuildsLogger;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import org.bukkit.Bukkit;

public class FunnyTabHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public FunnyTabHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult earlyInit() {
        FunnyGuildsLogger logger = FunnyGuilds.getPluginLogger();
        logger.warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        logger.warning("!!!      FUNNYTAB JEST JUZ      !!!");
        logger.warning("!!!        PRZESTARZALY         !!!");
        logger.warning("!!!                             !!!");
        logger.warning("!!!       SKASUJ GO PRZED       !!!");
        logger.warning("!!!    UZYWANIEM FUNNYGUILDS    !!!");
        logger.warning("!!!                             !!!");
        logger.warning("!!!     DO CZASU USUNIECIA      !!!");
        logger.warning("!!!     FUNNYGUILDS BEDZIE      !!!");
        logger.warning("!!!   AUTOMATYCZNIE WYLACZANE   !!!");
        logger.warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        Bukkit.getPluginManager().disablePlugin(plugin);
        return HookInitResult.UNUSED;
    }

}
