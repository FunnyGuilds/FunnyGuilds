@file:Suppress("RemoveRedundantQualifierName")

package net.dzikoysk.funnyguilds.guild.model

import com.dzikoysk.sqiffy.Constraint
import com.dzikoysk.sqiffy.ConstraintType.PRIMARY_KEY
import com.dzikoysk.sqiffy.DataType.UUID_BINARY
import com.dzikoysk.sqiffy.DataType.VARCHAR
import com.dzikoysk.sqiffy.Definition
import com.dzikoysk.sqiffy.DefinitionVersion
import com.dzikoysk.sqiffy.Property
import com.dzikoysk.sqiffy.Sqiffy
import net.dzikoysk.funnyguilds.FunnyGuildsVersion.V_5_0_0
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import panda.std.Result
import java.util.UUID

private const val MAX_GUILD_NAME_LENGTH = 64

@Definition([
    DefinitionVersion(
        version = V_5_0_0,
        name = "guilds",
        properties = [
            Property(name = "id", type = UUID_BINARY),
            Property(name = "name", type = VARCHAR, details = "64"),
        ],
        constraints = [
            Constraint(type = PRIMARY_KEY, on = "id", name = "pk_guilds_id"),
        ]
    )
])
object GuildDefinition

@JvmInline
value class GuildId(val value: UUID)

@JvmInline
value class GuildName(val value: String) {
    init {
        require(value.length <= MAX_GUILD_NAME_LENGTH) { "Guild name cannot be longer than $MAX_GUILD_NAME_LENGTH characters" }
    }
}

interface GuildRepository {

    fun createGuild(name: GuildName): Result<Guild, String>

    fun findGuildById(id: GuildId): Guild?

    fun findGuildByName(name: GuildName): Guild?

}

class SqlGuildRepository(private val sqiffy: Sqiffy) : GuildRepository {

    override fun createGuild(name: GuildName): Result<Guild, String> =
        sqiffy.transaction {
            val guild = Guild(
                id = UUID.randomUUID(),
                name = name.value,
            )

            val result = GuildTable.insert {
                it[id] = guild.id
                it[GuildTable.name] = guild.name
            }

            Result.ok<Guild, String>(guild)
                .filter({ result.insertedCount == 1 }, { "Failed to insert guild: $guild" })
        }

    override fun findGuildById(id: GuildId): Guild? =
        transaction {
            GuildTable.select { GuildTable.id eq id.value }
                .map { it.toGuild() }
                .firstOrNull()
        }

    override fun findGuildByName(name: GuildName): Guild? =
        sqiffy.transaction {
            GuildTable.select { GuildTable.name eq name.value }
                .map { it.toGuild()}
                .firstOrNull()
        }

    private fun ResultRow.toGuild(): Guild =
        Guild(
            id = this[GuildTable.id],
            name = this[GuildTable.name],
        )

}