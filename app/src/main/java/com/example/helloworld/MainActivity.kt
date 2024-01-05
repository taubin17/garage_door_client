package com.example.helloworld

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds


class MainActivity : AppCompatActivity() {
    /// Project Constants. Is there better way to do these? Perhaps make a settings menu to config?
    private val HOSTNAME = "73.238.20.72"
    private val PORT: Int = 55420
    private val DOOR_STATE_REQUEST_PERIOD = 0.100

    // Text field to display latest status from server.
    private lateinit var m_status_text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        m_status_text = findViewById(R.id.status_text_view)
    }

    // Override start to create thread to poll the current door state, and display to status text.
    override fun onStart() {
        super.onStart()
        Thread {
            while (true) {
                Thread.sleep(DOOR_STATE_REQUEST_PERIOD.seconds.inWholeMilliseconds)
                runBlocking {
                    val door_state : String? = getCurrentDoorState()
                    door_state ?.let {
                        val status_message = "Door is $door_state".uppercase()
                        runOnUiThread {
                            m_status_text.text = (status_message)
                        }
                    }
                }
            }
        }.start()
    }

    // Thread safe call to get the current door state.
    private fun getCurrentDoorState(): String? {
        val garage_door_client = openConnection()
        garage_door_client?.use {
            return try {
                garage_door_client.send_doorstate_request()
                garage_door_client.on_doorstate_response()
            } catch (e: Exception) {
                m_status_text.text = getString(R.string.connection_lost)
                return null
            }
        }

        return null
    }

    // Thread safe callback to open / close the door.
    // Expected to be bound to UI element (button).
    fun openCloseDoor(@Suppress("UNUSED_PARAMETER") view: View) {
        Thread {
            runBlocking {
                val garage_door_client = openConnection()
                garage_door_client?.use {
                    garage_door_client.send_toggle_door_request()
                }
            }
        }.start()
    }

    // Opens a connection to the garage door.
    private fun openConnection() : GarageDoorClient? {
        return try {
            GarageDoorClient(HOSTNAME, PORT)
        } catch (e: Exception) {
            m_status_text.text = getString(R.string.no_connection)
            return null
        }
    }
}