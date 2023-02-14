@file:Suppress("RemoveRedundantQualifierName")

package net.dzikoysk.funnyguilds.user

import com.dzikoysk.sqiffy.Constraint
import com.dzikoysk.sqiffy.ConstraintType.PRIMARY_KEY
import com.dzikoysk.sqiffy.DataType.UUID_BINARY
import com.dzikoysk.sqiffy.DataType.VARCHAR
import com.dzikoysk.sqiffy.Definition
import com.dzikoysk.sqiffy.DefinitionVersion
import com.dzikoysk.sqiffy.Property
import java.util.UUID
import com.dzikoysk.sqiffy.Sqiffy
import net.dzikoysk.funnyguilds.FunnyGuildsVersion
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface UserRepository {

    fun createUser(playerId: UUID, name: String): User

    fun findUserById(id: UUID): User?

}

@Definition([
    DefinitionVersion(
        version = FunnyGuildsVersion.V_5_0_0,
        name = "funnyguilds_users",
        properties = [
            Property(name = "id", type = UUID_BINARY),
            Property(name = "name", type = VARCHAR, details = "48"),
        ],
        constraints = [
            Constraint(type = PRIMARY_KEY, name = "pk_id", on = "id"),
        ]
    )
])
object UserDefinition

class SqlUserRepository(private val sqiffy: Sqiffy) : UserRepository {

    override fun createUser(playerId: UUID, name: String): User =
        sqiffy.transaction {
            val user = User(
                id = playerId,
                name = name
            )

            println()

            val result = UserTable.insert {
                it[UserTable.id] = user.id
                it[UserTable.name] = user.name
            }

            require(result.insertedCount == 1) { "Failed to insert user: $user" }
            user
        }


    override fun findUserById(id: UUID): User? =
        sqiffy.transaction {
            UserTable.select { UserTable.id eq id }
                .firstOrNull()
                ?.let {
                    User(
                        id = it[UserTable.id],
                        name = it[UserTable.name],
                    )
                }
        }

}