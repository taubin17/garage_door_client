package com.example.helloworld

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun main()
{
    val client = Socket("10.0.0.125", 55420)
    val client_istream = BufferedReader(InputStreamReader(client.getInputStream(), "UTF-8"))
    val client_ostream = PrintWriter(client.getOutputStream(), true)

    client_ostream.println("Open Door")
    println("echo: " + client_istream.readLine())

    client.close()
}