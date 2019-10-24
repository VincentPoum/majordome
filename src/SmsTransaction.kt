package fr.lapoumerole

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.lang.Exception

class SmsTransaction(private val from: String?, private val message: String?) {

    fun help(): String {
        return ("ret Help")
    }

    suspend fun formatSms(user: Int, texte: String){
        var ess = ""
        texte.split("|").forEach{
            it.split(" - ").forEach{
                ess += it.take(4)
            }
        }
        sendSms(user, ess)
    }

    suspend fun sendSms(user: Int, content: String) {
        val smsClient = HttpClient()
        val smsServer = "http://192.168.0.151:5554"
        val toNum = users[user].tel
        val uri = "$smsServer/SendSMS/user=&password=123456&phoneNumber=$toNum&msg=$content"
        print("Envoi sms $uri ----> ")
        val response = smsClient.get<String>(uri)
        println("$response <----")
        smsClient.close()
    }

    suspend fun parse(): String {
        // Structure du SMS
        // User (0..99) < Ordre séparé par |
        // Chaque ordre : appareil concerné - action - consigne
        message?.let {
            // verif emetteur
            val res = message.split("<")
            val numUser = res[0].toInt()
            try {
                val resUser = users.binarySearch { numCompare(it, numUser) }
                if (from?.takeLast(8) == users[resUser].tel.takeLast(8)) {
                    var retour = ""
                    val ordres = res[1].split("|").forEach{
                        retour += Instruction(it).process()
                    }
                    formatSms(resUser, retour)
                }
            } catch (e: Exception){
                return("Utilisateur inconnu")
            }

        }
        return ("receive sms ${this.message} from ${this.from}")
    }
}