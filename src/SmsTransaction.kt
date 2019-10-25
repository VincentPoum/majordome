package fr.lapoumerole

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.lang.Exception

class SmsTransaction(private val from: String?, private val message: String?) {

    fun help(): String {
        return ("ret Help")
    }

    suspend fun formatSms(user: Int, texte: String) {
        var ess = ""
        texte.split("|").forEach {
            it.split(" - ").forEach {
                ess += it.take(4)
            }
        }
        sendSms(users[user].tel, ess)
    }

    suspend fun sendSms(toNum: String, content: String) {
        val smsClient = HttpClient()
        val smsServer = "http://192.168.0.151:5554"
        val uri = "$smsServer/SendSMS/user=&password=123456&phoneNumber=$toNum&msg=$content"
        print("Envoi sms $uri ----> ")
        val response = smsClient.get<String>(uri)
        println("$response <----")
        smsClient.close()
    }

    suspend fun parse(): String {
        from?.let {
            val num = from.replace("+33", "0")
            if (num.matches(Regex("^(06|07)[0-9]{8}$"))) {
                val id = users.filter { it.tel == num }[0]
                id?.let {
                    message?.let {
                        val res = message.split("<")
                        if (res.size == 1) {
                            sendSms(num, "Message erroné")
                            return ("Pb de message, message erroné.")
                        }
                        if (id.num == res[0].toInt()) {
                            var retour = ""
                            res[1].split("|").forEach {
                                retour += Instruction(it).process()
                            }
                            formatSms(id.num, retour)
                            return("Message traité.")
                        }
                    }
                }
                sendSms(num,"Ne vous êtes-vous pas trompé de numéro ?")
                return("Utilisateur inconnu")
            }
            return("Mauvais numéro.")
        }
        return ("Erreur")
    }

}
