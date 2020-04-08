package io.subak.udptest

import android.os.StrictMode
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import io.dronefleet.mavlink.Mavlink2Message
import io.dronefleet.mavlink.MavlinkConnection
import io.dronefleet.mavlink.MavlinkMessage
import io.dronefleet.mavlink.common.Attitude
import io.dronefleet.mavlink.common.Heartbeat
import io.subak.udptest.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.math.PI

class AppViewModel : ViewModel(){

    var socket: DatagramSocket
    lateinit var packet: DatagramPacket
    lateinit var address:InetAddress
    private var pipedInputStream: PipedInputStream
    private var pipedOutputStream: PipedOutputStream
    private lateinit var connection: MavlinkConnection
    private lateinit var out: FileOutputStream
    private lateinit var message: MavlinkMessage<Any>
    private lateinit var heartbeat: MavlinkMessage<Heartbeat>
    private lateinit var headingAngle: HeadingAngle

    init {
        pipedOutputStream = PipedOutputStream()
        pipedInputStream = PipedInputStream()
        socket = DatagramSocket()

        pipedOutputStream.connect(pipedInputStream)
//        out = FileOutputStream("")
        Log.i("AppViewModel","ViewModel create!")
        headingAngle = HeadingAngle()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun connectMavlink() {
        GlobalScope.launch {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                address = InetAddress.getByName("192.168.0.115")
                //send
                val buf = ByteArray(256)
                packet = DatagramPacket(buf, buf.size, address, 14580)
                socket.send(packet)

                //get response
                packet = DatagramPacket(buf, buf.size)


                val job = launch {
                    connection = MavlinkConnection.create(
                        pipedInputStream,null
                    )
                    message = connection.next()
                    while (connection.next()!= null){
                        if(message is Mavlink2Message){
                            Log.i("Mavlink22222","Mavlink22222222222")
                        }

                        if(message.payload is Heartbeat) {
                            heartbeat = message as MavlinkMessage<Heartbeat>
                            Log.w("heartbeat","***************하트비트!!!*******")
                        }else {
                            Log.e("message type check : ", message.payload.javaClass.name)
                        }
                        if(message.payload is Attitude ){
                            Log.e("Attitude : ", (message.payload as Attitude).yaw().toString())
                            headingAngle.piToDegree(message)

                           // Log.e("Attitude(변환값)", headingAngle.degree.toString())
                            headingAngle.degreeToPi(headingAngle.degree!!)
                            Log.e("Attitude(변환값)", headingAngle.pi.toString())

                        }
                        message = connection.next()
                    }

                }


                while (true) {
                    socket.receive(packet)

                    //UDP로 받은 packet data를 pipedOutStream.write으로 쓰기
                    pipedOutputStream.write(packet.data)

                    Log.i("TCP Packet recevied","들어옴!!!!")

                    packet.length = buf.size
                }


            }catch (e: Exception){
                e.printStackTrace()
            } finally {
                if(socket != null){
                    socket.close()
                }
            }
        }
    }

    //
}