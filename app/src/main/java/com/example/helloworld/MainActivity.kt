package com.example.helloworld

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {
    private lateinit var garage_door_client: GarageDoorClient
    private lateinit var status_text: TextView

    /// Project Constants.
//    private val WIFI_HOST = "10.0.0.125"
    private val CELL_HOST = "73.238.20.72"
    private val PORT: Int = 55420

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add this:
        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        try {
            Class.forName("dalvik.system.CloseGuard")
                .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
                .invoke(null, true)
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
        setContentView(R.layout.activity_main)

        status_text = findViewById(R.id.status_text_view)
        garage_door_client = GarageDoorClient()
    }

    override fun onStart() {
        super.onStart()

        Thread {
            while (true) {
                garage_door_client.GetDoorState(CELL_HOST, PORT)
                val status_message = "Door is ${garage_door_client.current_door_state}".uppercase()
                runOnUiThread() {
                    status_text.text = (status_message)
                }
                Thread.sleep(1000L)
            }
        }.start()
    }

    fun socketTest(view : View) {
//        Thread {(garage_door_client.openCloseDoor(WIFI_HOST, PORT))}.start()
        Thread {
            garage_door_client.OpenCloseDoor(CELL_HOST, PORT)
            val status_message = "Door is ${garage_door_client.current_door_state}".uppercase()
            runOnUiThread() {
                status_text.text = (status_message)
            }
        }.start()

    }
}