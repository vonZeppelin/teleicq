package teleicq.db

import org.springframework.data.jpa.repository.*
import org.springframework.stereotype.*

@Repository
interface TelegramUserRepository: JpaRepository<TelegramUser, Int>