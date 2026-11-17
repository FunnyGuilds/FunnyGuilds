package net.dzikoysk.funnyguilds

import java.util.logging.Logger

class TestLogger(rootLogger: Logger) : FunnyGuildsLogger(rootLogger) {

    val errors = mutableListOf<String>()

    override fun error(content: String) {
        errors.add(content)
        super.error(content)
    }

    override fun parser(content: String) {
    }

}
