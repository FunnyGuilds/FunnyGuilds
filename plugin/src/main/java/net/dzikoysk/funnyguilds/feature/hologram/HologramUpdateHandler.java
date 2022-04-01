package net.dzikoysk.funnyguilds.feature.hologram;


import java.util.List;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.placeholders.GuildPlaceholders;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Formatter;

public class HologramUpdateHandler implements Runnable {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;

    public HologramUpdateHandler(FunnyGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public void run() {
        HologramConfiguration hologramConfig = config.heart.hologram;

        if (!hologramConfig.enabled) {
            return;
        }

        ItemStack item = new ItemStack(hologramConfig.item);
        HookManager.HOLOGRAPHIC_DISPLAYS.peek(hologramManager -> {
            for (Guild guild : plugin.getGuildManager().getGuilds()) {
                Formatter formatter = GuildPlaceholders.getOrInstallAllPlaceholders(this.plugin).toFormatter(guild);
                List<String> lines = PandaStream.of(hologramConfig.displayedLines)
                        .map(formatter::format)
                        .map(ChatUtils::colored)
                        .map(line -> GuildPlaceholders.getOrInstallAlliesEnemies(this.plugin).format(line, guild))
                        .map(line -> GuildPlaceholders.getOrInstallGuildMembers(this.plugin)
                                .format(line, Pair.of(ChatUtils.getLastColorBefore(line, "{MEMBERS}"), guild)))
                        .toList();

                Bukkit.getScheduler().runTask(plugin, () -> hologramManager.getOrCreateHologram(guild).peek(hologram -> {
                    hologram.clearHologram();

                    if (hologramConfig.item != Material.AIR) {
                        hologram.addIconItem(item);
                    }

                    hologram.addLines(lines);
                }));
            }
        });
    }
}
