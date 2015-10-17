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
import net.dzikoysk.funnyguilds.event.EventCaller;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildPreCreateEvent;
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
    public void execute(final CommandSender s, String[] args) {
        Messages messages = Messages.getInstance();
        Settings settings = Settings.getInstance();
        Player player = (Player) s;
        User user = User.get(player);

        if (user.hasGuild()) {
            player.sendMessage(messages.getMessage("createHasGuild"));
            return;
        }

        if (!(args.length == 2)) {
            if (args.length == 0) {
                player.sendMessage(messages.getMessage("createTag"));
                return;
            } else if (args.length == 1) {
                player.sendMessage(messages.getMessage("createName"));
                return;
            } else if (args.length > 2) {
                player.sendMessage(messages.getMessage("createMore"));
                return;
            }
        }

        String tag = args[0];
        String name = args[1];

        if (tag.length() > settings.createTagLength) {
            player.sendMessage(messages.getMessage("createTagLength")
                    .replace("{LENGTH}", Integer.toString(settings.createTagLength)));
            return;
        }

        if (tag.length() < settings.createTagMinLength) {
            player.sendMessage(messages.getMessage("createTagMinLength")
                    .replace("{LENGTH}", Integer.toString(settings.createTagMinLength)));
            return;
        }

        if (name.length() > settings.createNameLength) {
            player.sendMessage(messages.getMessage("createNameLength")
                    .replace("{LENGTH}", Integer.toString(settings.createNameLength)));
            return;
        }

        if (name.length() < settings.createNameMinLength) {
            player.sendMessage(messages.getMessage("createNameMinLength")
                    .replace("{LENGTH}", Integer.toString(settings.createNameMinLength)));
            return;
        }

        if (!tag.matches("[a-zA-Z]+")) {
            player.sendMessage(messages.getMessage("createOLTag"));
            return;
        }

        if (!name.matches("[a-zA-Z]+")) {
            player.sendMessage(messages.getMessage("createOLName"));
            return;
        }

        if (GuildUtils.isExists(name)) {
            player.sendMessage(messages.getMessage("createNameExists"));
            return;
        }

        if (GuildUtils.tagExists(tag)) {
            player.sendMessage(messages.getMessage("createTagExists"));
            return;
        }

        Location loc = player.getLocation();
        loc = loc.getBlock().getLocation();

        if (settings.createCenterY != 0)
            loc.setY(settings.createCenterY);
        int d = settings.regionSize + settings.createDistance;
        if (settings.enlargeItems != null)
            d += settings.enlargeItems.size() * settings.enlargeSize;
        if (d > player.getWorld().getSpawnLocation().distance(loc)) {
            player.sendMessage(messages.getMessage("createSpawn")
                    .replace("{DISTANCE}", Integer.toString(d)));
            return;
        }

        List<ItemStack> itemsList = null;
        if (player.hasPermission("funnyguilds.vip"))
            itemsList = settings.createItemsVip;
        else
            itemsList = settings.createItems;
        ItemStack[] items = itemsList.toArray(new ItemStack[0]);
        for (int i = 0; i < items.length; i++) {
            if (player.getInventory().containsAtLeast(items[i], items[i].getAmount()))
                continue;
            String msg = messages.getMessage("createItems");
            if (msg.contains("{ITEM}")) {
                StringBuilder sb = new StringBuilder();
                sb.append(items[i].getAmount());
                sb.append(" ");
                sb.append(items[i].getType().toString().toLowerCase());
                msg = msg.replace("{ITEM}", sb.toString());
            }
            if (msg.contains("{ITEMS}")) {
                ArrayList<String> list = new ArrayList<String>();
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

        if (RegionUtils.isNear(loc)) {
            player.sendMessage(messages.getMessage("createIsNear"));
            return;
        }

        GuildPreCreateEvent event = EventCaller.callEvent(new GuildPreCreateEvent(player, name, tag));
        if (event.isCancelled())
            return;

        player.getInventory().removeItem(items);
        Manager.getInstance().stop();

        Guild guild = Guild.get(name);
        guild.setTag(tag);
        guild.setOwner(user);
        guild.setLives(settings.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(System.currentTimeMillis() + settings.validityStart);
        guild.setAttacked(System.currentTimeMillis() - settings.warWait + settings.warProtection);
        guild.setPvP(settings.damageGuild);

        Region region = new Region(guild, loc, settings.regionSize);
        guild.setRegion(region);
        user.setGuild(guild);

        if (settings.createCenterSphere) {
            for (Location l : SpaceUtils.sphere(loc, 4, 4, false, true, 0))
                if (l.getBlock().getType() != Material.BEDROCK)
                    l.getBlock().setType(Material.AIR);
            for (Location l : SpaceUtils.sphere(loc, 4, 4, true, true, 0))
                if (l.getBlock().getType() != Material.BEDROCK)
                    l.getBlock().setType(Material.OBSIDIAN);
        }

        if (settings.createMaterial != null && settings.createMaterial != Material.AIR)
            loc.getBlock().getRelative(BlockFace.DOWN).setType(settings.createMaterial);
        else if (settings.createStringMaterial.equalsIgnoreCase("ender crystal"))
            EntityUtil.spawn(guild);
        player.teleport(loc);

        Manager.getInstance().start();
        IndependentThread.actions(ActionType.RANK_UPDATE_GUILD, guild);
        IndependentThread.actions(ActionType.PREFIX_GLOBAL_ADD_GUILD, guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, user.getOfflineUser());

        player.sendMessage(messages.getMessage("createGuild")
                .replace("{GUILD}", name)
                .replace("{PLAYER}", player.getName())
                .replace("{TAG}", tag));

        Bukkit.getServer().broadcastMessage(messages.getMessage("broadcastCreate")
                .replace("{GUILD}", name)
                .replace("{PLAYER}", player.getName())
                .replace("{TAG}", tag));

        EventCaller.callEvent(new GuildCreateEvent(guild));
        return;
    }

}
