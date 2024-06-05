package ru.deltadelete.netial.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dao.findByUserName
import ru.deltadelete.netial.types.Error
import ru.deltadelete.netial.utils.Config
import java.util.*
import kotlin.time.Duration.Companion.days

fun Application.configureSecurity() {
    val jwtAudience = Config.jwt.jwtAudience
    val jwtDomain = Config.jwt.jwtDomain
    val jwtRealm = Config.jwt.jwtRealm
    val jwtSecret = Config.jwt.jwtSecret
    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                jwtVerifier(jwtSecret, jwtAudience, jwtDomain)
            )
            validate { credential ->
                validate(credential, jwtAudience)
            }
        }
    }

    routing {
        route("/api") {
            post("/login") {
                if (call.request.header(HttpHeaders.ContentType) != "application/json") {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Error.UserError("Wrong content type", HttpStatusCode.BadRequest)
                    )
                    return@post
                }
                val credentials = call.receive<LoginRequest>()

                val user = transaction {
                    User.findByUserName(credentials.userName)
                }

                if (user == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Error.UserError("Пользователь с этим именем не найден", HttpStatusCode.BadRequest)
                    )
                    return@post
                }

                if (
                    !(credentials.userName == user.userName
                            && BCrypt.checkpw(credentials.password, user.passwordHash))
                ) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Error.UserError("Неверное имя пользователя или пароль", HttpStatusCode.BadRequest)
                    )
                    return@post
                }

                val expireTime = Clock.System.now().plus(7.days)
                val token = JWT.create()
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .withSubject(user.id.toString())
                    .withClaim("userName", user.userName)
                    .withIssuedAt(Clock.System.now().toJavaInstant())
                    .withExpiresAt(expireTime.toJavaInstant())
                    .sign(Algorithm.HMAC256(jwtSecret))
                call.respond(
                    HttpStatusCode.OK,
                    JWTResponse(token)
                )

                transaction {
                    user.lastLoginDate = Clock.System.now()
                }
            }

            authenticate("auth-jwt", strategy = AuthenticationStrategy.FirstSuccessful) {
                get("/protected") {
                    val principal = call.principal<JWTPrincipal>()
                    call.respond(
                        HttpStatusCode.OK,
                        """
                        Hello from protected route, ${principal?.getClaim("userName", String::class)}!
                        Token expires at ${principal?.expiresAt?.toInstant()?.toKotlinInstant()} 
                        """.trimIndent()
                    )
                }
            }
        }
    }
}

fun jwtVerifier(
    jwtSecret: String,
    jwtAudience: String,
    jwtDomain: String,
): JWTVerifier = JWT
    .require(Algorithm.HMAC256(jwtSecret))
    .withAudience(jwtAudience)
    .withIssuer(jwtDomain)
    .withClaimPresence("userName")
    .acceptLeeway(3600)
    .build()

fun validate(
    credential: JWTCredential,
    jwtAudience: String,
): JWTPrincipal? {
    val hasSubject = !credential.payload.subject.isNullOrBlank()
    val hasAudience = credential.payload.audience.contains(jwtAudience)
    val hasExpired = Clock.System.now().toJavaInstant().isAfter(credential.payload.expiresAt?.toInstant())

    return if (hasSubject && hasAudience && !hasExpired) {
        JWTPrincipal(credential.payload)
    } else {
        null
    }
}

fun authorize(token: String): JWTPrincipal?? {
    try {
        val verify = jwtVerifier(Config.jwt.jwtSecret, Config.jwt.jwtAudience, Config.jwt.jwtDomain).verify(token)
        val jwtParser = JWTParser()
        val payload = jwtParser.parsePayload(Base64.getDecoder().decode(verify.payload).toString(Charsets.UTF_8))
        val credential = JWTCredential(payload)
        return validate(credential, Config.jwt.jwtAudience)
    } catch (e: JWTVerificationException) {
        return null
    } catch (e: JWTDecodeException) {
        return null
    }
}

data class LoginRequest(
    val userName: String,
    val password: String,
)

data class JWTResponse(
    val token: String,
)