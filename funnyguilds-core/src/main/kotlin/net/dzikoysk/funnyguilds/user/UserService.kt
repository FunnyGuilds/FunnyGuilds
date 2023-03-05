package net.dzikoysk.funnyguilds.user

import net.dzikoysk.funnyguilds.FunnyComponent

class UserService(
    private val userRepository: UserRepository
) : FunnyComponent {

    fun createUser(id: UserId, name: String): User =
        userRepository.createUser(id, name)

    fun getUser(id: UserId): User? =
        userRepository.findUserById(id)

}