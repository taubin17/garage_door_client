package com.example.helloworld

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.TextView
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Scanner

class GarageDoorClient {

    private var client_socket_interface : Socket = Socket()
    private lateinit var client_socket_writer : PrintWriter
    private lateinit var client_socket_reader : BufferedReader
    private val client_socket_mutex : Mutex = Mutex()

    lateinit var current_door_state : String
//
//    override fun onBind(p0: Intent?): IBinder? {
//        TODO("Not yet implemented")
//        return null
//    }

    private fun InitializeConnection(host : String, port : Int) : Boolean
    {
        /// Initialize client --> server connection.
        try {
            client_socket_interface = Socket(host, port)
        } catch (e : ConnectException) {
            println("Couldn't connect using host: $host")
            return false
        }

        if (client_socket_interface.isConnected && !client_socket_interface.isClosed) {
            println("Socket successfully connected to server.")
            client_socket_writer = PrintWriter(client_socket_interface.getOutputStream(), true)
            client_socket_reader = BufferedReader(InputStreamReader(client_socket_interface.getInputStream()))
        }
        else {
            println("Socket failed to connect to server.")
            return false
        }

        return true
    }

    /// @brief Frees Resources created by InitializeConnection(host, port)
    private fun CloseConnection()
    {
        println("Closing Reader, Writer, and Socket")
        client_socket_reader.close()
        client_socket_writer.close()
        client_socket_interface.close()
    }

    private fun SendDoorToggleRequest() {
        println("Sending Door Toggle Request")
        client_socket_writer.println("Toggle Door")
    }

    private fun SendDoorStateRequest() {
        println("Sending request for current door state")
        client_socket_writer.println("Doorstate?")
    }

    private fun ReceiveDoorStateResponse(): String {
        val response: String = client_socket_reader.readLine()
        println("Door is: $response")
        return response
    }

    fun OpenCloseDoor(host : String, port : Int) {
        println("Opening/Closing Door.")

        synchronized(this) {
            val connected = InitializeConnection(host, port)
            if (!connected) {
                return
            }

            /// Now we need to send a door toggle request
            SendDoorToggleRequest()

            /// Ideally read the response to see if it worked.
            current_door_state = ReceiveDoorStateResponse()

            CloseConnection()
        }
    }

    fun GetDoorState(host: String, port: Int)
    {
        println("Getting Door State")

        synchronized(this) {
            val connected = InitializeConnection(host, port)
            if (!connected) {
                return
            }

            /// Send a request to get current door state
            SendDoorStateRequest()

            current_door_state = ReceiveDoorStateResponse()

            CloseConnection()
        }
    }
}