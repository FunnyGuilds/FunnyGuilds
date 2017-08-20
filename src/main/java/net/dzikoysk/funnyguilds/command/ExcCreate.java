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
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User u = User.get(p);

        boolean bool = this.checkWorld(p);
        if (bool) {
            p.sendMessage(m.blockedWorld);
            return;
        }

        if (u.hasGuild()) {
            p.sendMessage(m.createHasGuild);
            return;
        }

        if (!(args.length == 2)) {
            if (args.length == 0) {
                p.sendMessage(m.createTag);
                return;
            }
            else if (args.length == 1) {
                p.sendMessage(m.createName);
                return;
            }
            else if (args.length > 2) {
                p.sendMessage(m.createMore);
                return;
            }
        }

        PluginConfig c = Settings.getConfig();
        String tag = args[0];
        String name = args[1];

        if (tag.length() > c.createTagLength) {
            p.sendMessage(m.createTagLength.replace("{LENGTH}", Integer.toString(c.createTagLength)));
            return;
        }

        if (tag.length() < c.createTagMinLength) {
            p.sendMessage(m.createTagMinLength.replace("{LENGTH}", Integer.toString(c.createTagMinLength)));
            return;
        }

        if (name.length() > c.createNameLength) {
            p.sendMessage(m.createNameLength.replace("{LENGTH}", Integer.toString(c.createNameLength)));
            return;
        }

        if (name.length() < c.createNameMinLength) {
            p.sendMessage(m.createNameMinLength.replace("{LENGTH}", Integer.toString(c.createNameMinLength)));
            return;
        }

        if (!tag.matches("[a-zA-Z]+")) {
            p.sendMessage(m.createOLTag);
            return;
        }

        if (!name.matches("[a-zA-Z]+")) {
            p.sendMessage(m.createOLName);
            return;
        }

        if (GuildUtils.isExists(name)) {
            p.sendMessage(m.createNameExists);
            return;
        }

        if (GuildUtils.tagExists(tag)) {
            p.sendMessage(m.createTagExists);
            return;
        }

        Location loc = p.getLocation();
        loc = loc.getBlock().getLocation();

        if (c.createCenterY != 0) {
            loc.setY(c.createCenterY);
        }

        int d = c.regionSize + c.createDistance;

        if (c.enlargeItems != null) {
            d += c.enlargeItems.size() * c.enlargeSize;
        }

        if (d > p.getWorld().getSpawnLocation().distance(loc)) {
            p.sendMessage(m.createSpawn.replace("{DISTANCE}", Integer.toString(d)));
            return;
        }

        List<ItemStack> itemsList;
        if (p.hasPermission("funnyguilds.vip")) {
            itemsList = c.createItemsVip;
        }
        else {
            itemsList = c.createItems;
        }

        ItemStack[] items = itemsList.toArray(new ItemStack[0]);

        if (!u.getBypass()) {
            for (int i = 0; i < items.length; i++) {
                if (p.getInventory().containsAtLeast(items[i], items[i].getAmount())) {
                    continue;
                }
                String msg = m.createItems;
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
                p.sendMessage(msg);
                return;
            }
        }

        if (RegionUtils.isIn(loc)) {
            p.sendMessage(m.createIsNear);
            return;
        }

        if (RegionUtils.isNear(loc)) {
            p.sendMessage(m.createIsNear);
            return;
        }

        if (u.getBypass()) {
            u.setBypass(false);
        }
        else {
            p.getInventory().removeItem(items);
        }

        Manager.getInstance().stop();

        Guild guild = new Guild(name);
        guild.setTag(tag);
        guild.setOwner(u);
        guild.setLives(c.warLives);
        guild.setBorn(System.currentTimeMillis());
        guild.setValidity(System.currentTimeMillis() + c.validityStart);
        guild.setAttacked(System.currentTimeMillis() - c.warWait + c.warProtection);
        guild.setPvP(c.damageGuild);

        Region region = new Region(guild, loc, c.regionSize);
        guild.setRegion(region.getName());
        guild.addRegion(region.getName());

        u.setGuild(guild);

        if (c.createCenterSphere) {
            for (Location l : SpaceUtils.sphere(loc, 4, 4, false, true, 0)) {
                if (l.getBlock().getType() != Material.BEDROCK) {
                    l.getBlock().setType(Material.AIR);
                }
            }

            for (Location l : SpaceUtils.sphere(loc, 4, 4, true, true, 0)) {
                if (l.getBlock().getType() != Material.BEDROCK) {
                    l.getBlock().setType(Material.OBSIDIAN);
                }
            }
        }

        if (c.createMaterial != null && c.createMaterial != Material.AIR) {
            loc.getBlock().getRelative(BlockFace.DOWN).setType(c.createMaterial);
        }
        else if (c.createStringMaterial.equalsIgnoreCase("ender crystal")) {
            EntityUtil.spawn(guild);
        }

        p.teleport(loc);
        Manager.getInstance().start();

        IndependentThread.actions(ActionType.RANK_UPDATE_GUILD, guild);
        IndependentThread.actions(ActionType.PREFIX_GLOBAL_ADD_GUILD, guild);
        IndependentThread.action(ActionType.PREFIX_GLOBAL_ADD_PLAYER, u.getOfflineUser());

        p.sendMessage(m.createGuild.replace("{GUILD}", name).replace("{PLAYER}", p.getName()).replace("{TAG}", tag));

        Bukkit.getServer().broadcastMessage(m.broadcastCreate.replace("{GUILD}", name).replace("{PLAYER}", p.getName()).replace("{TAG}", tag));
    }

}
