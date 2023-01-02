package net.dzikoysk.funnyguilds.feature.command.user;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildAsyncTask;
import net.dzikoysk.funnyguilds.feature.scoreboard.nametag.NameTagGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.config.NumberRange;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class CreateCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.create.name}",
            description = "${user.create.description}",
            aliases = "${user.create.aliases}",
            permission = "funnyguilds.create",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, User user, String[] args) {
        when(!this.config.guildsEnabled, this.messages.adminGuildsDisabled);
        when(LocationUtils.checkWorld(player), this.messages.blockedWorld);
        when(user.hasGuild(), this.messages.generalHasGuild);

        if (args.length != 2) {
            when(args.length == 0, this.messages.generalNoTagGiven);
            when(args.length == 1, this.messages.generalNoNameGiven);

            throw new ValidationException(this.messages.createMore);
        }

        String tag = args[0];
        if (!this.config.guildTagKeepCase) {
            tag = this.config.guildTagUppercase ? tag.toUpperCase(Locale.ROOT) : tag.toLowerCase(Locale.ROOT);
        }

        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();
        World world = player.getWorld();

        when(tag.length() > this.config.createTagLength,
                FunnyFormatter.format(this.messages.createTagLength, "{LENGTH}", this.config.createTagLength));
        when(tag.length() < this.config.createTagMinLength,
                FunnyFormatter.format(this.messages.createTagMinLength, "{LENGTH}", this.config.createTagMinLength));
        when(name.length() > this.config.createNameLength,
                FunnyFormatter.format(this.messages.createNameLength, "{LENGTH}", this.config.createNameLength));
        when(name.length() < this.config.createNameMinLength,
                FunnyFormatter.format(this.messages.createNameMinLength, "{LENGTH}", this.config.createNameMinLength));

        when(!this.config.tagRegex.matches(tag), this.messages.createOLTag);
        when(!this.config.nameRegex.matches(name), this.messages.createOLName);

        when(this.guildManager.nameExists(name), this.messages.createNameExists);
        when(this.guildManager.tagExists(tag), this.messages.createTagExists);

        when(this.config.regionsEnabled && this.regionManager.isInRegion(guildLocation), this.messages.createIsNear);
        when(this.config.regionsEnabled && this.regionManager.isNearRegion(guildLocation), this.messages.createIsNear);

        if (this.config.checkForRestrictedGuildNames) {
            when(!GuildUtils.validateName(this.config, name), this.messages.restrictedGuildName);
            when(!GuildUtils.validateTag(this.config, tag), this.messages.restrictedGuildTag);
        }

        HeartConfiguration heartConfig = this.config.heart;

        if (this.config.regionsEnabled) {
            if (!heartConfig.usePlayerPositionForCenterY) {
                guildLocation.setY(heartConfig.createCenterY);
            }

            if (heartConfig.createEntityType != null && guildLocation.getBlockY() < (world.getMaxHeight() - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }

            int distance = this.config.regionSize + this.config.createDistance;
            if (this.config.enlargeItems != null) {
                distance += this.config.enlargeItems.size() * this.config.enlargeSize;
            }

            when(distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation),
                    FunnyFormatter.format(this.messages.createSpawn, "{DISTANCE}", distance));
        }

        if (this.config.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? this.config.rankCreateVip : this.config.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                FunnyFormatter formatter = new FunnyFormatter()
                        .register("{REQUIRED-FORMAT}", NumberRange.inRangeToString(requiredRank, this.config.pointsFormat))
                        .register("{POINTS}", requiredRank)
                        .register("{REQUIRED}", requiredRank)
                        .register("{POINTS-FORMAT}", NumberRange.inRangeToString(points, this.config.pointsFormat))
                        .register("{POINTS}", points);

                user.sendMessage(formatter.format(this.messages.createRank));
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items")
                ? this.config.createItemsVip
                : this.config.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items")
                ? this.config.requiredExperienceVip
                : this.config.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items")
                ? this.config.requiredMoneyVip
                : this.config.requiredMoney;

        if (player.getTotalExperience() < requiredExperience) {
            user.sendMessage(FunnyFormatter.format(this.messages.createExperience, "{EXP}", requiredExperience));
            return;
        }

        if (VaultHook.isEconomyHooked() && !VaultHook.canAfford(player, requiredMoney)) {
            user.sendMessage(FunnyFormatter.format(this.messages.createMoney, "{MONEY}", requiredMoney));
            return;
        }

        if (!ItemUtils.playerHasEnoughItems(player, requiredItems, this.messages.createItems)) {
            return;
        }

        if (HookManager.WORLD_GUARD.isPresent() && HookManager.WORLD_GUARD.get().isInNonGuildsRegion(guildLocation)) {
            user.sendMessage(this.messages.invalidGuildLocation);
            return;
        }

        Guild guild = new Guild(name, tag);
        guild.setOwner(user);
        guild.setLives(this.config.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(Instant.now().plus(this.config.validityStart).toEpochMilli());
        guild.setProtection(Instant.now().plus(this.config.warProtection).toEpochMilli());
        guild.setPvP(this.config.damageGuild);

        Location home = guildLocation.clone()
                .add(0.5D, -2.0D, 0.5D)
                .add(heartConfig.homeOffset);
        heartConfig.homeHeadPosition.setHeadPosition(home);
        guild.setHome(home);

        if (this.config.regionsEnabled) {
            Region region = new Region(guild, guildLocation, this.config.regionSize);
            guild.setRegion(region);

            WorldBorder border = world.getWorldBorder();
            double radius = border.getSize() / 2;
            FunnyBox bbox = FunnyBox.of(border.getCenter().toVector(), radius - this.config.createMinDistanceFromBorder,
                    world.getMaxHeight(), radius - this.config.createMinDistanceFromBorder);
            FunnyBox gbox = FunnyBox.of(region.getFirstCorner(), region.getSecondCorner());

            // border box does not contain guild box
            if (!bbox.contains(gbox)) {
                user.sendMessage(FunnyFormatter.format(this.messages.createNotEnoughDistanceFromBorder,
                        "{BORDER-MIN-DISTANCE}", this.config.createMinDistanceFromBorder));
                return;
            }
        }

        if (!SimpleEventHandler.handle(new GuildPreCreateEvent(EventCause.USER, user, guild))) {
            return;
        }

        player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
        player.setTotalExperience(player.getTotalExperience() - requiredExperience);

        if (VaultHook.isEconomyHooked()) {
            EconomyResponse withdrawResult = VaultHook.withdrawFromPlayerBank(player, requiredMoney);

            if (!withdrawResult.transactionSuccess()) {
                user.sendMessage(FunnyFormatter.format(this.messages.withdrawError, "{ERROR}", withdrawResult.errorMessage));
                return;
            }
        }

        if (this.config.regionsEnabled) {
            if (heartConfig.pasteSchematicOnCreation) {
                HookManager.WORLD_EDIT.peek(worldEdit -> {
                    if (!worldEdit.pasteSchematic(heartConfig.guildSchematicFile, guildLocation, heartConfig.pasteSchematicWithAir)) {
                        user.sendMessage(this.messages.createGuildCouldNotPasteSchematic);
                    }
                });
            }
            else if (heartConfig.createCenterSphere) {
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

                if (this.config.eventPhysics) {
                    guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                }
            }

            this.guildManager.spawnHeart(this.plugin.getGuildEntityHelper(), guild);
            //player.teleport(guildLocation);
            guild.teleportHome(player);
        }

        this.guildManager.addGuild(guild);
        user.setGuild(guild);

        guild.getRegion().peek(region -> this.regionManager.addRegion(region));

        this.plugin.scheduleFunnyTasks(
                new NameTagGlobalUpdateUserSyncTask(this.plugin.getIndividualNameTagManager(), user),
                new DatabaseUpdateGuildAsyncTask(this.plugin.getDataModel(), guild)
        );

        SimpleEventHandler.handle(new GuildCreateEvent(EventCause.USER, user, guild));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        user.sendMessage(formatter.format(this.messages.createGuild));
        this.broadcastMessage(formatter.format(this.messages.broadcastCreate));

        if (!this.config.giveRewardsForFirstGuild || this.guildManager.countGuilds() > 1) {
            return;
        }

        this.config.firstGuildRewards.forEach(item -> {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                return;
            }

            player.getInventory().addItem(item);
        });
    }

}
