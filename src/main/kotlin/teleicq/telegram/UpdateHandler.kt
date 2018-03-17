package teleicq.telegram

import com.pengrad.telegrambot.*
import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.request.*
import mu.*
import org.springframework.stereotype.*

private val logger = KotlinLogging.logger {}

interface UpdateHandler {
    interface Chain {
        val update: Update

        fun proceed(): BaseRequest<*, *>?
    }

    fun handle(chain: Chain): BaseRequest<*, *>?
}

@Component
internal class UpdatesProcessor(bot: TelegramBot, updateHandlers: List<UpdateHandler>) {
    private class ChainImpl(override val update: Update, private val handlers: List<UpdateHandler>) : UpdateHandler.Chain {
        override fun proceed() = handlers.firstOrNull()?.handle(ChainImpl(update, handlers.drop(1)))
    }

    init {
        bot.setUpdatesListener {
            it.fold(UpdatesListener.CONFIRMED_UPDATES_ALL) { prevId, update ->
                try {
                    ChainImpl(update, updateHandlers).proceed()?.let {
                        bot.execute(it)
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
