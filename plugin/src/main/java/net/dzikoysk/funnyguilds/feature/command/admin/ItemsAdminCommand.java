package net.dzikoysk.funnyguilds.feature.command.admin;

import java.util.List;
import java.util.Map;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class ItemsAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${admin.items.name}",
            permission = "funnyguilds.admin",
            acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 1, config -> config.generalNoNickGiven);

        User target = UserValidation.requireUserByName(args[0]);
        Player targetPlayer = Bukkit.getPlayer(target.getUUID());

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            this.messageService.getMessage(config -> config.itemsPlayerOffline)
                    .receiver(sender)
                    .with("{PLAYER}", args[0])
                    .send();
            return;
        }

        GuildItemSet set;
        String setName;
        if (args.length >= 2) {
            setName = args[1];
            set = itemsConfiguration.getGuildItemSets().get(setName);
            if (set == null) {
                this.messageService.getMessage(config -> config.itemsSetNotFound)
                        .receiver(sender)
                        .with("{SET}", setName)
                        .with("{SETS}", String.join(", ", itemsConfiguration.getGuildItemSets().keySet()))
                        .send();
                return;
            }
        } else {
            set = guildItemSetService.getSetForPlayer(targetPlayer);
            setName = itemsConfiguration.getGuildItemSets().entrySet().stream()
                    .filter(e -> e.getValue() == set)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("default");
        }

        // Budujemy przedmioty z setu i dajemy graczowi
        List<ItemStack> items = ItemUtils.buildRequiredItems(set, itemsConfiguration);

        if (items.isEmpty()) {
            this.messageService.getMessage(config -> config.itemsAdminNoItems)
                    .receiver(sender)
                    .with("{SET}", setName)
                    .send();
            return;
        }

        int givenCount = 0;
        for (ItemStack item : items) {
            Map<Integer, ItemStack> overflow = targetPlayer.getInventory().addItem(item);
            for (ItemStack leftover : overflow.values()) {
                targetPlayer.getWorld().dropItemNaturally(targetPlayer.getLocation(), leftover);
            }
            givenCount += item.getAmount();
        }

        final String finalSetName = setName;
        final int finalGivenCount = givenCount;
        this.messageService.getMessage(config -> config.itemsAdminGiven)
                .receiver(sender)
                .with("{PLAYER}", target.getName())
                .with("{SET}", finalSetName)
                .with("{COUNT}", finalGivenCount)
                .send();

        this.messageService.getMessage(config -> config.itemsAdminReceived)
                .receiver(targetPlayer)
                .with("{SET}", finalSetName)
                .with("{COUNT}", finalGivenCount)
                .send();
    }

}
