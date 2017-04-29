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

package co.marcin.novaguilds.command.admin;

import co.marcin.novaguilds.api.storage.Migrant;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.DataStorageType;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.impl.storage.MigrantImpl;
import co.marcin.novaguilds.impl.storage.StorageConnector;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CommandAdminMigrate extends AbstractCommandExecutor {
	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length != 2) {
			getCommand().getUsageMessage().send(sender);
			return;
		}

		DataStorageType dataStorageType;
		try {
			dataStorageType = DataStorageType.valueOf(args[1].toUpperCase());
		}
		catch(IllegalArgumentException e) {
			Message.CHAT_ADMIN_MIGRATE_INVALIDTYPE.send(sender);
			return;
		}

		if(dataStorageType == plugin.getConfigManager().getDataStorageType()) {
			Message.CHAT_ADMIN_MIGRATE_SAMETYPE.send(sender);
			return;
		}

		if(!args[0].equalsIgnoreCase("from") && !args[0].equalsIgnoreCase("to")) {
			Message.CHAT_INVALIDPARAM.send(sender);
			return;
		}

		Storage storage = new StorageConnector(dataStorageType).getStorage();

		Storage toStorage, fromStorage;
		if(args[0].equalsIgnoreCase("from")) {
			fromStorage = storage;
			toStorage = plugin.getStorage();
		}
		else {
			fromStorage = plugin.getStorage();
			toStorage = storage;
		}

		toStorage.registerManagers();
		Migrant migrant = new MigrantImpl(fromStorage, toStorage);
		migrant.migrate();
		migrant.save();

		Message.CHAT_ADMIN_MIGRATE_SUCCESS.send(sender);
	}

	@Override
	protected Collection<String> tabCompleteOptions(CommandSender sender, String[] args) {
		Set<String> set = new HashSet<>();

		if(args.length == 1) {
			set.add("from");
			set.add("to");
		}
		else if(args.length == 2) {
			for(DataStorageType dataStorageType : DataStorageType.values()) {
				set.add(dataStorageType.name());
			}
		}

		return set;
	}
}
