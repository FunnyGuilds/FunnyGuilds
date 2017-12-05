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
import net.dzikoysk.funnyguilds.util.SpaceUtils;
import net.dzikoysk.funnyguilds.util.StringUtils;
import net.dzikoysk.funnyguilds.util.reflect.EntityUtil;
import net.dzikoysk.funnyguilds.util.thread.ActionType;
import net.dzikoysk.funnyguilds.util.thread.IndependentThread;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExcCreate implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesConfig messages = Messages.getInstance();
        PluginConfig config = Settings.getConfig();

        Player player = (Player) sender;
        User user = User.get(player);

        if (!config.guildsEnabled) {
            player.sendMessage(messages.guildsDisabled);
            return;
        }

        boolean bool = this.checkWorld(player);
        if (bool) {
            player.sendMessage(messages.blockedWorld);
            return;
        }

        if (user.hasGuild()) {
            player.sendMessage(messages.createHasGuild);
            return;
        }

        if (args.length != 2) {
            if (args.length == 0) {
                player.sendMessage(messages.createTag);
                return;
            } else if (args.length == 1) {
                player.sendMessage(messages.createName);
                return;
            } else if (args.length > 2) {
                player.sendMessage(messages.createMore);
                return;
            }
        }

        String tag = args[0];
        tag = Settings.getConfig().guildTagUppercase ? tag.toUpperCase() : tag.toLowerCase();
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

        if (!tag.matches("[a-zA-Z]+")) {
            player.sendMessage(messages.createOLTag);
            return;
        }

        if (!name.matches("[a-zA-Z]+")) {
            player.sendMessage(messages.createOLName);
            return;
        }

        if (GuildUtils.isExists(name)) {
            player.sendMessage(messages.createNameExists);
            return;
        }

        if (GuildUtils.tagExists(tag)) {
            player.sendMessage(messages.createTagExists);
            return;
        }

        Location guildLocation = player.getLocation().getBlock().getLocation();

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

        if (config.rankCreateEnable) {
            int requiredRank = player.hasPermission("funnyguilds.vip.rank") ? config.rankCreateVip : config.rankCreate;
            int points = user.getRank().getPoints();

            if (points < requiredRank) {
                String msg = messages.createRank;
                msg = StringUtils.replace(msg, "{REQUIRED}", String.valueOf(requiredRank));
                msg = StringUtils.replace(msg, "{POINTS}", String.valueOf(points));
                player.sendMessage(msg);
                return;
            }
        }

        List<ItemStack> itemsList = player.hasPermission("funnyguilds.vip.items") ? config.createItemsVip : config.createItems;

        if (!user.getBypass()) {
            for (ItemStack is : itemsList) {
                if (player.getInventory().containsAtLeast(is, is.getAmount())) {
                    continue;
                }
                
                String msg = messages.createItems;
                if (msg.contains("{ITEM}")) {
                    StringBuilder messageBuilder = new StringBuilder();
                    messageBuilder.append(is.getAmount());
                    messageBuilder.append(" ");
                    messageBuilder.append(is.getType().toString().toLowerCase());
                    msg = msg.replace("{ITEM}", messageBuilder.toString());
                }
                
                if (msg.contains("{ITEMS}")) {
                    ArrayList<String> list = new ArrayList<>();
                    for (ItemStack it : itemsList) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(it.getAmount());
                        sb.append(" ");
                        sb.append(it.getType().toString().toLowerCase());
                        list.add(sb.toString());
                    }
                    
                    msg = msg.replace("{ITEMS}", StringUtils.toString(list, true));
                }
                
                player.sendMessage(msg);
                return;
            }
        }

        if (RegionUtils.isIn(guildLocation)) {
            player.sendMessage(messages.createIsNear);
            return;
        }

        if (RegionUtils.isNear(guildLocation)) {
            player.sendMessage(messages.createIsNear);
            return;
        }

        if (user.getBypass()) {
            user.setBypass(false);
        } else {
            player.getInventory().removeItem(itemsList.toArray(new ItemStack[0]));
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

        Region region = new Region(guild, guildLocation, config.regionSize);
        guild.setRegion(region.getName());
        guild.addRegion(region.getName());

        user.setGuild(guild);

        if (config.createCenterSphere) {
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

        if (config.createMaterial != null && config.createMaterial != Material.AIR) {
            guildLocation.getBlock().getRelative(BlockFace.DOWN).setType(config.createMaterial);
        } else if (config.createStringMaterial.equalsIgnoreCase("ender crystal")) {
            EntityUtil.spawn(guild);
        }

        player.teleport(guildLocation);
        Manager.getInstance().start();

        IndependentThread.actions(ActionType.RANK_UPDATE_GUILD, guild);
        IndependentThread.actions(ActionType.PREFIX_GLOBAL_ADD_GUILD, guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());

        MessageTranslator translator = new MessageTranslator()
                .register("{GUILD}", name)
                .register("{TAG}", tag)
                .register("{PLAYER}", player.getName());

        player.sendMessage(translator.translate(messages.createGuild));
        Bukkit.getServer().broadcastMessage(translator.translate(messages.broadcastCreate));
    }

}
