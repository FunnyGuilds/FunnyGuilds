package net.dzikoysk.funnyguilds.feature.regen;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.util.YamlWrapper;
import net.dzikoysk.funnyguilds.shared.FunnyIOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import panda.std.Option;
import panda.std.Result;

/**
 * Handles serialization and deserialization of regeneration data to/from YAML files.
 */
public final class RegenerationDataSerializer {

    private RegenerationDataSerializer() {
    }

    /**
     * Saves destroyed blocks for a guild to a YAML file.
     *
     * @param dataFolder The data folder for regeneration files
     * @param guildId The guild UUID
     * @param blocks The list of destroyed blocks to save
     */
    public static void save(File dataFolder, UUID guildId, List<DestroyedBlock> blocks) {
        File file = new File(dataFolder, guildId.toString() + ".yml");
        
        if (blocks == null || blocks.isEmpty()) {
            // Delete the file if there are no blocks
            if (file.exists()) {
                file.delete();
            }
            return;
        }

        YamlWrapper wrapper = new YamlWrapper(file);
        
        // Clear existing data
        for (String key : wrapper.getKeys(false)) {
            wrapper.set(key, null);
        }
        
        // Save blocks
        int index = 0;
        for (DestroyedBlock block : blocks) {
            String path = "blocks." + index;
            Location loc = block.getLocation();
            
            wrapper.set(path + ".world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            wrapper.set(path + ".x", loc.getBlockX());
            wrapper.set(path + ".y", loc.getBlockY());
            wrapper.set(path + ".z", loc.getBlockZ());
            wrapper.set(path + ".material", block.getMaterial().name());
            wrapper.set(path + ".blockData", block.getBlockData().getAsString());
            wrapper.set(path + ".destroyedAt", block.getDestroyedAt().toEpochMilli());
            
            if (block.getTileEntityData() != null) {
                wrapper.set(path + ".tileEntityData", block.getTileEntityData());
            }
            
            index++;
        }
        
        wrapper.save();
    }

    /**
     * Loads destroyed blocks for a guild from a YAML file.
     *
     * @param dataFolder The data folder for regeneration files
     * @param guildId The guild UUID
     * @return List of destroyed blocks, or empty list if file doesn't exist
     */
    public static List<DestroyedBlock> load(File dataFolder, UUID guildId) {
        File file = new File(dataFolder, guildId.toString() + ".yml");
        List<DestroyedBlock> blocks = new ArrayList<>();
        
        if (!file.exists()) {
            return blocks;
        }

        YamlWrapper wrapper = new YamlWrapper(file);
        ConfigurationSection blocksSection = wrapper.getConfigurationSection("blocks");
        
        if (blocksSection == null) {
            return blocks;
        }

        for (String key : blocksSection.getKeys(false)) {
            String path = "blocks." + key;
            
            String worldName = wrapper.getString(path + ".world");
            int x = wrapper.getInt(path + ".x");
            int y = wrapper.getInt(path + ".y");
            int z = wrapper.getInt(path + ".z");
            String materialName = wrapper.getString(path + ".material");
            String blockDataString = wrapper.getString(path + ".blockData");
            long destroyedAtMillis = wrapper.getLong(path + ".destroyedAt");
            String tileEntityData = wrapper.getString(path + ".tileEntityData");
            
            if (worldName == null || materialName == null || blockDataString == null) {
                FunnyGuilds.getPluginLogger().warning("Skipping invalid regeneration block data for guild " + guildId);
                continue;
            }
            
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                FunnyGuilds.getPluginLogger().warning("World '" + worldName + "' not found for regeneration block, skipping");
                continue;
            }
            
            Material material;
            try {
                material = Material.valueOf(materialName);
            } catch (IllegalArgumentException e) {
                FunnyGuilds.getPluginLogger().warning("Invalid material '" + materialName + "' for regeneration block, skipping");
                continue;
            }
            
            Location location = new Location(world, x, y, z);
            Instant destroyedAt = Instant.ofEpochMilli(destroyedAtMillis);
            
            try {
                org.bukkit.block.data.BlockData blockData = Bukkit.createBlockData(material, blockDataString);
                DestroyedBlock block = new DestroyedBlock(location, material, blockData, destroyedAt, tileEntityData);
                blocks.add(block);
            } catch (IllegalArgumentException e) {
                // Try with just material if block data string is invalid
                try {
                    org.bukkit.block.data.BlockData blockData = material.createBlockData();
                    DestroyedBlock block = new DestroyedBlock(location, material, blockData, destroyedAt, tileEntityData);
                    blocks.add(block);
                } catch (Exception ex) {
                    FunnyGuilds.getPluginLogger().warning("Failed to create block data for material '" + materialName + "', skipping");
                }
            }
        }
        
        return blocks;
    }

    /**
     * Deletes the regeneration data file for a guild.
     *
     * @param dataFolder The data folder for regeneration files
     * @param guildId The guild UUID
     */
    public static void delete(File dataFolder, UUID guildId) {
        File file = new File(dataFolder, guildId.toString() + ".yml");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Gets all guild UUIDs that have regeneration data.
     *
     * @param dataFolder The data folder for regeneration files
     * @return List of guild UUIDs
     */
    public static List<UUID> getAllGuildIds(File dataFolder) {
        List<UUID> guildIds = new ArrayList<>();
        
        if (!dataFolder.exists() || !dataFolder.isDirectory()) {
            return guildIds;
        }
        
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return guildIds;
        }
        
        for (File file : files) {
            String name = file.getName();
            String uuidString = name.substring(0, name.length() - 4); // Remove .yml
            try {
                UUID uuid = UUID.fromString(uuidString);
                guildIds.add(uuid);
            } catch (IllegalArgumentException e) {
                // Invalid UUID, skip
            }
        }
        
        return guildIds;
    }
}
