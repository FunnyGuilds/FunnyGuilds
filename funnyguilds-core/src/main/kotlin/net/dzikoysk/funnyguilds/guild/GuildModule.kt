package net.dzikoysk.funnyguilds.guild

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.FunnyModule
import net.dzikoysk.funnyguilds.InitContext
import net.dzikoysk.funnyguilds.guild.commands.CreateGuildCommand
import net.dzikoysk.funnyguilds.guild.model.GuildDefinition
import net.dzikoysk.funnyguilds.guild.model.MembershipDefinition
import net.dzikoysk.funnyguilds.guild.model.SqlGuildRepository
import net.dzikoysk.funnyguilds.guild.model.SqlMembershipRepository
import net.dzikoysk.funnyguilds.server.ServerContext
import net.dzikoysk.funnyguilds.user.UserService

class GuildModule : FunnyModule {

    override fun onLoad(initContext: InitContext) {
        initContext.definitions.addAll(
            listOf(
                GuildDefinition,
                MembershipDefinition
            )
        )
    }

    override fun onEnable(context: ServerContext, funnyGuilds: FunnyGuilds) {
        val guildService = GuildService(
            guildRepository = SqlGuildRepository(funnyGuilds.database),
            membershipRepository = SqlMembershipRepository(funnyGuilds.database)
        )

        funnyGuilds.registerComponent(guildService)
        val userService = funnyGuilds.getComponent<UserService>() // we should declare dependency on user domain in onLoad

        context.registerCommand(
            CreateGuildCommand(
                guildService = guildService,
                userService = userService
            )
        )
    }

}