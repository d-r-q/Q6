package q6.core.accounts

import org.jetbrains.exposed.sql.Database

data class AddAccount(
    val name: String
)

class AccountsService(
    private val db: Database
) {

    fun addAccount(cmd: AddAccount) {
        TODO()
    }

}