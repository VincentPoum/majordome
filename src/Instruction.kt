package fr.lapoumerole

class Instruction(private val instruct: String) {

    suspend fun process(): String {
        print("$instruct - ")
        var retour =""
        val ordre = instruct.split("-")
        when (ordre[0]) {
            "status" -> {
                val leDevice1 = Daikin("192.168.0.161")
                var leD1 = ""
                leDevice1.getDeviceInfos().name.substring(1).toUpperCase().split("%").forEach{
                    var a =(it.first().toInt()-48)*16+if(it.last().toInt()>59){it.last().toInt()-55}else{it.last().toInt()-48}
                    leD1 += a.toChar()
                }
                val leDevice2 = Daikin("192.168.0.162")
                var leD2 = ""
                leDevice2.getDeviceInfos().name.substring(1).toUpperCase().split("%").forEach{
                    var a =(it.first().toInt()-48)*16+if(it.last().toInt()>59){it.last().toInt()-55}else{it.last().toInt()-48}
                    leD2 += a.toChar()
                }
                with(leDevice1.getSensorInfo()) {
                    retour = "Temp - Ext - ${this.otemp.toString()}|"
                    retour += "${leD1} - ${this.htemp.toString()}|"
                }
                with(leDevice2.getSensorInfo()){
                retour += "${leD2} - ${this.htemp.toString()}|"
                }
                with( leDevice1.getControlInfo()) {
                    retour += "${leD1} - Mode - ${this.mode.toString()}|"
                    retour += "Power - ${this.power.toString()}|"
                    retour += "Consigne - ${this.stemp.toString()}|"
                }
                with( leDevice2.getControlInfo()) {
                    retour += "${leD2} - Mode - ${this.mode.toString()}|"
                    retour += "Power - ${this.power.toString()}|"
                    retour += "Consigne - ${this.stemp.toString()}|"
                }
                println(retour)
            }
            else -> {
                println("ordre inconnu")
                retour = "xxx"
            }
        }
        return (retour)
    }
}