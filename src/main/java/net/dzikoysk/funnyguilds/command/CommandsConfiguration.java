package net.dzikoysk.funnyguilds.command;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.resources.types.PlayerType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.command.admin.*;
import net.dzikoysk.funnyguilds.command.user.*;
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
                .command("validity", commands.validity, new ValidityCommand());

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
