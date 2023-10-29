package com.example.helloworld

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main()
{
    val server = ServerSocket(55420)

    while (true) {
        val client = server.accept()
        val client_istream = BufferedReader(InputStreamReader(client.getInputStream()))
        val client_ostream = PrintWriter(client.getOutputStream(), true)

        client_ostream.println(client_istream.readLine())

        client_istream.close()
        client_ostream.close()
        client.close()
    }
}