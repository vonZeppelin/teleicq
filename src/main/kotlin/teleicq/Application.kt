package teleicq

import com.pengrad.telegrambot.*
import io.ebean.*
import io.ebean.config.*
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
            "Creating PurpleFacade with id=$APP_NAME and user directory=$userDir"
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

    @Bean
    fun ebeanServer(@Value("\${db.url}") url: String,
                    @Value("\${db.user}") user: String,
                    @Value("\${db.password}") password: String): EbeanServer {
        logger.info {
            "Creating EbeanServer with url=$url and user=$user"
        }
        val serverConfig = ServerConfig().apply {
            dataSourceConfig.driver = "org.h2.Driver"
            dataSourceConfig.url = url
            dataSourceConfig.username = user
            dataSourceConfig.password = password

            isDdlGenerate = true
            isDdlRun = true
            name = APP_NAME
        }
        return EbeanServerFactory.create(serverConfig)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
