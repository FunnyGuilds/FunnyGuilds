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
import net.dzikoysk.funnyguilds.command.user.PlayerInfoCommand;
import net.dzikoysk.funnyguilds.command.user.PvPCommand;
import net.dzikoysk.funnyguilds.command.user.RankResetCommand;
import net.dzikoysk.funnyguilds.command.user.RankingCommand;
import net.dzikoysk.funnyguilds.command.user.SetBaseCommand;
import net.dzikoysk.funnyguilds.command.user.TopCommand;
import net.dzikoysk.funnyguilds.command.user.ValidityCommand;
import net.dzikoysk.funnyguilds.command.user.WarCommand;
import net.dzikoysk.funnyguilds.command.user.TntCommand;
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

        FunnyCommand enlargeCommand = commands.enlarge;
        enlargeCommand.enabled = enlargeCommand.enabled && configuration.enlargeEnable;

        CommandComponents userCommands = new CommandComponents("user")
                .command("ally", commands.ally, new AllyCommand())
                .command("base", commands.base, new BaseCommand())
                .command("break", commands.break_, new BreakCommand())
                .command("confirm", commands.confirm, new ConfirmCommand())
                .command("create", commands.create, new CreateCommand())
                .command("delete", commands.delete, new DeleteCommand())
                .command("deputy", commands.deputy, new DeputyCommand())
                .command("enlarge", enlargeCommand, new EnlargeCommand())
                .command("escape", commands.escape, new EscapeCommand())
                .command("funnyguilds", commands.funnyguilds, new FunnyGuildsCommand(funnyGuilds))
                .command("guild", commands.guild, new GuildCommand())
                .command("info", commands.info, new InfoCommand())
                .command("invite", commands.invite, new InviteCommand())
                .command("items", commands.items, new ItemsCommand())
                .command("join", commands.join, new JoinCommand())
                .command("kick", commands.kick, new KickCommand())
                .command("leader", commands.leader, new LeaderCommand())
                .command("leave", commands.leave, new LeaveCommand())
                .command("player", commands.player, new PlayerInfoCommand())
                .command("pvp", commands.pvp, new PvPCommand())
                .command("ranking", commands.ranking, new RankingCommand())
                .command("rank-reset", commands.rankReset, new RankResetCommand())
                .command("set-base", commands.setbase, new SetBaseCommand())
                .command("top", commands.top, new TopCommand())
                .command("validity", commands.validity, new ValidityCommand())
                .command("war", commands.war, new WarCommand())
                .command("tnt", commands.tnt, new TntCommand());

        CommandComponents adminCommands = new CommandComponents("admin")
                .command("add", commands.admin.add, new AddCommand())
                .command("base", commands.admin.base, new BaseAdminCommand())
                .command("ban", commands.admin.ban, new BanCommand())
                .command("deaths", commands.admin.deaths, new DeathsCommand())
                .command("delete", commands.admin.delete, new DeleteAdminCommand())
                .command("deputy", commands.admin.deputy, new DeputyAdminCommand())
                .command("guilds-enabled", commands.admin.enabled, new GuildsEnabledCommand())
                .command("kick", commands.admin.kick, new KickAdminCommand())
                .command("kills", commands.admin.kills, new KillsCommand())
                .command("leader", commands.admin.leader, new LeaderAdminCommand())
                .command("lives", commands.admin.lives, new LivesCommand())
                .command("main", commands.admin.main, new MainCommand())
                .command("move", commands.admin.move, new MoveCommand())
                .command("name", commands.admin.name, new NameCommand())
                .command("points", commands.admin.points, new PointsCommand())
                .command("protection", commands.admin.protection, new ProtectionCommand())
                .command("spy", commands.admin.spy, new SpyCommand())
                .command("tag", commands.admin.tag, new TagCommand())
                .command("teleport", commands.admin.teleport, new TeleportCommand())
                .command("unban", commands.admin.unban, new UnbanCommand())
                .command("validity", commands.admin.validity, new ValidityAdminCommand());

        return FunnyCommands.configuration(() -> funnyGuilds)
                .registerDefaultComponents()
                .placeholders(userCommands.placeholders)
                .placeholders(adminCommands.placeholders)
                .bind(new SettingsBind())
                .bind(new MessagesBind())
                .bind(new UserBind())
                .bind(new GuildBind())
                .type(new PlayerType(server))
                .completer(new GuildsCompleter())
                .completer(new MembersCompleter())
                .validator(new MemberValidator())
                .validator(new ManageValidator())
                .validator(new OwnerValidator())
                .registerComponents(userCommands.commands)
                .registerComponents(adminCommands.commands)
                .exceptionHandler(new FunnyGuildsExceptionHandler(funnyGuilds))
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
                this.placeholders.put(group + "." + name + ".name", key -> configuration.name);
                this.placeholders.put(group + "." + name + ".aliases", key -> Joiner.on(", ").join(configuration.aliases).toString());
                this.placeholders.put(group + "." + name + ".description", key -> "");
                this.commands.add(command);
            }

            return this;
        }

        private CommandComponents command(String name, String alias, Object command) {
            this.placeholders.put(group + "." + name + ".name", key -> alias);
            this.commands.add(command);
            return this;
        }

    }

}
