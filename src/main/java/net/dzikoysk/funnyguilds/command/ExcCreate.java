package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Manager;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import net.dzikoysk.funnyguilds.data.util.MessageTranslator;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.SpaceUtils;
import net.dzikoysk.funnyguilds.util.commons.StringUtils;
import net.dzikoysk.funnyguilds.element.schematic.SchematicHelper;
import net.dzikoysk.funnyguilds.util.hook.VaultHook;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.concurrency.independent.ActionType;
import net.dzikoysk.funnyguilds.concurrency.independent.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExcCreate implements Executor {

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();

        Player player = (Player) sender;
        User user = User.get(player);

        if (!config.guildsEnabled) {
            player.sendMessage(messages.adminGuildsDisabled);
            return;
        }

        boolean isBlockedWorld = this.checkWorld(player);
        if (isBlockedWorld) {
            player.sendMessage(messages.blockedWorld);
            return;
        }

        if (user.hasGuild()) {
            player.sendMessage(messages.generalHasGuild);
            return;
        }

        if (args.length != 2) {
            switch (args.length) {
                case 0:
                    player.sendMessage(messages.generalNoTagGiven);
                    return;
                case 1:
                    player.sendMessage(messages.generalNoNameGiven);
                    return;
                default:
                    player.sendMessage(messages.createMore);
                    return;
            }
        }

        String tag = args[0];

        if (!config.guildTagKeepCase) {
            tag = config.guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
        }
        
        String name = args[1];

        if (tag.length() > config.createTagLength) {
            player.sendMessage(messages.createTagLength.replace("{LENGTH}", Integer.toString(config.createTagLength)));
            return;
        }

        if (tag.length() < config.createTagMinLength) {
            player.sendMessage(messages.createTagMinLength.replace("{LENGTH}", Integer.toString(config.createTagMinLength)));
            return;
        }

        if (name.length() > config.createNameLength) {
            player.sendMessage(messages.createNameLength.replace("{LENGTH}", Integer.toString(config.createNameLength)));
            return;
        }

        if (name.length() < config.createNameMinLength) {
            player.sendMessage(messages.createNameMinLength.replace("{LENGTH}", Integer.toString(config.createNameMinLength)));
            return;
        }

        if (!tag.matches(config.tagRegex.getPattern())) {
            player.sendMessage(messages.createOLTag);
            return;
        }

        if (!name.matches(config.nameRegex.getPattern())) {
            player.sendMessage(messages.createOLName);
            return;
        }

        if (GuildUtils.nameExists(name)) {
            player.sendMessage(messages.createNameExists);
            return;
        }

        if (GuildUtils.tagExists(tag)) {
            player.sendMessage(messages.createTagExists);
            return;
        }

        if (config.checkForRestrictedGuildNames) {
            if (!GuildUtils.isNameValid(name)) {
                player.sendMessage(messages.restrictedGuildName);
                return;
            }
            else if (!GuildUtils.isTagValid(tag)) {
                player.sendMessage(messages.restrictedGuildTag);
                return;
            }
        }

        Location guildLocation = player.getLocation().getBlock().getLocation();
        if (config.regionsEnabled) {
            if (config.createCenterY != 0) {
                guildLocation.setY(config.createCenterY);
            }
    
            int d = config.regionSize + config.createDistance;
    
            if (config.enlargeItems != null) {
                d += config.enlargeItems.size() * config.enlargeSize;
            }
    
            if (d > player.getWorld().getSpawnLocation().distance(guildLocation)) {
                player.sendMessage(messages.createSpawn.replace("{DISTANCE}", Integer.toString(d)));
                return;
            }
        }

        if (config.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? config.rankCreateVip : config.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = messages.createRank;
                
                msg = StringUtils.replace(msg, "{REQUIRED-FORMAT}", IntegerRange.inRange(requiredRank, config.pointsFormat).replace("{POINTS}", "{REQUIRED}"));
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS-FORMAT}", IntegerRange.inRange(points, config.pointsFormat));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> requiredItems = player.hasPermission("funnyguilds.vip.items") ? config.createItemsVip : config.createItems;
        int requiredExperience = player.hasPermission("funnyguilds.vip.items") ? config.requiredExperienceVip : config.requiredExperience;
        double requiredMoney = player.hasPermission("funnyguilds.vip.items") ? config.requiredMoneyVip : config.requiredMoney;

        if (!user.getBypass()) {
            if (player.getTotalExperience() < requiredExperience) {
                String msg = messages.createExperience;
                msg = StringUtils.replace(msg, "{EXP}", String.valueOf(requiredExperience));
                player.sendMessage(msg);
                return;
            }

            if (VaultHook.isHooked() && !VaultHook.canAfford(player, requiredMoney)) {
                String notEnoughMoneyMessage = messages.createMoney;
                notEnoughMoneyMessage = StringUtils.replace(notEnoughMoneyMessage, "{MONEY}", Double.toString(requiredMoney));
                player.sendMessage(notEnoughMoneyMessage);
                return;
            }

            for (ItemStack requiredItem : requiredItems) {
                if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                    continue;
                }
                
                String msg = ItemUtils.translatePlaceholder(messages.createItems, requiredItems, requiredItem);
                player.sendMessage(msg);
                return;
            }
        }

        if (config.regionsEnabled) {
            if (RegionUtils.isIn(guildLocation)) {
                player.sendMessage(messages.createIsNear);
                return;
            }
            
            if (RegionUtils.isNear(guildLocation)) {
                player.sendMessage(messages.createIsNear);
                return;
            }

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
        
        if (!SimpleEventHandler.handle(new GuildCreateEvent(EventCause.USER, user, name, tag, guildLocation))) {
            return;
        }
        
        if (user.getBypass()) {
            user.setBypass(false);
        } else {
            player.getInventory().removeItem(ItemUtils.toArray(requiredItems));
            player.setTotalExperience(player.getTotalExperience() - requiredExperience);
            if (VaultHook.isHooked()) {
                VaultHook.withdrawFromPlayerBank(player, requiredMoney);
            }
        }

        Manager.getInstance().stop();

        Guild guild = new Guild(name);
        guild.setTag(tag);
        guild.setOwner(user);
        guild.setLives(config.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(System.currentTimeMillis() + config.validityStart);
        guild.setAttacked(System.currentTimeMillis() - config.warWait + config.warProtection);
        guild.setPvP(config.damageGuild);
        user.setGuild(guild);
        
        if (config.regionsEnabled) {
            Region region = new Region(guild, guildLocation, config.regionSize);
            guild.setRegion(region.getName());
            guild.addRegion(region.getName());

            if (config.pasteSchematicOnCreation) {
                if (!SchematicHelper.pasteSchematic(config.guildSchematicFile, guildLocation, config.pasteSchematicWithAir)) {
                    player.sendMessage(messages.createGuildCouldNotPasteSchematic);
                }
            } else if (config.createCenterSphere) {
                for (Location l : SpaceUtils.sphere(guildLocation, 4, 4, false, true, 0)) {
                    if (l.getBlock().getType() != Material.BEDROCK) {
                        l.getBlock().setType(Material.AIR);
                    }
                }

                for (Location l : SpaceUtils.sphere(guildLocation, 4, 4, true, true, 0)) {
                    if (l.getBlock().getType() != Material.BEDROCK) {
                        l.getBlock().setType(Material.OBSIDIAN);
                    }
                }
            }
            
            if (config.createMaterialData != null && config.createMaterialData.getItemType() != Material.AIR) {
                Block heart = guildLocation.getBlock().getRelative(BlockFace.DOWN);
                
                heart.setType(config.createMaterialData.getItemType());
                heart.setData(config.createMaterialData.getData());
            } else if (config.createEntityType != null) {
                EntityUtil.spawn(guild);
            }

            player.teleport(guildLocation);
        }
        
        Manager.getInstance().start();

        IndependentThread.actions(ActionType.RANK_UPDATE_GUILD, guild);
        IndependentThread.actions(ActionType.PREFIX_GLOBAL_ADD_GUILD, guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getName());

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        player.sendMessage(translator.translate(messages.createGuild));
        Bukkit.broadcastMessage(translator.translate(messages.broadcastCreate));
    }
}
