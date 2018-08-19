package teleicq

import com.pengrad.telegrambot.*
import mu.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import teleicq.purple.*

const val APP_NAME = "teleicq"

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class Application {
    @Bean
    fun purpleFacade(@Value("\${purple.userDir}") userDir: String): PurpleFacade {
        logger.info {
            "Creating PurpleFacade with id=$APP_NAME, user directory=$userDir"
        }
        return PurpleFacade(APP_NAME, userDir)
    }

    @Bean
    fun telegramBot(@Value("\${bot.token}") token: String): TelegramBot {
        logger.info {
            "Creating TelegramBot with token=****${token.takeLast(4)}"
        }
        return TelegramBot.Builder(token).build()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
