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
        when (!this.pluginConfig.guildsEnabled, this.messageConfig.adminGuildsDisabled);
        when (LocationUtils.checkWorld(player), this.messageConfig.blockedWorld);
        when (user.hasGuild(), this.messageConfig.generalHasGuild);

        if (args.length != 2) {
            when (args.length == 0, this.messageConfig.generalNoTagGiven);
            when (args.length == 1, this.messageConfig.generalNoNameGiven);

            throw new ValidationException(this.messageConfig.createMore);
        }

        String tag = args[0];

        if (!this.pluginConfig.guildTagKeepCase) {
            tag = this.pluginConfig.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }
        
        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();

        when (tag.length() > this.pluginConfig.createTagLength, this.messageConfig.createTagLength.replace("{LENGTH}", Integer.toString(this.pluginConfig.createTagLength)));
        when (tag.length() < this.pluginConfig.createTagMinLength, this.messageConfig.createTagMinLength.replace("{LENGTH}", Integer.toString(this.pluginConfig.createTagMinLength)));
        when (name.length() > this.pluginConfig.createNameLength, this.messageConfig.createNameLength.replace("{LENGTH}", Integer.toString(this.pluginConfig.createNameLength)));
        when (name.length() < this.pluginConfig.createNameMinLength, this.messageConfig.createNameMinLength.replace("{LENGTH}", Integer.toString(this.pluginConfig.createNameMinLength)));
        when (!tag.matches(this.pluginConfig.tagRegex.getPattern()), this.messageConfig.createOLTag);
        when (!name.matches(this.pluginConfig.nameRegex.getPattern()), this.messageConfig.createOLName);
        when (GuildUtils.nameExists(name), this.messageConfig.createNameExists);
        when (GuildUtils.tagExists(tag), this.messageConfig.createTagExists);
        when (this.pluginConfig.regionsEnabled && RegionUtils.isIn(guildLocation), this.messageConfig.createIsNear);
        when (this.pluginConfig.regionsEnabled && RegionUtils.isNear(guildLocation), this.messageConfig.createIsNear);

        if (this.pluginConfig.checkForRestrictedGuildNames) {
            when (!GuildUtils.isNameValid(name), this.messageConfig.restrictedGuildName);
            when (!GuildUtils.isTagValid(tag), this.messageConfig.restrictedGuildTag);
        }

        if (this.pluginConfig.regionsEnabled) {
            if (this.pluginConfig.createCenterY != 0) {
                guildLocation.setY(this.pluginConfig.createCenterY);
            }

            if (this.pluginConfig.createEntityType != null && guildLocation.getBlockY() < (SKY_LIMIT - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }
    
            int distance = this.pluginConfig.regionSize + this.pluginConfig.createDistance;
    
            if (this.pluginConfig.enlargeItems != null) {
                distance += this.pluginConfig.enlargeItems.size() * this.pluginConfig.enlargeSize;
            }
    
            when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation), this.messageConfig.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        }

        if (this.pluginConfig.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? this.pluginConfig.rankCreateVip : this.pluginConfig.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = this.messageConfig.createRank;
                
                msg = StringUtils.replace(msg, "{REQUIRED-FORMAT}", IntegerRange.inRangeToString(requiredRank, this.pluginConfig.pointsFormat).replace("{POINTS}", "{REQUIRED}"));
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS-FORMAT}", IntegerRange.inRangeToString(points, this.pluginConfig.pointsFormat));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfig.createItemsVip : this.pluginConfig.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfig.requiredExperienceVip : this.pluginConfig.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfig.requiredMoneyVip : this.pluginConfig.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            String msg = this.messageConfig.createExperience;
            msg = StringUtils.replace(msg, "{EXP}", String.valueOf(requiredExperience));
            player.sendMessage(msg);
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            String notEnoughMoneyMessage = this.messageConfig.createMoney;
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
        guild.setLives(this.pluginConfig.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(Instant.now().plus(this.pluginConfig.validityStart).toEpochMilli());
        guild.setProtection(Instant.now().plus(this.pluginConfig.warProtection).toEpochMilli());
        guild.setPvP(this.pluginConfig.damageGuild);
        guild.setHome(guildLocation);

        if (this.pluginConfig.regionsEnabled) {
            Region region = new Region(guild, guildLocation, this.pluginConfig.regionSize);

            WorldBorder border = player.getWorld().getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - this.pluginConfig.createMinDistanceFromBorder, 256, radius - this.pluginConfig.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                String notEnoughDistanceMessage = this.messageConfig.createNotEnoughDistanceFromBorder;
                notEnoughDistanceMessage = StringUtils.replace(notEnoughDistanceMessage, "{BORDER-MIN-DISTANCE}", Double.toString(this.pluginConfig.createMinDistanceFromBorder));
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
        
        if (this.pluginConfig.regionsEnabled) {
            if (this.pluginConfig.pasteSchematicOnCreation) {
                if (! PluginHook.WORLD_EDIT.pasteSchematic(this.pluginConfig.guildSchematicFile, guildLocation, this.pluginConfig.pasteSchematicWithAir)) {
                    player.sendMessage(this.messageConfig.createGuildCouldNotPasteSchematic);
                }
            }
            else if (this.pluginConfig.createCenterSphere) {
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
                
                if (this.pluginConfig.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }
            
            GuildUtils.spawnHeart(guild);
            player.teleport(guildLocation);
        }

        user.setGuild(guild);
        GuildUtils.addGuild(guild);

        if (this.pluginConfig.regionsEnabled) {
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

        player.sendMessage(formatter.format(this.messageConfig.createGuild));
        Bukkit.broadcastMessage(formatter.format(this.messageConfig.broadcastCreate));

        if (!this.pluginConfig.giveRewardsForFirstGuild) {
            return;
        }

        for (ItemStack item : this.pluginConfig.firstGuildRewards) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                continue;
            }

            player.getInventory().addItem(item);
        }
    }

}
