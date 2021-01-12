package net.dzikoysk.funnyguilds.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.guild.GuildUtils;
import net.dzikoysk.funnyguilds.basic.guild.Region;
import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import net.dzikoysk.funnyguilds.basic.user.User;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateGuildRequest;
import net.dzikoysk.funnyguilds.data.configs.MessageConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.hook.PluginHook;
import net.dzikoysk.funnyguilds.hook.VaultHook;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.panda_lang.utilities.commons.text.Formatter;

import java.util.List;

import static net.dzikoysk.funnyguilds.command.DefaultValidation.when;

@FunnyComponent
public final class CreateCommand {

    private static final int SKY_LIMIT = 256;

    @FunnyCommand(
        name = "${user.create.name}",
        description = "${user.create.description}",
        aliases = "${user.create.aliases}",
        permission = "funnyguilds.create",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(PluginConfiguration config, MessageConfiguration messages, Player player, User user, String[] args) {
        when (!config.guildsEnabled, messages.adminGuildsDisabled);
        when (LocationUtils.checkWorld(player), messages.blockedWorld);
        when (user.hasGuild(), messages.generalHasGuild);

        if (args.length != 2) {
            when (args.length == 0, messages.generalNoTagGiven);
            when (args.length == 1, messages.generalNoNameGiven);

            throw new ValidationException(messages.createMore);
        }

        String tag = args[0];

        if (!config.guildTagKeepCase) {
            tag = config.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }
        
        String name = args[1];

        when (tag.length() > config.createTagLength, messages.createTagLength.replace("{LENGTH}", Integer.toString(config.createTagLength)));
        when (tag.length() < config.createTagMinLength, messages.createTagMinLength.replace("{LENGTH}", Integer.toString(config.createTagMinLength)));
        when (name.length() > config.createNameLength, messages.createNameLength.replace("{LENGTH}", Integer.toString(config.createNameLength)));
        when (name.length() < config.createNameMinLength, messages.createNameMinLength.replace("{LENGTH}", Integer.toString(config.createNameMinLength)));
        when (!tag.matches(config.tagRegex.getPattern()), messages.createOLTag);
        when (!name.matches(config.nameRegex.getPattern()), messages.createOLName);
        when (GuildUtils.nameExists(name), messages.createNameExists);
        when (GuildUtils.tagExists(tag), messages.createTagExists);

        if (config.checkForRestrictedGuildNames) {
            when (!GuildUtils.isNameValid(name), messages.restrictedGuildName);
            when (!GuildUtils.isTagValid(tag), messages.restrictedGuildTag);
        }

        Location guildLocation = player.getLocation().getBlock().getLocation();

        if (config.regionsEnabled) {
            if (config.createCenterY != 0) {
                guildLocation.setY(config.createCenterY);
            }

            if (config.createEntityType != null && guildLocation.getBlockY() < (SKY_LIMIT - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }
    
            int distance = config.regionSize + config.createDistance;
    
            if (config.enlargeItems != null) {
                distance += config.enlargeItems.size() * config.enlargeSize;
            }
    
            when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation), messages.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        }

        if (config.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? config.rankCreateVip : config.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = messages.createRank;
                
                msg = StringUtils.replace(msg, "{REQUIRED-FORMAT}", IntegerRange.inRangeToString(requiredRank, config.pointsFormat).replace("{POINTS}", "{REQUIRED}"));
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS-FORMAT}", IntegerRange.inRangeToString(points, config.pointsFormat));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items") ? config.createItemsVip : config.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items") ? config.requiredExperienceVip : config.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items") ? config.requiredMoneyVip : config.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            String msg = messages.createExperience;
            msg = StringUtils.replace(msg, "{EXP}", String.valueOf(requiredExperience));
            player.sendMessage(msg);
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            String notEnoughMoneyMessage = messages.createMoney;
            notEnoughMoneyMessage = StringUtils.replace(notEnoughMoneyMessage, "{MONEY}", Double.toString(requiredMoney));
            player.sendMessage(notEnoughMoneyMessage);
            return;
        }

        if (! ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        if (config.regionsEnabled) {
            when (RegionUtils.isIn(guildLocation), messages.createIsNear);
            when (RegionUtils.isNear(guildLocation), messages.createIsNear);

            if (config.createMinDistanceFromBorder != -1) {
                WorldBorder border = player.getWorld().getWorldBorder();
                double borderSize = border.getSize() / 2;
                double borderX = border.getCenter().getX() + borderSize;
                double borderZ = border.getCenter().getZ() + borderSize;
                double distanceX = Math.abs(borderX) - Math.abs(player.getLocation().getX());
                double distanceZ = Math.abs(borderZ) - Math.abs(player.getLocation().getZ());

                if ( (distanceX < config.createMinDistanceFromBorder) || (distanceZ < config.createMinDistanceFromBorder) ) {
                    String notEnoughDistanceMessage = messages.createNotEnoughDistanceFromBorder;
                    notEnoughDistanceMessage = StringUtils.replace(notEnoughDistanceMessage, "{BORDER-MIN-DISTANCE}", Double.toString(config.createMinDistanceFromBorder));
                    player.sendMessage(notEnoughDistanceMessage);
                    return;
                }
            }
        }

        Guild guild = new Guild(name);
        guild.setTag(tag);
        guild.setOwner(user);
        guild.setLives(config.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(System.currentTimeMillis() + config.validityStart);
        guild.setAttacked(System.currentTimeMillis() - config.warWait + config.warProtection);
        guild.setPvP(config.damageGuild);
        guild.setHome(guildLocation);

        if (!SimpleEventHandler.handle(new GuildPreCreateEvent(EventCause.USER, user, guild))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        player.setTotalExperience(player.getTotalExperience() - requiredExperience);

        if (VaultHook.isEconomyHooked()) {
            VaultHook.withdrawFromPlayerBank(player, requiredMoney);
        }

        user.setGuild(guild);
        GuildUtils.addGuild(guild);
        
        if (config.regionsEnabled) {
            Region region = new Region(guild, guildLocation, config.regionSize);
            guild.setRegion(region);

            if (config.pasteSchematicOnCreation) {
                if (! PluginHook.WORLD_EDIT.pasteSchematic(config.guildSchematicFile, guildLocation, config.pasteSchematicWithAir)) {
                    player.sendMessage(messages.createGuildCouldNotPasteSchematic);
                }
            }
            else if (config.createCenterSphere) {
                for (Location locationInSphere : SpaceUtils.sphere(guildLocation, 4, 4, false, true, 0)) {
                    if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                        locationInSphere.getBlock().setType(Material.AIR);
                    }
                }

                for (Location locationInSphere : SpaceUtils.sphere(guildLocation, 4, 4, true, true, 0)) {
                    if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                        locationInSphere.getBlock().setType(Material.OBSIDIAN);
                    }
                }
                
                if (config.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }
            
            GuildUtils.spawnHeart(guild);
            player.teleport(guildLocation);
        }

        FunnyGuilds.getInstance().getConcurrencyManager().postRequests(
                new RankUpdateGuildRequest(guild),
                new PrefixGlobalAddGuildRequest(guild),
                new PrefixGlobalAddPlayerRequest(user.getName()),
                new DatabaseUpdateGuildRequest(guild)
        );

        Formatter formatter = new Formatter()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        player.sendMessage(formatter.format(messages.createGuild));
        Bukkit.broadcastMessage(formatter.format(messages.broadcastCreate));

        if (!config.giveRewardsForFirstGuild) {
            return;
        }

        for (ItemStack item : config.firstGuildRewards) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                continue;
            }

            player.getInventory().addItem(item);
        }
    }

}
