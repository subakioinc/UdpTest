package io.subak.udptest

import android.util.Log
import io.dronefleet.mavlink.MavlinkMessage
import io.dronefleet.mavlink.common.Attitude
import kotlin.math.PI

class HeadingAngle {

    var degree: Int? = null
    var pi:Float? = null

    fun piToDegree(message:MavlinkMessage<Any>){
        degree = ((message.payload as Attitude).yaw() * (180/ PI)).toInt()

        if(degree!!<0){
            degree = degree!!+360
            Log.e("Attitude(변환값)", degree.toString())
        }
    }
    fun degreeToPi(angle:Int){
        pi = if(angle>180){
            var getAngle = angle-360
            (getAngle*(PI/180)).toFloat()
        }else {
            angle * (PI/180).toFloat()
        }
    }
}