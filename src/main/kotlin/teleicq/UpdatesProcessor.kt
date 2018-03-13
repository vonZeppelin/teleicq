package teleicq

import com.pengrad.telegrambot.*
import com.pengrad.telegrambot.model.*
import mu.*
import org.springframework.stereotype.*

private val logger = KotlinLogging.logger {}

@Component
class UpdatesProcessor(private val bot: TelegramBot, private val purple: PurpleFacade) {
    init {
        bot.setUpdatesListener {
            it.fold(UpdatesListener.CONFIRMED_UPDATES_NONE) { prevId, update ->
                try {
                    processUpdate(update)
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

    fun processUpdate(update: Update) {
        println(update)
    }
}
