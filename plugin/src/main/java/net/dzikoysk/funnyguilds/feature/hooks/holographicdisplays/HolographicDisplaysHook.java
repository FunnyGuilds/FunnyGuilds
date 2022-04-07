package net.dzikoysk.funnyguilds.feature.hooks.holographicdisplays;

import eu.okaeri.commons.bukkit.holographicdisplays.Holo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HologramConfiguration;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.feature.holograms.HologramsHook;
import net.dzikoysk.funnyguilds.feature.placeholders.PlaceholdersService;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Formatter;

public final class HolographicDisplaysHook extends HologramsHook implements Listener {

    private final FunnyGuilds plugin;
    private final PluginConfiguration config;
    private final Map<Guild, Holo> holograms = new ConcurrentHashMap<>();

    public HolographicDisplaysHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
        this.config = plugin.getPluginConfiguration();
    }

    @Override
    public HookInitResult init() {
        Runnable updateTask = () -> this.plugin.getGuildManager().getGuilds().forEach(this::update);
        long updateInterval = config.heart.hologram.updateInterval;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, updateTask, 100L, updateInterval);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        return HookInitResult.SUCCESS;
    }

    @Override
    public void update(@NotNull Guild guild) {
        HologramConfiguration holoConfig = this.config.heart.hologram;
        if (!holoConfig.enabled) {
            return;
        }

        Option<Location> guildCenter = guild.getCenter();
        if (guildCenter.isEmpty()) {
            return;
        }

        Holo holo = this.holograms.computeIfAbsent(guild, (g) -> Holo.create(this.plugin));
        holo.location(guildCenter.map(it -> it.add(holoConfig.locationCorrection)).get());
        holo.clear();

        if (holoConfig.item != Material.AIR) {
            holo.appendItem(new ItemStack(holoConfig.item));
        }

        PlaceholdersService placeholdersService = this.plugin.getPlaceholdersService();

        Formatter formatter = placeholdersService.getGuildPlaceholders()
                .toVariableFormatter(guild);

        holo.appendTexts(PandaStream.of(holoConfig.displayedLines)
                .map(formatter::format)
                .map(ChatUtils::colored)
                .map(formatter::format)
                .map(line -> placeholdersService.getGuildAlliesEnemiesPlaceholders().formatVariable(line, guild))
                .map(line -> placeholdersService.getGuildMembersPlaceholders()
                        .toVariableFormatter(Pair.of(ChatUtils.getLastColorBefore(line, "{MEMBERS}"), guild))
                        .format(line))
                .toList());

        holo.update();
    }

    @Override
    public void configUpdated() {
        if (config.heart.hologram.enabled) {
            return;
        }
        this.holograms.values().forEach(Holo::delete);
        this.holograms.clear();
    }

    @EventHandler
    public void handleGuildDelete(GuildDeleteEvent event) {
        Holo holo = this.holograms.remove(event.getGuild());
        if (holo == null) {
            return;
        }
        holo.delete();
    }

    @EventHandler
    public void handleGuildCreate(GuildCreateEvent event) {
        this.update(event.getGuild());
    }
}
