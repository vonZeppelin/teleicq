package teleicq.db

import javax.persistence.*

@MappedSuperclass
abstract class BaseModel(
    @Id var id: Long,
    @Version var version: Long = 0
) : io.ebean.Model()

@Entity
class TelegramUser(
    id: Long,
    var firstName: String?,
    var lastName: String?,
    var userName: String?
) : BaseModel(id)
