package com.example.socketexperiment0.ui.send

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socketexperiment0.ui.About
import com.example.socketexperiment0.databinding.FragmentSendBinding
import com.google.android.material.textfield.TextInputLayout
import java.net.InetSocketAddress
import java.net.Socket


class SendFragment : Fragment() {

    private var _binding: FragmentSendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val sendViewModel = ViewModelProvider(this)[SendViewModel::class.java]

        _binding = FragmentSendBinding.inflate(inflater, container, false)

        val logo : ImageView = binding.sendLogo
        logo.setOnClickListener {
            val i = Intent(binding.root.context, About::class.java)
            startActivity(i)
        }

        return binding.root
    }

    fun startMeow() {

        val editHostTextView : TextInputLayout = binding.editSendHost
        val editPortTextView : TextInputLayout = binding.editSendPort
        val editMessageTextView : TextInputLayout = binding.editMessage
        val returnValue = sendData(editHostTextView.editText?.text.toString(), editPortTextView.editText?.text.toString(), editMessageTextView.editText?.text.toString())
        if (returnValue == 0) {
            Toast.makeText(binding.root.context, "Data Sent", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(binding.root.context, "Error in Sending Data", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(binding.root.context, "Text Size too Large", Toast.LENGTH_LONG).show()
            return 1
        }

        if (host.isEmpty() && port.isEmpty() && message.isEmpty()) {
            Toast.makeText(binding.root.context, "Fill all fields to continue", Toast.LENGTH_SHORT).show()
            return 1
        }

        Toast.makeText(binding.root.context, "Sending Data to $host:$port", Toast.LENGTH_SHORT).show()
        thread.start()
        Toast.makeText(binding.root.context, receivedData, Toast.LENGTH_SHORT).show()
        return 0
    }
}