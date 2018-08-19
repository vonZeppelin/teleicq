package teleicq.telegram

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.model.request.*
import com.pengrad.telegrambot.request.*
import org.springframework.core.annotation.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*
import teleicq.db.*

private const val SHRUG_EMOJI = "\uD83E\uDD37"
private const val WINKING_FACE_EMOJI = "\uD83D\uDE09"
private const val WORRIED_FACE_EMOJI = "\uD83D\uDE1F"

private const val CANCEL_STOP_CALLBACK = "stopNo"
private const val CONFIRM_STOP_CALLBACK = "stopYes"

@Component @Order(1)
internal class CallbackQueryHandler(private val userRepo: TelegramUserRepository) : UpdateHandler {
    @Transactional
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        val query = chain.update.callbackQuery() ?: return chain.proceed()
        val msg = query.message()

        return when (query.data()) {
            CONFIRM_STOP_CALLBACK -> {
                val userId = query.from().id()
                if (userRepo.existsById(userId)) {
                    userRepo.deleteById(userId)
                }
                listOf(
                    AnswerCallbackQuery(query.id()).text("Good bye!"),
                    EditMessageReplyMarkup(msg.chat().id(), msg.messageId())
                )
            }
            CANCEL_STOP_CALLBACK -> listOf(
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
                SendMessage(msg.chat().id(), "I didn't understand you $SHRUG_EMOJI")
            )
        }
        return requests
    }
}

@Component @Order(3)
internal class BotCommandHandler(private val userRepo: TelegramUserRepository) : UpdateHandler {
    @Transactional
    override fun handle(chain: Chain): List<BaseRequest<*, *>> {
        val msg = chain.update.message()
        val chat = msg.chat().id()
        val command = msg.entities()
                         .orEmpty()
                         .find { it.type() == MessageEntity.Type.bot_command }
                         ?.run { msg.text().substring(offset() + 1, offset() + length()) }

        return when (command) {
            null -> chain.proceed()
            "start" -> {
                with(msg.from()) {
                    val user = userRepo.findById(id()).orElseGet { TelegramUser(id()) }
                    user.firstName = firstName()
                    user.lastName = lastName()
                    user.userName = username()
                    userRepo.save(user)
                }
                listOf(SendMessage(chat, "Hi, I'm TeleICQ bot!"))
            }
            "stop" -> {
                if (userRepo.existsById(msg.from().id())) {
                    val keyboard = InlineKeyboardMarkup(
                        arrayOf(
                            InlineKeyboardButton("Yes").callbackData(CONFIRM_STOP_CALLBACK),
                            InlineKeyboardButton("No").callbackData(CANCEL_STOP_CALLBACK)
                        )
                    )
                    listOf(SendMessage(chat, "Are you sure $WORRIED_FACE_EMOJI?").replyMarkup(keyboard))
                } else {
                    listOf(SendMessage(chat, "We haven't even started $WINKING_FACE_EMOJI!"))
                }
            }
            "help" -> listOf(SendMessage(chat, "TODO help"))
            "settings" -> listOf(SendMessage(chat, "TODO settings"))
            "register" -> listOf(SendMessage(chat, "TODO register"))
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
