package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;


import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.feature.placeholders.Placeholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Formatter;

import java.util.List;

public class HologramUpdateHandler implements Runnable {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;

    public HologramUpdateHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public void run() {
        HologramConfiguration hologramConfig = config.heartConfig.hologram;

        if (!hologramConfig.enabled) {
            return;
        }

        ItemStack item = new ItemStack(hologramConfig.item);
        FunnyHologramManager hologramManager = PluginHook.HOLOGRAPHIC_DISPLAYS;
        for (Guild guild : plugin.getGuildManager().getGuilds()) {
            Formatter formatter = Placeholders.GUILD_ALL.toFormatter(guild);
            List<String> lines = PandaStream.of(hologramConfig.displayedLines)
                    .map(formatter::format)
                    .map(ChatUtils::colored)
                    .map(line -> Placeholders.GUILD_MEMBERS_COLOR_CONTEXT
                            .format(line, Pair.of(ChatUtils.getLastColorBefore(line, "<online>"), guild)))
                    .toList();

            Bukkit.getScheduler().runTask(plugin, () -> hologramManager.getOrCreateHologram(guild).peek(hologram -> {
                hologram.clearHologram();

                if (hologramConfig.item != Material.AIR) {
                    hologram.addIconItem(item);
                }

                hologram.addLines(lines);
            }));
        }
    }
}
