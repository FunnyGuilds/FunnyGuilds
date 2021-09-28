package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.rank.RankUpdateGuildRequest;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.hooks.PluginHook;
import net.dzikoysk.funnyguilds.feature.hooks.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.utilities.text.Formatter;

import java.time.Instant;
import java.util.List;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class CreateCommand extends AbstractFunnyCommand {

    private static final int SKY_LIMIT = 256;

    @FunnyCommand(
        name = "${user.create.name}",
        description = "${user.create.description}",
        aliases = "${user.create.aliases}",
        permission = "funnyguilds.create",
        acceptsExceeded = true,
        playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when (!pluginConfiguration.guildsEnabled, messageConfiguration.adminGuildsDisabled);
        when (LocationUtils.checkWorld(player), messageConfiguration.blockedWorld);
        when (user.hasGuild(), messageConfiguration.generalHasGuild);

        if (args.length != 2) {
            when (args.length == 0, messageConfiguration.generalNoTagGiven);
            when (args.length == 1, messageConfiguration.generalNoNameGiven);

            throw new ValidationException(messageConfiguration.createMore);
        }

        String tag = args[0];

        if (!pluginConfiguration.guildTagKeepCase) {
            tag = pluginConfiguration.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }
        
        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();

        when (tag.length() > pluginConfiguration.createTagLength, messageConfiguration.createTagLength.replace("{LENGTH}", Integer.toString(pluginConfiguration.createTagLength)));
        when (tag.length() < pluginConfiguration.createTagMinLength, messageConfiguration.createTagMinLength.replace("{LENGTH}", Integer.toString(pluginConfiguration.createTagMinLength)));
        when (name.length() > pluginConfiguration.createNameLength, messageConfiguration.createNameLength.replace("{LENGTH}", Integer.toString(pluginConfiguration.createNameLength)));
        when (name.length() < pluginConfiguration.createNameMinLength, messageConfiguration.createNameMinLength.replace("{LENGTH}", Integer.toString(pluginConfiguration.createNameMinLength)));
        when (!tag.matches(pluginConfiguration.tagRegex.getPattern()), messageConfiguration.createOLTag);
        when (!name.matches(pluginConfiguration.nameRegex.getPattern()), messageConfiguration.createOLName);
        when (GuildUtils.nameExists(name), messageConfiguration.createNameExists);
        when (GuildUtils.tagExists(tag), messageConfiguration.createTagExists);
        when (pluginConfiguration.regionsEnabled && RegionUtils.isIn(guildLocation), messageConfiguration.createIsNear);
        when (pluginConfiguration.regionsEnabled && RegionUtils.isNear(guildLocation), messageConfiguration.createIsNear);

        if (pluginConfiguration.checkForRestrictedGuildNames) {
            when (!GuildUtils.isNameValid(name), messageConfiguration.restrictedGuildName);
            when (!GuildUtils.isTagValid(tag), messageConfiguration.restrictedGuildTag);
        }

        if (pluginConfiguration.regionsEnabled) {
            if (pluginConfiguration.createCenterY != 0) {
                guildLocation.setY(pluginConfiguration.createCenterY);
            }

            if (pluginConfiguration.createEntityType != null && guildLocation.getBlockY() < (SKY_LIMIT - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }
    
            int distance = pluginConfiguration.regionSize + pluginConfiguration.createDistance;
    
            if (pluginConfiguration.enlargeItems != null) {
                distance += pluginConfiguration.enlargeItems.size() * pluginConfiguration.enlargeSize;
            }
    
            when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation), messageConfiguration.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        }

        if (pluginConfiguration.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? pluginConfiguration.rankCreateVip : pluginConfiguration.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = messageConfiguration.createRank;
                
                msg = StringUtils.replace(msg, "{REQUIRED-FORMAT}", IntegerRange.inRangeToString(requiredRank, pluginConfiguration.pointsFormat).replace("{POINTS}", "{REQUIRED}"));
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS-FORMAT}", IntegerRange.inRangeToString(points, pluginConfiguration.pointsFormat));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items") ? pluginConfiguration.createItemsVip : pluginConfiguration.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items") ? pluginConfiguration.requiredExperienceVip : pluginConfiguration.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items") ? pluginConfiguration.requiredMoneyVip : pluginConfiguration.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            String msg = messageConfiguration.createExperience;
            msg = StringUtils.replace(msg, "{EXP}", String.valueOf(requiredExperience));
            player.sendMessage(msg);
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            String notEnoughMoneyMessage = messageConfiguration.createMoney;
            notEnoughMoneyMessage = StringUtils.replace(notEnoughMoneyMessage, "{MONEY}", Double.toString(requiredMoney));
            player.sendMessage(notEnoughMoneyMessage);
            return;
        }

        if (! ItemUtils.playerHasEnoughItems(player, requiredItems)) {
            return;
        }

        Guild guild = new Guild(name);
        guild.setTag(tag);
        guild.setOwner(user);
        guild.setLives(pluginConfiguration.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(Instant.now().plus(pluginConfiguration.validityStart).toEpochMilli());
        guild.setProtection(Instant.now().plus(pluginConfiguration.warProtection).toEpochMilli());
        guild.setPvP(pluginConfiguration.damageGuild);
        guild.setHome(guildLocation);

        if (pluginConfiguration.regionsEnabled) {
            Region region = new Region(guild, guildLocation, pluginConfiguration.regionSize);

            WorldBorder border = player.getWorld().getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - pluginConfiguration.createMinDistanceFromBorder, 256, radius - pluginConfiguration.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                String notEnoughDistanceMessage = messageConfiguration.createNotEnoughDistanceFromBorder;
                notEnoughDistanceMessage = StringUtils.replace(notEnoughDistanceMessage, "{BORDER-MIN-DISTANCE}", Double.toString(pluginConfiguration.createMinDistanceFromBorder));
                player.sendMessage(notEnoughDistanceMessage);
                return;
            }

            guild.setRegion(region);
        }

        if (!SimpleEventHandler.handle(new GuildPreCreateEvent(EventCause.USER, user, guild))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        player.setTotalExperience(player.getTotalExperience() - requiredExperience);

        if (VaultHook.isEconomyHooked()) {
            VaultHook.withdrawFromPlayerBank(player, requiredMoney);
        }
        
        if (pluginConfiguration.regionsEnabled) {
            if (pluginConfiguration.pasteSchematicOnCreation) {
                if (! PluginHook.WORLD_EDIT.pasteSchematic(pluginConfiguration.guildSchematicFile, guildLocation, pluginConfiguration.pasteSchematicWithAir)) {
                    player.sendMessage(messageConfiguration.createGuildCouldNotPasteSchematic);
                }
            }
            else if (pluginConfiguration.createCenterSphere) {
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
                
                if (pluginConfiguration.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }
            
            GuildUtils.spawnHeart(guild);
            player.teleport(guildLocation);
        }

        user.setGuild(guild);
        GuildUtils.addGuild(guild);

        if (pluginConfiguration.regionsEnabled) {
            RegionUtils.addRegion(guild.getRegion());
        }

        this.concurrencyManager.postRequests(
                new RankUpdateGuildRequest(guild),
                new PrefixGlobalAddGuildRequest(guild),
                new PrefixGlobalAddPlayerRequest(user.getName()),
                new DatabaseUpdateGuildRequest(guild)
        );

        Formatter formatter = new Formatter()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        player.sendMessage(formatter.format(messageConfiguration.createGuild));
        Bukkit.broadcastMessage(formatter.format(messageConfiguration.broadcastCreate));

        if (!pluginConfiguration.giveRewardsForFirstGuild) {
            return;
        }

        for (ItemStack item : pluginConfiguration.firstGuildRewards) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                continue;
            }

            player.getInventory().addItem(item);
        }
    }

}
