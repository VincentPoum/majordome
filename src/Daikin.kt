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
        var port: Int = 0,
        var mac: String = ""
    )

    class DaikinMandatoryControls(
        var power: Int = 0,
        var mode: Int = 0,
        var stemp: Float = 20f,
        var shum: String = "",
        var f_rate: String = "",
        var f_dir: Int = 0
    )

    class DaikinSensors(
        var htemp: Float = 20f,
        var hhum: String = "",
        var otemp: Float =  10f,
        var err: Int = 0,
        var cmpfreq: Int = 0
    )

    suspend fun getDeviceInfos(): DaikinInfos{
        val smsClient = HttpClient()
        val uri= "http://${this.adress}/common/basic_info"
        println("getDeviceInfo $uri")
        val response = smsClient.get<String>(uri)
        var res = response.split(",").map{it.split("=")}.forEach{
            when (it[0]) {
                "type" -> infos.type = it[1]
                "name" -> infos.name = it[1]
                "port" -> infos.port = it[1].toInt()
                "mac" -> infos.mac = it[1]
            }
        }
        smsClient.close()
        return infos
    }
    suspend fun getControlInfo(): DaikinMandatoryControls{
        val smsClient = HttpClient()
        val uri= "http://${this.adress}/aircon/get_control_info"
        println("getControlInfo $uri")
        val response = smsClient.get<String>(uri)
        var res = response.split(",").map{it.split("=")}.forEach{
            when (it[0]) {
                "pow" -> controls.power = it[1].toInt()
                "mode" -> controls.mode = it[1].toInt()
                "stemp" -> controls.stemp = it[1].toFloat()
                "shum" -> controls.shum = it[1]
                "f_rate" -> controls.f_rate = it[1]
                "f_dir" -> controls.f_dir = it[1].toInt()
            }
        }
        smsClient.close()
        return controls
    }

    suspend fun getSensorInfo(): DaikinSensors{
        val smsClient = HttpClient()
        val uri= "http://${adress}/aircon/get_sensor_info"
        println("getSensorInfo $uri")
        val response = smsClient.get<String>(uri)
        var res = response.split(",").slice(1..5).map{it.split("=")}.forEach{
            when (it[0]) {
                "htemp" -> sensors.htemp = it[1].toFloat()
                "hhum" -> sensors.hhum = it[1]
                "otemp" -> sensors.otemp = it[1].toFloat()
                "err" -> sensors.err = it[1].toInt()
                "cmpfreq" -> sensors.cmpfreq = it[1].toInt()
            }
        }

        smsClient.close()
        return sensors
    }
}