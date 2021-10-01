package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.database.DatabaseUpdateGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddGuildRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalAddPlayerRequest;
import net.dzikoysk.funnyguilds.config.IntegerRange;
import net.dzikoysk.funnyguilds.config.subcomponents.HeartConfiguration;
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
        Location guildLocation = player.getLocation().getBlock().getLocation();

        when (tag.length() > config.createTagLength, messages.createTagLength.replace("{LENGTH}", Integer.toString(config.createTagLength)));
        when (tag.length() < config.createTagMinLength, messages.createTagMinLength.replace("{LENGTH}", Integer.toString(config.createTagMinLength)));
        when (name.length() > config.createNameLength, messages.createNameLength.replace("{LENGTH}", Integer.toString(config.createNameLength)));
        when (name.length() < config.createNameMinLength, messages.createNameMinLength.replace("{LENGTH}", Integer.toString(config.createNameMinLength)));
        when (!tag.matches(config.tagRegex.getPattern()), messages.createOLTag);
        when (!name.matches(config.nameRegex.getPattern()), messages.createOLName);
        when (GuildUtils.nameExists(name), messages.createNameExists);
        when (GuildUtils.tagExists(tag), messages.createTagExists);
        when (config.regionsEnabled && RegionUtils.isIn(guildLocation), messages.createIsNear);
        when (config.regionsEnabled && RegionUtils.isNear(guildLocation), messages.createIsNear);

        if (config.checkForRestrictedGuildNames) {
            when (!GuildUtils.isNameValid(name), messages.restrictedGuildName);
            when (!GuildUtils.isTagValid(tag), messages.restrictedGuildTag);
        }

        HeartConfiguration heart = config.heartConfig;

        if (config.regionsEnabled) {
            if (heart.createCenterY != 0) {
                guildLocation.setY(heart.createCenterY);
            }

            if (heart.createEntityType != null && guildLocation.getBlockY() < (SKY_LIMIT - 2)) {
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

        Guild guild = new Guild(name);
        guild.setTag(tag);
        guild.setOwner(user);
        guild.setLives(config.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(Instant.now().plus(config.validityStart).toEpochMilli());
        guild.setProtection(Instant.now().plus(config.warProtection).toEpochMilli());
        guild.setPvP(config.damageGuild);
        guild.setHome(guildLocation);

        if (config.regionsEnabled) {
            Region region = new Region(guild, guildLocation, config.regionSize);

            WorldBorder border = player.getWorld().getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - config.createMinDistanceFromBorder, 256, radius - config.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                String notEnoughDistanceMessage = messages.createNotEnoughDistanceFromBorder;
                notEnoughDistanceMessage = StringUtils.replace(notEnoughDistanceMessage, "{BORDER-MIN-DISTANCE}", Double.toString(config.createMinDistanceFromBorder));
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
        
        if (config.regionsEnabled) {
            if (heart.pasteSchematicOnCreation) {
                if (! PluginHook.WORLD_EDIT.pasteSchematic(heart.guildSchematicFile, guildLocation, heart.pasteSchematicWithAir)) {
                    player.sendMessage(messages.createGuildCouldNotPasteSchematic);
                }
            }
            else if (heart.createCenterSphere) {
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

        user.setGuild(guild);
        GuildUtils.addGuild(guild);

        if (config.regionsEnabled) {
            RegionUtils.addRegion(guild.getRegion());
        }

        this.concurrencyManager.postRequests(
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
