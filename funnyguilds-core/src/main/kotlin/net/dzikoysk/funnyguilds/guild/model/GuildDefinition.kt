package net.dzikoysk.funnyguilds.guild.model

import com.dzikoysk.sqiffy.SqiffyDatabase
import com.dzikoysk.sqiffy.definition.Constraint
import com.dzikoysk.sqiffy.definition.ConstraintType.PRIMARY_KEY
import com.dzikoysk.sqiffy.definition.DataType.UUID_TYPE
import com.dzikoysk.sqiffy.definition.DataType.VARCHAR
import com.dzikoysk.sqiffy.definition.Definition
import com.dzikoysk.sqiffy.definition.DefinitionVersion
import com.dzikoysk.sqiffy.definition.Property
import com.dzikoysk.sqiffy.dsl.Row
import com.dzikoysk.sqiffy.dsl.eq
import net.dzikoysk.funnyguilds.FunnyGuildsVersion.V_5_0_0
import java.util.UUID

private const val MAX_GUILD_NAME_LENGTH = 64

@Definition([
    DefinitionVersion(
        version = V_5_0_0,
        name = "guilds",
        properties = [
            Property(name = "id", type = UUID_TYPE),
            Property(name = "name", type = VARCHAR, details = "64"),
        ],
        constraints = [
            Constraint(type = PRIMARY_KEY, on = ["id"], name = "pk_guilds_id"),
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

    fun createGuild(name: GuildName): Guild

    fun findGuildById(id: GuildId): Guild?

    fun findGuildByName(name: GuildName): Guild?

}

class SqlGuildRepository(private val database: SqiffyDatabase) : GuildRepository {

    override fun createGuild(name: GuildName): Guild =
        UUID.randomUUID()
            .let { guildId ->
                database
                    .insert(GuildTable) {
                        it[GuildTable.id] = guildId
                        it[GuildTable.name] = name.value
                    }
                    .execute()
                    .let {
                        Guild(
                            id = guildId,
                            name = name.value
                        )
                    }
            }

    override fun findGuildById(id: GuildId): Guild? =
        database.select(GuildTable)
            .where { GuildTable.id eq id.value }
            .map { it.toGuild() }
            .firstOrNull()

    override fun findGuildByName(name: GuildName): Guild? =
        database.select(GuildTable)
            .where { GuildTable.name eq name.value }
                .map { it.toGuild() }
                .firstOrNull()

    private fun Row.toGuild(): Guild =
        Guild(
            id = this[GuildTable.id],
            name = this[GuildTable.name],
        )

}