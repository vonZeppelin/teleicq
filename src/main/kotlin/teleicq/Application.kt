package teleicq

import com.pengrad.telegrambot.*
import mu.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class Application {
    @Bean
    fun purpleFacade(@Value("\${purple.id}") id: String,
                     @Value("\${purple.userDir}") userDir: String,
                     @Value("\${purple.debug}") debug: Boolean): PurpleFacade {
        logger.info {
            "Creating PurpleFacade with id=$id and user directory=$userDir"
        }
        return PurpleFacade(id, userDir, debug)
    }

    @Bean
    fun telegramBot(@Value("\${bot.token}") token: String): TelegramBot {
        logger.info("Creating TelegramBot with token=******")
        return TelegramBot.Builder(token).build()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
