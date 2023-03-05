package net.dzikoysk.funnyguilds.guild.commands

import net.dzikoysk.funnyguilds.guild.GuildService
import net.dzikoysk.funnyguilds.guild.model.GuildName
import net.dzikoysk.funnyguilds.server.command.FunnyCommandEntry
import net.dzikoysk.funnyguilds.server.command.FunnyCommandFactory
import net.dzikoysk.funnyguilds.user.UserId
import net.dzikoysk.funnyguilds.user.UserId.Companion.toUserId
import net.dzikoysk.funnyguilds.user.UserService

const val CREATE_GUILD_PERMISSION = "funnyguilds.commands.create"

class CreateGuildCommand(
    private val commandName: String = "create",
    private val guildService: GuildService,
    private val userService: UserService
) : FunnyCommandFactory {

    override fun create(): FunnyCommandEntry =
        FunnyCommandEntry(
            pattern = "$commandName {name}",
            permission = CREATE_GUILD_PERMISSION
        ) {
            val callingUser = caller
                ?.let { userService.getUser(it.toUserId()) }
                ?: run {
                    sendMessage("You must be a player with user profile to execute this command!")
                    return@FunnyCommandEntry
                }

            val userHasGuild = guildService.getGuild(UserId(callingUser.id))
            require(userHasGuild == null) { "You are already in a guild ${userHasGuild!!.name}!" }

            val guildName = getArgument("name")
            val guildWithSameNameExists = guildService.getGuild(GuildName(guildName))
            require(guildWithSameNameExists == null) { "Guild with name $guildName already exists!" }

            val createdGuild = guildService.createGuild(
                name = GuildName(guildName),
                owner = UserId(callingUser.id)
            )

            createdGuild.consume(
                { guild -> sendMessage("Created guild ${guild.name}!") },
                { error -> sendMessage("Failed to create guild: $error") }
            )
        }

}