package teleicq.telegram

import com.pengrad.telegrambot.*
import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.request.*
import mu.*
import org.springframework.stereotype.*

interface Chain {
    val update: Update

    fun proceed(): List<BaseRequest<*, *>>
}

interface UpdateHandler {
    fun handle(chain: Chain): List<BaseRequest<*, *>>
}

private val logger = KotlinLogging.logger {}

@Component
internal class UpdatesProcessor(bot: TelegramBot, private val handlers: List<UpdateHandler>) {
    private inner class ChainImpl(override val update: Update, private val idx: Int = 0) : Chain {
        override fun proceed(): List<BaseRequest<*, *>> {
            val handler = handlers.getOrNull(idx) ?: return emptyList()
            return handler.handle(ChainImpl(update, idx + 1))
        }
    }

    init {
        bot.setUpdatesListener {
            it.fold(UpdatesListener.CONFIRMED_UPDATES_ALL) { prevId, update ->
                try {
                    ChainImpl(update).proceed().forEach {
                        val response = bot.execute(it)
                        logger.debug {
                            "Sending request $response"
                        }
                    }
                } catch (e: Exception) {
                    logger.warn(e) {
                        "Error processing update with id=${update.updateId()}"
                    }
                    return@fold prevId
                }
                update.updateId()
            }
        }
    }
}
