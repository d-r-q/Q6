package q6.platform.auth

import at.favre.lib.crypto.bcrypt.BCrypt


object PasswordEncoder {

    private val bcrypt = BCrypt.withDefaults()
    private val verifyer = BCrypt.verifyer()

    fun encode(pass: String): String {
        return bcrypt.hashToString(10, pass.toCharArray())
    }

    fun verify(pass: String, bcryptHash: String) =
        verifyer.verify(pass.toCharArray(), bcryptHash.toCharArray())

}