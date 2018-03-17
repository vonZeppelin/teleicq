package teleicq.telegram

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.request.*
import org.springframework.core.annotation.*
import org.springframework.stereotype.*

@Component @Order(1)
internal object DefaultHandler : UpdateHandler {
    override fun handle(chain: UpdateHandler.Chain): BaseRequest<*, *>? {
        val message = chain.update.message()
        if (message != null) {
            return chain.proceed()?: SendMessage(message.chat().id(), """I didn't understand you ¯\_(ツ)_/¯""")
        }
        return null
    }
}

@Component @Order(2)
internal object BotCommandHandler : UpdateHandler {
    override fun handle(chain: UpdateHandler.Chain): BaseRequest<*, *>? {
        val message = chain.update.message()
        val chat = message.chat().id()
        val command = message.entities()
                             .orEmpty()
                             .find { it.type() == MessageEntity.Type.bot_command }
                             ?.run { message.text().substring(offset() + 1, offset() + length()) }

        return when (command) {
            "start" -> SendMessage(chat, "Hi, I'm TeleICQ bot!")
            "stop" -> SendMessage(chat, "TODO stop")
            "help" -> SendMessage(chat, "TODO help")
            "settings" -> SendMessage(chat, "TODO settings")
            "register" -> SendMessage(chat, "TODO register")
            null -> chain.proceed()
            else -> SendMessage(chat, "Unknown command")
        }
    }
}

@Component @Order(3)
internal object TextMessageHandler : UpdateHandler {
    override fun handle(chain: UpdateHandler.Chain): BaseRequest<*, *>? {
        return chain.update.message().run { SendMessage(chat().id(), text()) }
    }
}
