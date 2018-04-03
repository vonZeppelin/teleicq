package teleicq.telegram

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.model.request.*
import com.pengrad.telegrambot.request.*
import io.ebean.*
import org.springframework.core.annotation.*
import org.springframework.stereotype.*
import teleicq.db.*

private const val CONFIRM_STOP = "stopYes"
private const val CANCEL_STOP = "stopNo"

@Component @Order(1)
internal class CallbackQueryHandler(private val db: EbeanServer) : UpdateHandler {
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        val query = chain.update.callbackQuery() ?: return chain.proceed()
        val msg = query.message()

        return when (query.data()) {
            CONFIRM_STOP -> {
                db.find(TelegramUser::class.java, query.from().id())?.let(db::delete)
                listOf(
                    AnswerCallbackQuery(query.id()).text("Good bye!"),
                    EditMessageReplyMarkup(msg.chat().id(), msg.messageId())
                )
            }
            CANCEL_STOP -> listOf(
                AnswerCallbackQuery(query.id()),
                EditMessageReplyMarkup(msg.chat().id(), msg.messageId())
            )
            else -> listOf(AnswerCallbackQuery(query.id()))
        }
    }
}

@Component @Order(2)
internal object DefaultMessageHandler : UpdateHandler {
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        val msg = chain.update.message() ?: return emptyList()
        val requests = chain.proceed()
        if (requests.isEmpty()) {
            return listOf(
                SendMessage(msg.chat().id(), "I didn't understand you \uD83E\uDD37")
            )
        }
        return requests
    }
}

@Component @Order(3)
internal class BotCommandHandler(private val db: EbeanServer) : UpdateHandler {
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        val msg = chain.update.message()
        val chat = msg.chat().id()
        val command = msg.entities()
                         .orEmpty()
                         .find { it.type() == MessageEntity.Type.bot_command }
                         ?.run { msg.text().substring(offset() + 1, offset() + length()) }

        return when (command) {
            "start" -> {
                with(msg.from()) {
                    val user = db.find(TelegramUser::class.java, id())
                    if (user == null) {
                        db.save(
                            TelegramUser(id().toLong(), firstName(), lastName(), username())
                        )
                    } else {
                        user.firstName = firstName()
                        user.lastName = lastName()
                        user.userName = username()
                        db.update(user)
                    }
                }
                listOf(SendMessage(chat, "Hi, I'm TeleICQ bot!"))
            }
            "stop" -> {
                val user = db.find(TelegramUser::class.java, msg.from().id())
                if (user == null) {
                    listOf(SendMessage(chat, "We haven't even started! \uD83D\uDE09"))
                } else {
                    val keyboard = InlineKeyboardMarkup(
                        arrayOf(
                            InlineKeyboardButton("Yes").callbackData(CONFIRM_STOP),
                            InlineKeyboardButton("No").callbackData(CANCEL_STOP)
                        )
                    )
                    listOf(SendMessage(chat, "Are you sure? \uD83D\uDE1F").replyMarkup(keyboard))
                }
            }
            "help" -> listOf(SendMessage(chat, "TODO help"))
            "settings" -> listOf(SendMessage(chat, "TODO settings"))
            "register" -> listOf(SendMessage(chat, "TODO register"))
            null -> chain.proceed()
            else -> listOf(SendMessage(chat, "Unknown command"))
        }
    }
}

@Component @Order(4)
internal object TextMessageHandler : UpdateHandler {
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        return chain.update.message().run {
            listOf(SendMessage(chat().id(), text()))
        }
    }
}
