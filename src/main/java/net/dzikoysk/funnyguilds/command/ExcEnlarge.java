package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnyguilds.basic.Region;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.command.util.Executor;
import net.dzikoysk.funnyguilds.data.Messages;
import net.dzikoysk.funnyguilds.data.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExcEnlarge implements Executor {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Settings settings = Settings.getInstance();
        Messages messages = Messages.getInstance();
        Player player = (Player) sender;
        User user = User.get(player);

        if (!user.hasGuild()) {
            player.sendMessage(messages.getMessage("enlargeHasNotGuild"));
            return;
        }

        if (!user.isOwner() && !user.isDeputy()) {
            player.sendMessage(messages.getMessage("enlargeIsNotOwner"));
            return;
        }

        Region region = user.getGuild().getRegion();
        int enlarge = region.getEnlarge();

        if (enlarge > settings.enlargeItems.size() - 1) {
            player.sendMessage(messages.getMessage("enlargeMaxSize"));
            return;
        }

        ItemStack need = settings.enlargeItems.get(enlarge);
        if (!player.getInventory().containsAtLeast(need, need.getAmount())) {
            player.sendMessage(messages.getMessage("enlargeItem")
                    .replace("{ITEM}", need.getAmount() + " " + need.getType().name().toLowerCase()));
            return;
        }

        player.getInventory().removeItem(need);
        region.setEnlarge(enlarge + 1);
        region.setSize(region.getSize() + settings.enlargeSize);

        String tm = messages.getMessage("enlargeDone")
                .replace("{SIZE}", Integer.toString(region.getSize()))
                .replace("{LEVEL}", Integer.toString(region.getEnlarge()));

        for (User member : user.getGuild().getMembers())
            if (member.isOnline())
                member.getPlayer().sendMessage(tm);
        return;
    }

}
