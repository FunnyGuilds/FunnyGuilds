package net.dzikoysk.funnyguilds.guild

import net.dzikoysk.funnyguilds.FunnyComponent
import net.dzikoysk.funnyguilds.guild.model.Guild
import net.dzikoysk.funnyguilds.guild.model.GuildId
import net.dzikoysk.funnyguilds.guild.model.GuildName
import net.dzikoysk.funnyguilds.guild.model.GuildRepository
import net.dzikoysk.funnyguilds.guild.model.MembershipRepository
import net.dzikoysk.funnyguilds.guild.model.MembershipRole
import net.dzikoysk.funnyguilds.user.UserId
import panda.std.Result

class GuildService(
    private val guildRepository: GuildRepository,
    private val membershipRepository: MembershipRepository
) : FunnyComponent {

    fun createGuild(name: GuildName, owner: UserId): Result<Guild, String> =
        guildRepository.createGuild(name)
            .peek { // TODO: replace with flatMap
                membershipRepository.createMembership(
                    guildId = GuildId(it.id),
                    userId = owner,
                    role = MembershipRole.OWNER
                )
            }

    fun getGuild(userId: UserId): Guild? =
        membershipRepository.findMembershipByUser(userId)
            ?.let { guildRepository.findGuildById(GuildId(it.guildId)) }

    fun getGuild(guildId: GuildId): Guild? =
        guildRepository.findGuildById(guildId)

    fun getGuild(name: GuildName): Guild? =
        guildRepository.findGuildByName(name)

}