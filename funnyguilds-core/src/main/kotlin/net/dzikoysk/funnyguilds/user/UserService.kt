package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyComponent
import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) : FunnyComponent {

    fun createUser(id: UUID, name: String): User =
        userRepository.createUser(id, name)

    fun getUser(id: UUID): User? =
        userRepository.findUserById(id)

}