package fr.lapoumerole

class Instruction(private val instruct: String) {

    suspend fun process(): String {
        print("$instruct - ")
        var retour =""
        val ordre = instruct.split("-")
        when (ordre[0]) {
            "status" -> {
                val leDevice1 = Daikin("192.168.0.161")
                val leD1 = "R${leDevice1.getName().take(6)}"
                val leDevice2  = Daikin("192.168.0.162")
                val leD2 = "R${leDevice2.getName().take(6)}"
                with(leDevice1.getSensorInfo()) {
                    retour = "WT${this.otemp}|${leD1}-T${this.htemp}|"
                }
                with(leDevice2.getSensorInfo()){
                retour += "${leD2}-T${this.htemp}|"
                }
                with( leDevice1.getControlInfo()) {
                    retour += "${leD1}-M${this.mode}|${leD1}-P${this.power}|${leD1}-S${this.stemp}|"
                }
                with( leDevice2.getControlInfo()) {
                    retour += "${leD2}-M${this.mode}|${leD2}-P${this.power}|${leD2}-S${this.stemp}|"
                }
                println(retour)
            }
            "RCui" -> {
                retour = Daikin("192.168.0.161").setControls(ordre[1])
            }
            "RSal" -> {
                retour = Daikin("192.168.0.162").setControls(ordre[1])
            }
            else -> {
                println("ordre inconnu")
                retour = "xxx"
            }
        }
        return (retour)
    }
}