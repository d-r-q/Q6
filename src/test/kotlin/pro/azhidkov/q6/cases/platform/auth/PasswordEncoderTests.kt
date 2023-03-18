package pro.azhidkov.q6.cases.platform.auth

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import q6.platform.auth.PasswordEncoder


class PasswordEncoderTests {

    @Test
    fun `PasswordEncoder should verify pass with its own hash`() {
        // Given
        val pass = "password"
        val hash = PasswordEncoder.encode(pass)

        // When
        val verificationResult = PasswordEncoder.verify(pass, hash)

        // Then
        assertThat(verificationResult.verified, equalTo(true))
    }

}