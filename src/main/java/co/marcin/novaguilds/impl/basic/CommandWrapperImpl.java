/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.CommandExecutor;
import co.marcin.novaguilds.api.basic.CommandWrapper;
import co.marcin.novaguilds.api.basic.MessageWrapper;
import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CommandWrapperImpl implements CommandWrapper {
	protected final Collection<Flag> flags = new HashSet<>();
	protected Class<? extends CommandExecutor> clazz;
	protected Permission permission;
	protected TabCompleter tabCompleter;
	protected CommandExecutor executor;
	protected MessageWrapper usageMessage;
	protected Resource executorVariable;
	protected String name;
	protected String genericCommand;

	@Override
	public Permission getPermission() {
		return permission;
	}

	@Override
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return permission.has(sender);
	}

	@Override
	public TabCompleter getTabCompleter() {
		return tabCompleter;
	}

	@Override
	public void setTabCompleter(TabCompleter tabCompleter) {
		this.tabCompleter = tabCompleter;
	}

	@Override
	public boolean hasTabCompleter() {
		return tabCompleter != null;
	}

	@Override
	public void setFlags(Flag... flags) {
		this.flags.clear();
		Collections.addAll(this.flags, flags);
	}

	@Override
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	@Override
	public Collection<Flag> getFlags() {
		return new HashSet<>(flags);
	}

	@Override
	public void setExecutor(CommandExecutor executor) {
		this.executor = executor;
	}

	@Override
	public CommandExecutor getExecutor() {
		return executor;
	}

	@Override
	public Class<? extends CommandExecutor> getExecutorClass() {
		return clazz;
	}

	@Override
	public boolean isReversed() {
		return CommandExecutor.Reversed.class.isAssignableFrom(getExecutorClass());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		NovaGuilds.getInstance().getCommandManager().execute(this, sender, args);
	}

	@Override
	public MessageWrapper getUsageMessage() {
		return usageMessage;
	}

	@Override
	public void setUsageMessage(MessageWrapper message) {
		this.usageMessage = message;
	}

	@Override
	public boolean allowedSender(CommandSender sender) {
		return sender instanceof Player || !hasFlag(Flag.NOCONSOLE);
	}

	@Override
	public Resource getExecutorVariable() {
		return executorVariable;
	}

	@Override
	public void executorVariable(Resource resource) {
		executorVariable = resource;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean hasGenericCommand() {
		return genericCommand != null;
	}

	@Override
	public String getGenericCommand() {
		return genericCommand;
	}

	@Override
	public void setGenericCommand(String genericCommand) {
		this.genericCommand = genericCommand;
	}
}
