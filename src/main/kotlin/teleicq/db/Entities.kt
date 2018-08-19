package teleicq.db

import java.io.Serializable
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity<T>(@Id val id: T, @Version val version: Long = 0) : Serializable

@Entity
class TelegramUser(
    id: Int,
    var firstName: String? = null,
    var lastName: String? = null,
    var userName: String? = null
) : BaseEntity<Int>(id)
