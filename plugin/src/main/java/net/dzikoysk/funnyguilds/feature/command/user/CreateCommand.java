package net.dzikoysk.funnyguilds.feature.command.user;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.config.sections.items.ItemsConfiguration;
import net.dzikoysk.funnyguilds.data.DataModel;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseDeleteGuildAsyncTask;
import net.dzikoysk.funnyguilds.data.tasks.DatabaseUpdateGuildAsyncTask;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.feature.hooks.vault.VaultHook;
import net.dzikoysk.funnyguilds.feature.items.GuildCreationPayment;
import net.dzikoysk.funnyguilds.feature.items.GuildCreationPayment.PaymentResult;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult;
import net.dzikoysk.funnyguilds.feature.items.ItemRequirementResult.ItemCountResult;
import net.dzikoysk.funnyguilds.feature.scoreboard.ScoreboardGlobalUpdateUserSyncTask;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildUtils;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.shared.bukkit.FunnyBox;
import net.dzikoysk.funnyguilds.shared.bukkit.InventoryUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.shared.formatter.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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

        when(this.guildManager.nameExists(name) || this.plugin.getDataModel().isGuildNameReserved(name),
                config -> config.createNameExists);
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

            int distance = this.regionManager.sizeAtEnlargementLevel(this.config.getMaxEnlargementLevel()) + this.config.createDistance;

            when(distance > LocationUtils.flatDistance(player.getWorld().getSpawnLocation(), guildLocation),
                    config -> config.createSpawn, Replacement.string("{DISTANCE}", distance));
        }

        ItemsConfiguration items = this.itemsConfiguration.snapshot();
        GuildItemSet activeSet = this.guildItemSetService.getSetForPlayer(player, items);
        if (player.hasPermission(items.adminItemsBypassPermission)) {
            this.messageService.getMessage(config -> config.itemsAdminBypass)
                    .receiver(player)
                    .send();
        }
        else {
            ItemRequirementResult result = this.guildItemRequirementChecker.check(player, user, activeSet, items);

            if (!result.meetsAll()) {
                this.messageService.getMessage(config -> config.itemsRequirementsNotMet)
                        .receiver(player)
                        .send();

                if (!result.isMeetsMoney() && activeSet.requirements.moneyEnabled) {
                    double current = VaultHook.isEconomyHooked() ? VaultHook.accountBalance(player) : 0;
                    this.messageService.getMessage(config -> config.itemsRequirementMoney)
                            .receiver(player)
                            .with("{STATUS_COLOR}", "<red>")
                            .with("{CURRENT}", String.format(Locale.ROOT, "%.0f", current))
                            .with("{REQUIRED}", String.format(Locale.ROOT, "%.0f", activeSet.requiredMoney))
                            .send();
                }
                if (!result.isMeetsLevel() && activeSet.requirements.levelEnabled) {
                    this.messageService.getMessage(config -> config.itemsRequirementLevel)
                            .receiver(player)
                            .with("{STATUS_COLOR}", "<red>")
                            .with("{CURRENT}", player.getLevel())
                            .with("{REQUIRED}", activeSet.requiredLevel)
                            .send();
                }
                if (!result.isMeetsRank() && activeSet.requirements.rankEnabled) {
                    this.messageService.getMessage(config -> config.itemsRequirementRank)
                            .receiver(player)
                            .with("{STATUS_COLOR}", "<red>")
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
                                        .with("{STATUS_COLOR}", "<red>")
                                        .with("{ITEM}", counts.getDisplayName())
                                        .with("{KEY}", entry.getKey())
                                        .with("{CURRENT}", counts.getInv())
                                        .with("{REQUIRED}", counts.getRequired())
                                        .send();
                            }
                        }
                    }
                }
                return;
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
        guild.setLives(this.config.war.lives);
        guild.setHeartLives(this.config.war.heartLives);
        guild.setValidity(Instant.now().plus(this.config.validityStart));
        guild.setProtection(Instant.now().plus(this.config.war.protection));
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

        boolean adminBypass = player.hasPermission(items.adminItemsBypassPermission);
        AtomicBoolean moneyWithdrawn = new AtomicBoolean(false);
        GuildCreationPayment payment = null;

        if (!adminBypass) {
            // Rebuild after GuildPreCreateEvent: listeners may have changed the inventory.
            payment = new GuildCreationPayment(player.getInventory(), activeSet, items);
            when(!payment.isSatisfied(), config -> config.itemsRequirementsNotMet);

            PaymentResult paymentResult = payment.pay(
                    () -> {
                        boolean withdrawn = withdrawCreationMoney(player, activeSet);
                        if (withdrawn && activeSet.requirements.moneyEnabled && activeSet.requiredMoney > 0) {
                            moneyWithdrawn.set(true);
                        }
                        return withdrawn;
                    },
                    refund -> InventoryUtils.addItemsWithOverflowDrop(player, refund)
            );
            when(paymentResult == PaymentResult.ITEMS_CHANGED, config -> config.itemsRequirementsNotMet);
            if (paymentResult == PaymentResult.WITHDRAWAL_FAILED) {
                return;
            }
        }

        // From here on the player is charged. Any failure before the commit point below must
        // compensate money, items and levels and remove everything this attempt created.
        int previousLevel = player.getLevel();
        boolean levelDeducted = false;
        boolean heartPlaced = false;
        boolean guildAdded = false;
        boolean regionAdded = false;
        List<BlockState> changedBlocks = new ArrayList<>();
        List<Runnable> schematicUndo = new ArrayList<>(1);
        Optional<BlockState> previousHeartState = Optional.empty();

        try {
            if (!adminBypass && activeSet.requirements.levelEnabled && activeSet.requiredLevel > 0) {
                int newLevel = player.getLevel() - activeSet.requiredLevel;
                player.setLevel(Math.max(0, newLevel));
                levelDeducted = true;
            }

            if (this.config.regionsEnabled) {
                if (heartConfig.pasteSchematicOnCreation) {
                    // Abort instead of continuing: a guild without its pasted heart would leave
                    // the player charged for an unusable creation. Throwing reaches the rollback
                    // below and the command handler then shows the message to the player.
                    if (HookManager.WORLD_EDIT.isEmpty()) {
                        throw new InternalValidationException(config -> config.createGuildWorldEditMissing);
                    }

                    HookManager.WORLD_EDIT.orElseThrow().pasteSchematic(heartConfig.guildSchematicFile, guildLocation,
                            heartConfig.pasteSchematicWithAir, schematicUndo::add);
                    if (schematicUndo.isEmpty()) {
                        throw new InternalValidationException(config -> config.createGuildCouldNotPasteSchematic);
                    }
                }
                else if (heartConfig.createCenterSphere) {
                    // Snapshot every block this attempt is about to modify so a rollback can
                    // restore the full terrain, not only the heart.
                    List<Location> airSphere = SpaceUtils.sphere(guildLocation, 4, 4, false, true, 0);
                    List<Location> obsidianSphere = SpaceUtils.sphere(guildLocation, 4, 4, true, true, 0);
                    Stream.concat(airSphere.stream(), obsidianSphere.stream())
                            .map(Location::getBlock)
                            .map(Block::getState)
                            .forEach(changedBlocks::add);
                    if (this.config.eventPhysics) {
                        changedBlocks.add(guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().getState());
                    }

                    for (Location locationInSphere : airSphere) {
                        if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                            locationInSphere.getBlock().setType(Material.AIR);
                        }
                    }

                    for (Location locationInSphere : obsidianSphere) {
                        if (locationInSphere.getBlock().getType() != Material.BEDROCK) {
                            locationInSphere.getBlock().setType(Material.OBSIDIAN);
                        }
                    }

                    if (this.config.eventPhysics) {
                        guildLocation.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                    }
                }

                // Remember the block the heart is about to replace so a rollback can restore it
                // instead of damaging existing terrain with the generic heart removal.
                if (heartConfig.createEntityType == null && heartConfig.createMaterial != null && heartConfig.createMaterial != Material.AIR) {
                    previousHeartState = guild.getRegion().flatMap(Region::getHeartBlock).map(Block::getState);
                }
                // Mark the spawn attempt before calling into NMS: the helper caches the entity
                // before sending spawn packets, so a packet failure must still let the rollback
                // despawn the partially spawned heart (cleanup tolerates an absent entity).
                heartPlaced = true;
                this.guildManager.spawnHeart(this.plugin.getGuildEntityHelper(), guild);
                guild.teleportHome(player);
            }

            when(this.guildManager.nameExists(guild.getName()) || this.plugin.getDataModel().isGuildNameReserved(guild.getName()),
                    config -> config.createNameExists);
            this.guildManager.addGuild(guild);
            guildAdded = true;
            user.setGuild(guild);

            if (guild.getRegion().isPresent()) {
                this.regionManager.addRegion(guild.getRegion().orElseThrow());
                regionAdded = true;
            }

            this.plugin.getIndividualNameTagManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .ifPresent(this.plugin::scheduleFunnyTasks);

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
        }
        catch (RuntimeException exception) {
            this.rollbackCreation(player, user, guild, payment, moneyWithdrawn, activeSet, previousLevel, levelDeducted,
                    heartPlaced, guildAdded, regionAdded, changedBlocks, schematicUndo, previousHeartState);
            throw exception;
        }

        // Commit point: the guild is fully registered and announced, so no rollback can happen
        // anymore. Persist and hand out rewards only now, so an async save can never resurrect
        // a rolled-back guild and a reward failure can no longer undo a completed creation.
        this.plugin.scheduleFunnyTasks(new DatabaseUpdateGuildAsyncTask(this.plugin.getDataModel(), guild));

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

    /**
     * Compensates a failure after payment and before the commit point: refunds money, items and levels,
     * then undoes the terrain, heart, region, membership and manager registrations this attempt made.
     * Every step is guarded independently so one failure cannot skip the remaining cleanup.
     */
    private void rollbackCreation(Player player, User user, Guild guild, GuildCreationPayment payment, AtomicBoolean moneyWithdrawn,
                                  GuildItemSet set, int previousLevel, boolean levelDeducted, boolean heartPlaced,
                                  boolean guildAdded, boolean regionAdded, List<BlockState> changedBlocks, List<Runnable> schematicUndo,
                                  Optional<BlockState> previousHeartState) {
        FunnyGuilds.getPluginLogger().warning("Guild creation failed after payment, rolling back the transaction");

        if (moneyWithdrawn.get()) {
            try {
                EconomyResponse response = VaultHook.depositToPlayerBank(player, set.requiredMoney);
                if (!response.transactionSuccess()) {
                    FunnyGuilds.getPluginLogger().error("Could not refund guild creation money: " + response.errorMessage);
                }
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not refund guild creation money", compensationFailure);
            }
        }

        if (payment != null) {
            try {
                List<ItemStack> reserved = payment.takeReservedItems();
                if (!reserved.isEmpty()) {
                    InventoryUtils.addItemsWithOverflowDrop(player, reserved);
                }
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not return reserved guild creation items", compensationFailure);
            }
        }

        if (levelDeducted) {
            try {
                player.setLevel(previousLevel);
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not restore the player's level", compensationFailure);
            }
        }

        if (heartPlaced) {
            try {
                if (this.config.heart.createEntityType != null) {
                    this.plugin.getGuildEntityHelper().despawnGuildEntity(guild);
                }
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not remove the spawned guild heart", compensationFailure);
            }

            try {
                // The guild was never announced as live, so restore the terrain this attempt
                // modified: the schematic (via the WorldEdit undo) or the sphere blocks, plus
                // the exact block state the heart replaced.
                schematicUndo.forEach(Runnable::run);
                changedBlocks.forEach(state -> state.update(true, false));
                previousHeartState.ifPresent(state -> state.update(true, false));
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not restore the terrain modified by guild creation", compensationFailure);
            }
        }

        if (regionAdded) {
            try {
                guild.getRegion().ifPresent(region -> this.regionManager.deleteRegion(this.plugin.getDataModel(), region));
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not remove the partially created region", compensationFailure);
            }
        }

        try {
            user.removeGuild();
        }
        catch (RuntimeException compensationFailure) {
            FunnyGuilds.getPluginLogger().error("Could not clear the player's guild membership", compensationFailure);
        }

        if (guildAdded) {
            try {
                DataModel dataModel = this.plugin.getDataModel();
                dataModel.queueGuildDeletion(guild);
                this.guildManager.removeGuild(guild);
                this.plugin.scheduleFunnyTasks(new DatabaseDeleteGuildAsyncTask(dataModel));
            }
            catch (RuntimeException compensationFailure) {
                FunnyGuilds.getPluginLogger().error("Could not remove the rolled-back guild", compensationFailure);
            }
        }

        try {
            this.plugin.getIndividualNameTagManager()
                    .map(manager -> new ScoreboardGlobalUpdateUserSyncTask(manager, user))
                    .ifPresent(this.plugin::scheduleFunnyTasks);
        }
        catch (RuntimeException compensationFailure) {
            FunnyGuilds.getPluginLogger().error("Could not refresh the player's name tag", compensationFailure);
        }
    }

    private boolean withdrawCreationMoney(Player player, GuildItemSet set) {
        if (!set.requirements.moneyEnabled || set.requiredMoney <= 0 || !VaultHook.isEconomyHooked()) {
            return true;
        }
        EconomyResponse response = VaultHook.withdrawFromPlayerBank(player, set.requiredMoney);
        if (response.transactionSuccess()) {
            return true;
        }
        this.messageService.getMessage(config -> config.withdrawError)
                .receiver(player)
                .with("{ERROR}", response.errorMessage)
                .send();
        return false;
    }

}
