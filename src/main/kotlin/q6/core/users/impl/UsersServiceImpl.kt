package q6.core.users.impl

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import q6.core.users.api.*
import q6.platform.auth.PasswordEncoder
import q6.platform.exposed.enumArray
import q6.platform.exposed.isDuplicatedUniqueKey
import q6.platform.kotlin.ifFailure
import q6.platform.rmq.RabbitMqClient

class UsersServiceImpl(
    private val db: Database,
    private val rmqClient: RabbitMqClient
) : UsersService {

    init {
        transaction(db) {
            SchemaUtils.checkMappingConsistence(Users)
        }
    }

    override fun registerUser(registerUserRequest: RegisterUserRequest): UserId = transaction(db) {
        val inserted = Repo.addUser(
            registerUserRequest.copy(password = PasswordEncoder.encode(registerUserRequest.password))
        )

        val userId = UserId(inserted.value)
        rmqClient.send(USER_REGISTERED_EVENTS_QUEUE, UserRegisteredEvent(userId.id.toString()))

        userId
    }

    override fun findById(userId: UserId): User? = transaction(db) {
        Repo.findById(userId)
    }

    override fun findByEmail(email: String): User? = transaction(db) {
        Repo.findByEmail(email)
    }

}

private object Users : LongIdTable("users") {
    val email = varchar("email", 256).uniqueIndex()
    val password = varchar("password", 60)
    val name = varchar("name", 256)
    val roles = enumArray("roles", size = null, Role::name) { Role.valueOf(it as String) }
}

private object Repo {

    fun addUser(registerUserRequest: RegisterUserRequest): EntityID<Long> {
        val res = Result.runCatching {
            Users.insert {
                it[email] = registerUserRequest.email
                it[password] = registerUserRequest.password
                it[name] = registerUserRequest.name
                it[roles] = arrayOf(Role.ROLE_USER)
            }[Users.id]
        }
        res.ifFailure(::isDuplicatedUniqueKey) {
            throw DuplicatedEmail(registerUserRequest.email, it)
        }

        return res.getOrThrow()
    }


    fun findByEmail(email: String): User? =
        Users.select { Users.email eq email }
            .singleOrNull()
            ?.let { mapUserRow(it) }

    fun findById(id: UserId): User? =
        Users.select { Users.id eq id.id }
            .singleOrNull()
            ?.let { mapUserRow(it) }

    private fun mapUserRow(it: ResultRow) =
        User(UserId(it[Users.id].value), it[Users.email], it[Users.password], it[Users.roles].toSet())

}
