package com.example.helloworld


import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class GarageDoorClient (host: String, port: Int) : Closeable {
    private var m_client_socket_interface : Socket = Socket(host, port)

    // Classes used to interface with the socket.
    private var m_client_socket_writer : PrintWriter =
        PrintWriter(m_client_socket_interface.getOutputStream(), true)

    private var m_client_socket_reader : BufferedReader =
        BufferedReader(InputStreamReader(m_client_socket_interface.getInputStream()))

    /// @brief Frees all resources.
    override fun close()
    {
        println("Closing Reader, Writer, and Socket")
        m_client_socket_writer.close()
        m_client_socket_interface.close()
        m_client_socket_reader.close()
    }

    fun send_toggle_door_request() {
        println("Sending Door Toggle Request")
        m_client_socket_writer.println("Toggle Door")
    }

    fun send_doorstate_request() {
//        println("Sending request for current door state")
        m_client_socket_writer.println("Doorstate?")
    }

    fun on_doorstate_response(): String {
        return m_client_socket_reader.readLine()
    }

    fun connected(): Boolean {
        return m_client_socket_interface.isConnected
    }
}