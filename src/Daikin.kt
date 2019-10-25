package fr.lapoumerole

import io.ktor.client.HttpClient
import io.ktor.client.request.get


class Daikin(val adress: String?) {
    var infos = DaikinInfos()
    var controls = DaikinMandatoryControls()
    var sensors = DaikinSensors()

    class DaikinInfos(
        var type: String = "",
        var name: String = "",
        var port: String = "0",
        var mac: String = ""
    )

    class DaikinMandatoryControls(
        var power: String = "0",
        var mode: String = "0",
        var stemp: String = "20",
        var shum: String = "",
        var f_rate: String = "",
        var f_dir: String = "0"
    )

    class DaikinSensors(
        var htemp: String = "20",
        var hhum: String = "",
        var otemp: String =  "10",
        var err: String = "0",
        var cmpfreq: String = "0"
    )

    suspend fun getDeviceInfos(): DaikinInfos{
        val smsClient = HttpClient()
        val uri= "http://${this.adress}/common/basic_info"
        print("getDeviceInfo $uri ---> ")
        val response = smsClient.get<String>(uri)
        println(response.take(10))
        var res = response.split(",").map{it.split("=")}.forEach{
            when (it[0]) {
                "type" -> infos.type = it[1]
                "name" -> infos.name = it[1]
                "port" -> infos.port = it[1]
                "mac" -> infos.mac = it[1]
            }
        }
        smsClient.close()
        return infos
    }

    suspend fun getName(): String {
        var name = ""
        getDeviceInfos().name.substring(1).toUpperCase().split("%").forEach{
            var a =(it.first().toInt()-48)*16+if(it.last().toInt()>59){it.last().toInt()-55}else{it.last().toInt()-48}
            name += a.toChar()
        }
        return name
    }

    suspend fun getControlInfo(): DaikinMandatoryControls{
        val smsClient = HttpClient()
        val uri= "http://${this.adress}/aircon/get_control_info"
        print("getControlInfo $uri ---> ")
        val response = smsClient.get<String>(uri)
        println(response.take(10))
        var res = response.split(",").map{it.split("=")}.forEach{
            when (it[0]) {
                "pow" -> controls.power = it[1]
                "mode" -> controls.mode = it[1]
                "stemp" -> controls.stemp = it[1]
                "shum" -> controls.shum = it[1]
                "f_rate" -> controls.f_rate = it[1]
                "f_dir" -> controls.f_dir = it[1]
            }
        }
        smsClient.close()
        return controls
    }

    suspend fun setControls(ordres: String): String{
        getControlInfo()
        var res = ordres.split(",").map{it.split("=")}.forEach{
            when (it[0]) {
                "P" -> controls.power = it[1]
                "M" -> controls.mode = it[1]
                "S" -> controls.stemp = it[1]
            }
        }
        val newControlsString = controls.run {
            "pow=${this.power}&mode=${this.mode}&stemp=${this.stemp}&shum=${this.shum}"+
                    "&f_rate=${this.f_rate}&f_dir=${this.f_dir}"
        }
        val smsClient = HttpClient()
        val uri= "http://${this.adress}/aircon/set_control_info?$newControlsString"
        print("setControl $uri ---> ")
        val response = smsClient.get<String>(uri).take(10)
        println(response)
        smsClient.close()
        return response
    }

    suspend fun getSensorInfo(): DaikinSensors{
        val smsClient = HttpClient()
        val uri= "http://${adress}/aircon/get_sensor_info"
        print("getSensorInfo $uri ---> ")
        val response = smsClient.get<String>(uri)
        println(response.take(10))
        var res = response.split(",").slice(1..5).map{it.split("=")}.forEach{
            when (it[0]) {
                "htemp" -> sensors.htemp = it[1]
                "hhum" -> sensors.hhum = it[1]
                "otemp" -> sensors.otemp = it[1]
                "err" -> sensors.err = it[1]
                "cmpfreq" -> sensors.cmpfreq = it[1]
            }
        }

        smsClient.close()
        return sensors
    }
}