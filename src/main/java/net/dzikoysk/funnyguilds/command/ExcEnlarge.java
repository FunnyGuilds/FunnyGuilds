package net.dzikoysk.funnyguilds.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.RegionUtils;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import net.dzikoysk.funnyguilds.data.configs.MessagesConfig;
import net.dzikoysk.funnyguilds.data.configs.PluginConfig;

public class ExcEnlarge implements Executor {

    @Override
    public void execute(CommandSender s, String[] args) {
        MessagesConfig m = Messages.getInstance();
        PluginConfig c = Settings.getConfig();
        Player p = (Player) s;
        User lp = User.get(p);

        if (!c.enlargeEnable) {
            return;
        }

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

        if (enlarge > c.enlargeItems.size() - 1) {
            p.sendMessage(m.enlargeMaxSize);
            return;
        }

        ItemStack need = c.enlargeItems.get(enlarge);
        if (!p.getInventory().containsAtLeast(need, need.getAmount())) {
            StringBuilder sb = new StringBuilder();
            sb.append(need.getAmount());
            sb.append(" ");
            sb.append(need.getType().toString().toLowerCase());
            
            p.sendMessage(m.enlargeItem.replace("{ITEM}", sb.toString()));
            return;
        }

        if (RegionUtils.isNear(region.getCenter())) {
            p.sendMessage(m.enlargeIsNear);
            return;
        }

        p.getInventory().removeItem(need);
        region.setEnlarge(++enlarge);
        region.setSize(region.getSize() + c.enlargeSize);

        String tm = m.enlargeDone.replace("{SIZE}", Integer.toString(region.getSize())).replace("{LEVEL}", Integer.toString(region.getEnlarge()));
        for (User user : lp.getGuild().getOnlineMembers()) {
            user.getPlayer().sendMessage(tm);
        }
    }
}
