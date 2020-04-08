package io.subak.udptest

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.dronefleet.mavlink.Mavlink2Message
import io.dronefleet.mavlink.MavlinkConnection
import io.dronefleet.mavlink.MavlinkMessage
import io.dronefleet.mavlink.common.Heartbeat
import io.subak.udptest.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.lang.Exception
import java.net.DatagramPacket
import java.net.InetAddress

import java.net.DatagramSocket


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)


        binding.connect.setOnClickListener {
            runBlocking {
                viewModel.connectMavlink()
            }
        }

    }

}


