package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildAsyncTask;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
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
        when(!this.config.guildsEnabled, config -> config.adminGuildsDisabled);
        when(LocationUtils.checkWorld(player), config -> config.blockedWorld);
        when(user.hasGuild(), config -> config.generalHasGuild);

        if (args.length != 2) {
            when(args.length == 0, config -> config.generalNoTagGiven);
            when(args.length == 1, config -> config.generalNoNameGiven);

            throw new InternalValidationException(config -> config.createMore);
        }

        String tag = args[0];
        if (!this.config.guildTagKeepCase) {
            tag = this.config.guildTagUppercase ? tag.toUpperCase(Locale.ROOT) : tag.toLowerCase(Locale.ROOT);
        }

        String name = args[1];
        Location guildLocation = player.getLocation().getBlock().getLocation();
        World world = player.getWorld();

        when(tag.length() > this.config.createTagLength,
                config -> config.createTagLength, Replacement.string("{LENGTH}", this.config.createTagLength));
        when(tag.length() < this.config.createTagMinLength,
                config -> config.createTagMinLength, Replacement.string("{LENGTH}", this.config.createTagMinLength));
        when(name.length() > this.config.createNameLength,
                config -> config.createNameLength, Replacement.string("{LENGTH}", this.config.createNameLength));
        when(name.length() < this.config.createNameMinLength,
                config -> config.createNameMinLength, Replacement.string("{LENGTH}", this.config.createNameMinLength));

        when(!this.config.tagRegex.matches(tag), config -> config.createOLTag);
        when(!this.config.nameRegex.matches(name), config -> config.createOLName);

        when(this.guildManager.nameExists(name), config -> config.createNameExists);
        when(this.guildManager.tagExists(tag), config -> config.createTagExists);

        when(this.config.regionsEnabled && this.regionManager.isInRegion(guildLocation), config -> config.createIsNear);
        when(this.config.regionsEnabled && this.regionManager.isNearRegion(guildLocation), config -> config.createIsNear);

        if (this.config.checkForRestrictedGuildNames) {
            when(!GuildUtils.validateName(this.config, name), config -> config.restrictedGuildName);
            when(!GuildUtils.validateTag(this.config, tag), config -> config.restrictedGuildTag);
        }

        HeartConfiguration heartConfig = this.config.heart;

        if (this.config.regionsEnabled) {
            heartConfig.center.prepareCenterLocation(guildLocation);

            if (heartConfig.createEntityType != null && guildLocation.getBlockY() < (world.getMaxHeight() - 2)) {
                guildLocation.setY(guildLocation.getBlockY() + 2);
            }

            int distance = this.config.regionSize + this.config.createDistance;
            if (this.config.enlargeItems != null) {
                distance += this.config.enlargeItems.size() * this.config.enlargeSize;
            }

            when(distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation),
                    config -> config.createSpawn, Replacement.string("{DISTANCE}", distance));
        }

        GuildItemSet activeSet = this.guildItemSetService.getSetForPlayer(player);
        {
            if (player.hasPermission(this.itemsConfiguration.adminItemsBypassPermission)) {
                this.messageService.getMessage(config -> config.itemsAdminBypass)
                        .receiver(player)
                        .send();
            } else {
                ItemRequirementResult result = this.guildItemRequirementChecker.check(player, user, activeSet, this.itemsConfiguration);

                if (!result.meetsAll()) {
                    this.messageService.getMessage(config -> config.itemsRequirementsNotMet)
                            .receiver(player)
                            .send();

                    if (!result.isMeetsMoney() && activeSet.requirements.moneyEnabled) {
                        double current = VaultHook.isEconomyHooked() ? VaultHook.accountBalance(player) : 0;
                        this.messageService.getMessage(config -> config.itemsRequirementMoney)
                                .receiver(player)
                                .with("{CURRENT}", String.format("%.0f", current))
                                .with("{REQUIRED}", String.format("%.0f", activeSet.requiredMoney))
                                .send();
                    }
                    if (!result.isMeetsExperience() && activeSet.requirements.experienceEnabled) {
                        this.messageService.getMessage(config -> config.itemsRequirementExperience)
                                .receiver(player)
                                .with("{CURRENT}", player.getLevel())
                                .with("{REQUIRED}", activeSet.requiredExperience)
                                .send();
                    }
                    if (!result.isMeetsRank() && activeSet.requirements.rankEnabled) {
                        this.messageService.getMessage(config -> config.itemsRequirementRank)
                                .receiver(player)
                                .with("{CURRENT}", user.getRank().getPoints())
                                .with("{REQUIRED}", activeSet.requiredRank)
                                .send();
                    }
                    if (activeSet.requirements.itemsEnabled) {
                        boolean hasAnyMissing = result.getItemCounts().values().stream().anyMatch(c -> !c.isMet());
                        if (hasAnyMissing) {
                            this.messageService.getMessage(config -> config.itemsRequirementItemsHeader)
                                    .receiver(player)
                                    .send();
                            for (Map.Entry<String, ItemCountResult> entry : result.getItemCounts().entrySet()) {
                                    ItemCountResult counts = entry.getValue();
                                    if (!counts.isMet()) {
                                        this.messageService.getMessage(config -> config.itemsRequirementItemLine)
                                                .receiver(player)
                                                .with("{ITEM}", counts.getDisplayName())
                                                .with("{KEY}", entry.getKey())
                                                .with("{CURRENT}", counts.getTotal())
                                                .with("{REQUIRED}", counts.getRequired())
                                                .send();
                                    }
                                }
                        }
                    }
                    return;
                }
            }
        }

        if (HookManager.WORLD_GUARD.isPresent() && HookManager.WORLD_GUARD.get().isInNonGuildsRegion(guildLocation)) {
            this.messageService.getMessage(config -> config.invalidGuildLocation)
                    .receiver(player)
                    .send();
            return;
        }

        Guild guild = new Guild(name, tag);
        guild.setOwner(user);
        guild.setLives(this.config.warLives);
        guild.setValidity(Instant.now().plus(this.config.validityStart));
        guild.setProtection(Instant.now().plus(this.config.warProtection));
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

            if (!bbox.contains(gbox)) {
                this.messageService.getMessage(config -> config.createNotEnoughDistanceFromBorder)
                        .receiver(player)
                        .with("{BORDER-MIN-DISTANCE}", this.config.createMinDistanceFromBorder)
                        .send();
                return;
            }
        }

        if (!SimpleEventHandler.handle(new GuildPreCreateEvent(EventCause.USER, user, guild))) {
            return;
        }

        boolean adminBypass = player.hasPermission(this.itemsConfiguration.adminItemsBypassPermission);

        if (!adminBypass) {
            player.getInventory().removeItem(ItemUtils.toArray(ItemUtils.buildRequiredItems(
                    activeSet, this.itemsConfiguration)));

            if (activeSet.requirements.experienceEnabled && activeSet.requiredExperience > 0) {
                int newLevel = player.getLevel() - activeSet.requiredExperience;
                player.setLevel(Math.max(0, newLevel));
            }

            if (VaultHook.isEconomyHooked() && activeSet.requirements.moneyEnabled && activeSet.requiredMoney > 0) {
                EconomyResponse withdrawResult = VaultHook.withdrawFromPlayerBank(player, activeSet.requiredMoney);

                if (!withdrawResult.transactionSuccess()) {
                    this.messageService.getMessage(config -> config.withdrawError)
                            .receiver(player)
                            .with("{ERROR}", withdrawResult.errorMessage)
                            .send();
                    return;
                }
            }
        }

        if (this.config.regionsEnabled) {
            if (heartConfig.pasteSchematicOnCreation) {
                HookManager.WORLD_EDIT.peek(worldEdit -> {
                    if (!worldEdit.pasteSchematic(heartConfig.guildSchematicFile, guildLocation, heartConfig.pasteSchematicWithAir)) {
                        this.messageService.getMessage(config -> config.createGuildCouldNotPasteSchematic)
                                .receiver(player)
                                .send();
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
            guild.teleportHome(player);
        }

        this.guildManager.addGuild(guild);
        user.setGuild(guild);

        guild.getRegion().peek(region -> this.regionManager.addRegion(region));

        this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildAsyncTask(this.plugin.getDataModel(), guild));
        this.plugin.getIndividualNameTagManager()
                .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                .peek(this.plugin::scheduleFunnyTasks);

        SimpleEventHandler.handle(new GuildCreateEvent(EventCause.USER, user, guild));

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        this.messageService.getMessage(config -> config.createGuild)
                .receiver(player)
                .with(formatter)
                .send();
        this.messageService.getMessage(config -> config.broadcastCreate)
                .all()
                .with(formatter)
                .send();

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
