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

package co.marcin.novaguilds.enums;

public enum GuildPermission {
	BLOCK_BREAK,
	BLOCK_PLACE,

	MOB_ATTACK,
	MOB_RIDE,
	MOB_LEASH,

	INTERACT,

	ABANDON,
	KICK,
	EFFECT,
	INVITE,
	BANK_PAY,
	BANK_WITHDRAW,
	PVPTOGGLE,
	ALLY_CANCEL,
	ALLY_INVITE_SEND,
	ALLY_INVITE_CANCEL,
	ALLY_ACCEPT,
	BUYLIFE,
	BUYSLOT,
	HOME_SET,
	HOME_TELEPORT,
	OPENINVITATION,
	WAR_INVITE_SEND,
	WAR_INVITE_CANCEL,
	WAR_INVITE_ACCEPT,
	WAR_START,

	SET_NAME,
	SET_TAG,

	VAULT_ACCESS,
	VAULT_PUT,
	VAULT_TAKE,
	VAULT_PLACE,
	VAULT_BREAK,

	REGION_CREATE,
	REGION_REMOVE,
	REGION_RESIZE,

	RANK_DELETE,
	RANK_LIST,
	RANK_SET,
	RANK_EDIT;

	/**
	 * Gets permission from a string
	 *
	 * @param name enum name
	 * @return the permission
	 */
	public static GuildPermission fromString(String name) {
		try {
			return valueOf(name.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return null;
		}
	}
}
