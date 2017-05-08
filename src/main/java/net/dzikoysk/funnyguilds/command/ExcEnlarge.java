package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcEnlarge implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        Player p = (Player) s;
        User lp = User.get(p);

        if (!lp.hasGuild()) {
            p.sendMessage(m.enlargeHasNotGuild);
            return;
        }

        if (!lp.isOwner() && !lp.isDeputy()) {
            p.sendMessage(m.enlargeIsNotOwner);
            return;
        }

        Region region = Region.get(lp.getGuild().getRegion());
        int enlarge = region.getEnlarge();

        PluginConfig c = Settings.getConfig();

        if (enlarge > c.enlargeItems.size() - 1) {
            p.sendMessage(m.enlargeMaxSize);
            return;
        }

        ItemStack need = c.enlargeItems.get(enlarge);
        if (!p.getInventory().containsAtLeast(need, need.getAmount())) {
            p.sendMessage(
                    m.enlargeItem
                            .replace("{ITEM}", need.getAmount() + " " + need.getType().name().toLowerCase())
            );
            return;
        }

        if (RegionUtils.isNear(region.getCenter())) {
            p.sendMessage(m.enlargeIsNear);
            return;
        }

        p.getInventory().removeItem(need);
        region.setEnlarge(enlarge + 1);
        region.setSize(region.getSize() + c.enlargeSize);

        String tm = m.enlargeDone
                .replace("{SIZE}", region.getSize() + "")
                .replace("{LEVEL}", region.getEnlarge() + "");

        for (User user : lp.getGuild().getMembers()) {
            OfflinePlayer of = Bukkit.getOfflinePlayer(user.getName());
            if (of.isOnline()) {
                of.getPlayer().sendMessage(tm);
            }
        }
        return;
    }

}
