package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.resources.types.PlayerType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.admin.AddCommand;
import net.dzikoysk.funnyguilds.command.admin.BanCommand;
import net.dzikoysk.funnyguilds.command.admin.BaseAdminCommand;
import net.dzikoysk.funnyguilds.command.admin.DeathsCommand;
import net.dzikoysk.funnyguilds.command.admin.DeleteAdminCommand;
import net.dzikoysk.funnyguilds.command.admin.DeputyAdminCommand;
import net.dzikoysk.funnyguilds.command.admin.GuildsEnabledCommand;
import net.dzikoysk.funnyguilds.command.admin.KickAdminCommand;
import net.dzikoysk.funnyguilds.command.admin.KillsCommand;
import net.dzikoysk.funnyguilds.command.admin.LeaderAdminCommand;
import net.dzikoysk.funnyguilds.command.admin.LivesCommand;
import net.dzikoysk.funnyguilds.command.admin.MainCommand;
import net.dzikoysk.funnyguilds.command.admin.MoveCommand;
import net.dzikoysk.funnyguilds.command.admin.NameCommand;
import net.dzikoysk.funnyguilds.command.admin.PointsCommand;
import net.dzikoysk.funnyguilds.command.admin.ProtectionCommand;
import net.dzikoysk.funnyguilds.command.admin.SpyCommand;
import net.dzikoysk.funnyguilds.command.admin.TagCommand;
import net.dzikoysk.funnyguilds.command.admin.TeleportCommand;
import net.dzikoysk.funnyguilds.command.admin.UnbanCommand;
import net.dzikoysk.funnyguilds.command.admin.ValidityAdminCommand;
import net.dzikoysk.funnyguilds.command.user.AllyCommand;
import net.dzikoysk.funnyguilds.command.user.BaseCommand;
import net.dzikoysk.funnyguilds.command.user.BreakCommand;
import net.dzikoysk.funnyguilds.command.user.ConfirmCommand;
import net.dzikoysk.funnyguilds.command.user.CreateCommand;
import net.dzikoysk.funnyguilds.command.user.DeleteCommand;
import net.dzikoysk.funnyguilds.command.user.DeputyCommand;
import net.dzikoysk.funnyguilds.command.user.EnlargeCommand;
import net.dzikoysk.funnyguilds.command.user.EscapeCommand;
import net.dzikoysk.funnyguilds.command.user.FunnyGuildsCommand;
import net.dzikoysk.funnyguilds.command.user.GuildCommand;
import net.dzikoysk.funnyguilds.command.user.InfoCommand;
import net.dzikoysk.funnyguilds.command.user.InviteCommand;
import net.dzikoysk.funnyguilds.command.user.ItemsCommand;
import net.dzikoysk.funnyguilds.command.user.JoinCommand;
import net.dzikoysk.funnyguilds.command.user.KickCommand;
import net.dzikoysk.funnyguilds.command.user.LeaderCommand;
import net.dzikoysk.funnyguilds.command.user.LeaveCommand;
import net.dzikoysk.funnyguilds.command.user.PlayerCommand;
import net.dzikoysk.funnyguilds.command.user.PvPCommand;
import net.dzikoysk.funnyguilds.command.user.RankResetCommand;
import net.dzikoysk.funnyguilds.command.user.RankingCommand;
import net.dzikoysk.funnyguilds.command.user.SetBaseCommand;
import net.dzikoysk.funnyguilds.command.user.TopCommand;
import net.dzikoysk.funnyguilds.command.user.ValidityCommand;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.configs.PluginConfiguration.Commands.FunnyCommand;
import org.bukkit.Server;
import org.panda_lang.utilities.commons.text.Joiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class CommandsConfiguration {

    public FunnyCommands createFunnyCommands(Server server, FunnyGuilds funnyGuilds) {
        PluginConfiguration configuration = funnyGuilds.getPluginConfiguration();
        PluginConfiguration.Commands commands = configuration.commands;

        CommandComponents userCommands = new CommandComponents("user")
                .command("ally", commands.ally, new AllyCommand())
                .command("base", commands.base, new BaseCommand())
                .command("break", commands.break_, new BreakCommand())
                .command("confirm", commands.confirm, new ConfirmCommand())
                .command("create", commands.create, new CreateCommand())
                .command("delete", commands.delete, new DeleteCommand())
                .command("deputy", commands.deputy, new DeputyCommand())
                .command("enlarge", commands.enlarge, new EnlargeCommand())
                .command("escape", commands.escape, new EscapeCommand())
                .command("funnyguilds", commands.funnyguilds, new FunnyGuildsCommand())
                .command("guild", commands.guild, new GuildCommand())
                .command("info", commands.info, new InfoCommand())
                .command("invite", commands.invite, new InviteCommand())
                .command("items", commands.items, new ItemsCommand())
                .command("join", commands.join, new JoinCommand())
                .command("kick", commands.kick, new KickCommand())
                .command("leader", commands.leader, new LeaderCommand())
                .command("leave", commands.leave, new LeaveCommand())
                .command("player", commands.player, new PlayerCommand())
                .command("pvp", commands.pvp, new PvPCommand())
                .command("ranking", commands.ranking, new RankingCommand())
                .command("rank-reset", commands.rankReset, new RankResetCommand())
                .command("set-base", commands.setbase, new SetBaseCommand())
                .command("top", commands.top, new TopCommand())
                .command("validity", commands.validity, new ValidityCommand());

        CommandComponents adminCommands = new CommandComponents("admin")
                .command(new AddCommand())
                .command(new BaseAdminCommand())
                .command(new BanCommand())
                .command(new DeathsCommand())
                .command(new DeleteAdminCommand())
                .command(new DeputyAdminCommand())
                .command(new GuildsEnabledCommand())
                .command(new KickAdminCommand())
                .command(new KillsCommand())
                .command(new LeaderAdminCommand())
                .command(new LivesCommand())
                .command(new MainCommand())
                .command(new MoveCommand())
                .command(new NameCommand())
                .command(new PointsCommand())
                .command(new ProtectionCommand())
                .command(new SpyCommand())
                .command(new TagCommand())
                .command(new TeleportCommand())
                .command(new UnbanCommand())
                .command(new ValidityAdminCommand());

        return FunnyCommands.configuration(() -> funnyGuilds)
                .registerDefaultComponents()
                .placeholders(userCommands.placeholders)
                .type(new PlayerType(server))
                .registerComponents(userCommands.commands)
                .registerComponents(adminCommands.commands)
                .hook();
    }

    private final static class CommandComponents {

        private final String group;
        private final Map<String, Function<String, String>> placeholders = new HashMap<>();
        private final List<Object> commands = new ArrayList<>();

        private CommandComponents(String group) {
            this.group = group;
        }

        private CommandComponents command(String name, FunnyCommand configuration, Object command) {
            if (configuration.enabled) {
                this.commands.add(command);
                this.placeholders.put(group + "." + name + ".name", key -> configuration.name);
                this.placeholders.put(group + "." + name + ".aliases", key -> Joiner.on(", ").join(configuration.aliases).toString());
            }

            return this;
        }

        private CommandComponents command(Object command) {
            this.commands.add(command);
            return this;
        }

    }

}
