package com.example.socketexperiment0

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.socketexperiment0.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.textfield.TextInputLayout
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set ofIds because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_send, R.id.navigation_receive
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()

        val editHostTextView : TextInputLayout = findViewById(R.id.editSendHost)
        val editPortTextView : TextInputLayout = findViewById(R.id.editSendPort)
        val editMessageTextView : TextInputLayout = findViewById(R.id.editMessage)
        val sendDataButton : Button = findViewById(R.id.sendButton)
        val logo : ImageView = findViewById(R.id.send_logo)
        sendDataButton.setOnClickListener{
            //val text: String = textInputLayout.getEditText().getText()
            val returnValue = sendData(editHostTextView.editText?.text.toString(), editPortTextView.editText?.text.toString(), editMessageTextView.editText?.text.toString())
            if (returnValue == 0) {
                Toast.makeText(this, "Data Sent", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Error in Sending Data", Toast.LENGTH_SHORT).show()
            }
        }
        logo.setOnClickListener {
            val i = Intent(this@MainActivity, About::class.java)
            startActivity(i)
        }
    }

    private fun sendData(host : String, port: String, message :String) : Int {

        var receivedData = ""

        val thread = Thread {
            try {
                val clSocket = Socket()
                clSocket.connect(InetSocketAddress(host, port.toInt()), 500)
                val inputStream = clSocket.getInputStream()
                val outputStream = clSocket.getOutputStream()
                outputStream.write(message.toByteArray())
                receivedData = inputStream.reader().readText()
                outputStream.close()
                inputStream.close()
                clSocket.close()
            } catch (e: Exception) {
                Log.d(null, e.toString())
            }
        }

        if (message.length > 1024) {
            Toast.makeText(this, "Text Size too Large", Toast.LENGTH_LONG).show()
            return 1
        }

        if (host.isEmpty() && port.isEmpty() && message.isEmpty()) {
            Toast.makeText(this, "Fill all fields to continue", Toast.LENGTH_SHORT).show()
            return 1
        }

        Toast.makeText(this, "Sending Data to $host:$port", Toast.LENGTH_SHORT).show()
        thread.start()
        Toast.makeText(this, receivedData, Toast.LENGTH_SHORT).show()
        return 0
    }



    /*
    private fun receiveData() {
        val url = "https://chinmayavidyalaya.zohodesk.in/portal"
        //val intent = CustomTabsIntent.Builder().build()
        //intent.launchUrl(this, Uri.parse(url))
    }

     */
}