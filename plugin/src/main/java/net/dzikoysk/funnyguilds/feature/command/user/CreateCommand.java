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
        when (!this.pluginConfiguration.guildsEnabled, this.messageConfiguration.adminGuildsDisabled);
        when (LocationUtils.checkWorld(player), this.messageConfiguration.blockedWorld);
        when (user.hasGuild(), this.messageConfiguration.generalHasGuild);

        if (args.length != 2) {
            when (args.length == 0, this.messageConfiguration.generalNoTagGiven);
            when (args.length == 1, this.messageConfiguration.generalNoNameGiven);

            throw new ValidationException(this.messageConfiguration.createMore);
        }

        String tag = args[0];

        if (!this.pluginConfiguration.guildTagKeepCase) {
            tag = this.pluginConfiguration.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }
        
        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();

        when (tag.length() > this.pluginConfiguration.createTagLength, this.messageConfiguration.createTagLength.replace("{LENGTH}", Integer.toString(this.pluginConfiguration.createTagLength)));
        when (tag.length() < this.pluginConfiguration.createTagMinLength, this.messageConfiguration.createTagMinLength.replace("{LENGTH}", Integer.toString(this.pluginConfiguration.createTagMinLength)));
        when (name.length() > this.pluginConfiguration.createNameLength, this.messageConfiguration.createNameLength.replace("{LENGTH}", Integer.toString(this.pluginConfiguration.createNameLength)));
        when (name.length() < this.pluginConfiguration.createNameMinLength, this.messageConfiguration.createNameMinLength.replace("{LENGTH}", Integer.toString(this.pluginConfiguration.createNameMinLength)));
        when (!tag.matches(this.pluginConfiguration.tagRegex.getPattern()), this.messageConfiguration.createOLTag);
        when (!name.matches(this.pluginConfiguration.nameRegex.getPattern()), this.messageConfiguration.createOLName);
        when (GuildUtils.nameExists(name), this.messageConfiguration.createNameExists);
        when (GuildUtils.tagExists(tag), this.messageConfiguration.createTagExists);
        when (this.pluginConfiguration.regionsEnabled && RegionUtils.isIn(guildLocation), this.messageConfiguration.createIsNear);
        when (this.pluginConfiguration.regionsEnabled && RegionUtils.isNear(guildLocation), this.messageConfiguration.createIsNear);

        if (this.pluginConfiguration.checkForRestrictedGuildNames) {
            when (!GuildUtils.isNameValid(name), this.messageConfiguration.restrictedGuildName);
            when (!GuildUtils.isTagValid(tag), this.messageConfiguration.restrictedGuildTag);
        }

        if (this.pluginConfiguration.regionsEnabled) {
            if (this.pluginConfiguration.createCenterY != 0) {
                guildLocation.setY(this.pluginConfiguration.createCenterY);
            }

            if (this.pluginConfiguration.createEntityType != null && guildLocation.getBlockY() < (SKY_LIMIT - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }
    
            int distance = this.pluginConfiguration.regionSize + this.pluginConfiguration.createDistance;
    
            if (this.pluginConfiguration.enlargeItems != null) {
                distance += this.pluginConfiguration.enlargeItems.size() * this.pluginConfiguration.enlargeSize;
            }
    
            when (distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation), this.messageConfiguration.createSpawn.replace("{DISTANCE}", Integer.toString(distance)));
        }

        if (this.pluginConfiguration.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? this.pluginConfiguration.rankCreateVip : this.pluginConfiguration.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = this.messageConfiguration.createRank;
                
                msg = StringUtils.replace(msg, "{REQUIRED-FORMAT}", IntegerRange.inRangeToString(requiredRank, this.pluginConfiguration.pointsFormat).replace("{POINTS}", "{REQUIRED}"));
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS-FORMAT}", IntegerRange.inRangeToString(points, this.pluginConfiguration.pointsFormat));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfiguration.createItemsVip : this.pluginConfiguration.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfiguration.requiredExperienceVip : this.pluginConfiguration.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items") ? this.pluginConfiguration.requiredMoneyVip : this.pluginConfiguration.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            String msg = this.messageConfiguration.createExperience;
            msg = StringUtils.replace(msg, "{EXP}", String.valueOf(requiredExperience));
            player.sendMessage(msg);
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            String notEnoughMoneyMessage = this.messageConfiguration.createMoney;
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
        guild.setLives(this.pluginConfiguration.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(Instant.now().plus(this.pluginConfiguration.validityStart).toEpochMilli());
        guild.setProtection(Instant.now().plus(this.pluginConfiguration.warProtection).toEpochMilli());
        guild.setPvP(this.pluginConfiguration.damageGuild);
        guild.setHome(guildLocation);

        if (this.pluginConfiguration.regionsEnabled) {
            Region region = new Region(guild, guildLocation, this.pluginConfiguration.regionSize);

            WorldBorder border = player.getWorld().getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - this.pluginConfiguration.createMinDistanceFromBorder, 256, radius - this.pluginConfiguration.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                String notEnoughDistanceMessage = this.messageConfiguration.createNotEnoughDistanceFromBorder;
                notEnoughDistanceMessage = StringUtils.replace(notEnoughDistanceMessage, "{BORDER-MIN-DISTANCE}", Double.toString(this.pluginConfiguration.createMinDistanceFromBorder));
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
        
        if (this.pluginConfiguration.regionsEnabled) {
            if (this.pluginConfiguration.pasteSchematicOnCreation) {
                if (! PluginHook.WORLD_EDIT.pasteSchematic(this.pluginConfiguration.guildSchematicFile, guildLocation, this.pluginConfiguration.pasteSchematicWithAir)) {
                    player.sendMessage(this.messageConfiguration.createGuildCouldNotPasteSchematic);
                }
            }
            else if (this.pluginConfiguration.createCenterSphere) {
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
                
                if (this.pluginConfiguration.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }
            
            GuildUtils.spawnHeart(guild);
            player.teleport(guildLocation);
        }

        user.setGuild(guild);
        GuildUtils.addGuild(guild);

        if (this.pluginConfiguration.regionsEnabled) {
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

        player.sendMessage(formatter.format(this.messageConfiguration.createGuild));
        Bukkit.broadcastMessage(formatter.format(this.messageConfiguration.broadcastCreate));

        if (!this.pluginConfiguration.giveRewardsForFirstGuild) {
            return;
        }

        for (ItemStack item : this.pluginConfiguration.firstGuildRewards) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                continue;
            }

            player.getInventory().addItem(item);
        }
    }

}
