package net.dzikoysk.funnyguilds.feature.command.admin;

import dev.peri.yetanothermessageslibrary.replace.replacement.Replacement;
import java.util.Arrays;
import java.util.List;
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

        Player targetPlayer = Bukkit.getPlayer(UserValidation.requireUserByName(args[0]).getUUID());
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            throw new InternalValidationException(
                config -> config.itemsPlayerOffline,
                Replacement.string("{PLAYER}", args[0])
            );
        }

        Optional<ItemsGiveType> type = ItemsGiveType.fromCommandArg(args[1]);
        if (type.isEmpty()) {
            throw new InternalValidationException(
                config -> config.itemsAdminUnknownType,
                Replacement.string("{TYPE}", args[1])
            );
        }

        final ItemsGiveType resolvedType = type.get();
        List<ItemStack> items = switch (resolvedType) {
            case ItemsGiveType.GUILD -> findGuildSetItems(targetPlayer, args);
            case ItemsGiveType.BASE -> config.baseItems;
            case ItemsGiveType.JOIN -> config.joinItems;
            case ItemsGiveType.ENLARGE -> config.enlargeItems;
            case ItemsGiveType.VALIDITY -> config.validityItems;
            case ItemsGiveType.RANK_RESET -> config.rankResetItems;
            case ItemsGiveType.STATS_RESET -> config.statsResetItems;
            case ItemsGiveType.FIRST_GUILD_REWARD -> config.firstGuildRewards;
        };

        if (items.isEmpty()) {
            throw new InternalValidationException(
                config -> config.itemsAdminNoItems,
                Replacement.string("{TYPE}", resolvedType.commandArg)
            );
        }

        Pair<Integer, Integer> givenCounts = InventoryUtils.addItemsWithOverflowDrop(targetPlayer, items);

        this.messageService.getMessage(config -> config.itemsAdminGiven)
            .receiver(sender)
            .with("{PLAYER}", targetPlayer.getName())
            .with("{TYPE}", resolvedType.commandArg)
            .with("{COUNT}", givenCounts.getFirst() + givenCounts.getSecond())
            .with("{COUNT_ADDED}", givenCounts.getFirst())
            .with("{COUNT_DROPPED}", givenCounts.getSecond())
            .send();

        this.messageService.getMessage(config -> config.itemsAdminReceived)
            .receiver(targetPlayer)
            .with("{TYPE}", resolvedType.commandArg)
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

    public enum ItemsGiveType {
        GUILD("guild"),
        BASE("base"),
        JOIN("join"),
        ENLARGE("enlarge"),
        VALIDITY("validity"),
        RANK_RESET("rankReset"),
        STATS_RESET("statsReset"),
        FIRST_GUILD_REWARD("firstGuildReward");

        private final String commandArg;

        ItemsGiveType(String commandArg) {
            this.commandArg = commandArg;
        }

        public static final List<String> ALL_COMMAND_ARGS = Arrays.stream(values()).map(type -> type.commandArg).toList();

        public static Optional<ItemsGiveType> fromCommandArg(String arg) {
            return Arrays.stream(ItemsGiveType.values())
                .filter(type -> type.commandArg.equalsIgnoreCase(arg))
                .findFirst();
        }
    }
}
