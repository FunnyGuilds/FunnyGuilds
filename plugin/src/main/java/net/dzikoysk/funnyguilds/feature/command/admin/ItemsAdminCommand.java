package net.dzikoysk.funnyguilds.feature.command.admin;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.config.sections.items.GuildItemSet;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.InternalValidationException;
import net.dzikoysk.funnyguilds.feature.command.UserValidation;
import net.dzikoysk.funnyguilds.shared.bukkit.InventoryUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Pair;
import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class ItemsAdminCommand extends AbstractFunnyCommand {

    @FunnyCommand(
        name = "${admin.items.name}",
        permission = "funnyguilds.admin",
        completer = "online-players:3 items-give-types:3",
        acceptsExceeded = true
    )
    public void execute(CommandSender sender, String[] args) {
        when(args.length < 2, config -> config.itemsAdminUsage);

        String type = args[1];
        Player targetPlayer = Bukkit.getPlayer(UserValidation.requireUserByName(args[0]).getUUID());
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            throw new InternalValidationException(
                config -> config.itemsPlayerOffline,
                Replacement.string("{PLAYER}", args[0])
            );
        }

        List<ItemStack> items = switch (type.toLowerCase(Locale.ROOT)) {
            case "guild" -> findGuildSetItems(targetPlayer, args);
            case "base" -> config.baseItems;
            case "join" -> config.joinItems;
            case "enlarge" -> config.enlargeItems;
            case "validity" -> config.validityItems;
            case "rankreset" -> config.rankResetItems;
            case "statsreset" -> config.statsResetItems;
            case "firstguildreward" -> config.firstGuildRewards;
            default -> throw new InternalValidationException(
                config -> config.itemsAdminUnknownType,
                Replacement.string("{TYPE}", type)
            );
        };

        if (items.isEmpty()) {
            throw new InternalValidationException(
                config -> config.itemsAdminNoItems,
                Replacement.string("{TYPE}", type)
            );
        }

        Pair<Integer, Integer> givenCounts = InventoryUtils.addItemsWithOverflowDrop(targetPlayer, items);

        this.messageService.getMessage(config -> config.itemsAdminGiven)
            .receiver(sender)
            .with("{PLAYER}", targetPlayer.getName())
            .with("{TYPE}", type)
            .with("{COUNT}", givenCounts.getFirst() + givenCounts.getSecond())
            .with("{COUNT_ADDED}", givenCounts.getFirst())
            .with("{COUNT_DROPPED}", givenCounts.getSecond())
            .send();

        this.messageService.getMessage(config -> config.itemsAdminReceived)
            .receiver(targetPlayer)
            .with("{TYPE}", type)
            .with("{COUNT}", givenCounts.getFirst() + givenCounts.getSecond())
            .with("{COUNT_ADDED}", givenCounts.getFirst())
            .with("{COUNT_DROPPED}", givenCounts.getSecond())
            .send();
    }

    private List<ItemStack> findGuildSetItems(Player targetPlayer, String[] args) {
        if (args.length >= 3) {
            Optional<GuildItemSet> foundSet = itemsConfiguration.findGuildItemSet(args[2], true);
            if (foundSet.isEmpty()) {
                throw new InternalValidationException(
                    config -> config.itemsSetNotFound,
                    Replacement.string("{SET}", args[2]),
                    Replacement.string("{SETS}", String.join(", ", itemsConfiguration.getGuildItemSets().keySet()))
                );
            }

            return ItemUtils.buildRequiredItems(foundSet.get(), itemsConfiguration);
        }

        return ItemUtils.buildRequiredItems(guildItemSetService.getSetForPlayer(targetPlayer), itemsConfiguration);
    }

}
