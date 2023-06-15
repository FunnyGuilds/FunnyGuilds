package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyComponent
import net.dzikoysk.funnyguilds.user.model.User
import net.dzikoysk.funnyguilds.user.model.UserId
import net.dzikoysk.funnyguilds.user.model.UserRepository

class UserService(
    private val userRepository: UserRepository
) : FunnyComponent {

    fun createUser(id: UserId, name: String): User =
        userRepository.createUser(id, name)

    fun getUser(id: UserId): User? =
        userRepository.findUserById(id)

}