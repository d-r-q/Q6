package q6.core.users.impl

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import q6.core.users.api.*
import q6.platform.auth.PasswordEncoder
import q6.platform.exposed.enumArray

class UsersServiceImpl(
    private val db: Database
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
        UserId(inserted.value)
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

    fun addUser(registerUserRequest: RegisterUserRequest) = Users.insert {
        it[email] = registerUserRequest.email
        it[password] = registerUserRequest.password
        it[name] = registerUserRequest.name
        it[roles] = arrayOf(Role.ROLE_USER)
    }[Users.id]

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