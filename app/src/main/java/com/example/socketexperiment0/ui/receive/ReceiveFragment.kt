package com.example.socketexperiment0.ui.receive

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socketexperiment0.R
import com.example.socketexperiment0.connectionHandler.TcpClientHandler
import com.example.socketexperiment0.databinding.FragmentReceiveBinding
import com.example.socketexperiment0.ui.CustomAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.LinkedList
import java.util.Stack


class ReceiveFragment : Fragment() {

    var killSwitch = false
    var tempLst = LinkedList<String>()
    var msgLst = Stack<ArrayList<String>>()
    private val arrayAdapter = CustomAdapter(this.msgLst)



    private var _binding: FragmentReceiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //val receiveViewModel = ViewModelProvider(this)[ReceiveViewModel::class.java]



        _binding = FragmentReceiveBinding.inflate(inflater, container, false)
         
        val listenToggle : MaterialSwitch = binding.listenToggleSwitch

        listenToggle.setOnClickListener {
            startMeow()
            this.killSwitch = !listenToggle.isChecked
        }

        val recyclerView: RecyclerView = binding.msgLstView

        //val recyclerView: RecyclerView = binding.root.findViewById(R.id.msgLstView)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = this.arrayAdapter

        val progress = binding.progressBar
        progress.visibility = View.INVISIBLE

        activity?.findViewById<FloatingActionButton?>(R.id.mainButton)?.hide()


        return binding.root
    }


    fun updateAdapter() {
        if (this.tempLst.size > 0) {
            activity?.runOnUiThread {
                val index = tempLst.poll()
                if (index != null) {
                    this.arrayAdapter.notifyItemInserted(index.toInt())
                }
            }
        }
    }


    private fun startMeow() : Int {

        var srSocket: ServerSocket
        var clSocket: Socket? = null

        this.killSwitch = !(this.binding.listenToggleSwitch.isChecked)

        val progress = this.binding.progressBar

        //val hostname = binding.editReceiveHost.editText?.text.toString()
        val port = binding.editReceivePort.editText?.text.toString()
        val returnText = binding.editReceiveReturn.editText?.text.toString()

        if (port.isEmpty()) {
            Toast.makeText(binding.root.context, "Fill all fields to continue", Toast.LENGTH_SHORT)
                .show()
            return 1
        }

        val startListen = Thread {
            srSocket = ServerSocket(port.toInt())
            try {
                while (!(this.killSwitch)) {
                    clSocket = srSocket.accept()
                    Log.i("Server", "New client: $clSocket")
                    val dataInputStream = DataInputStream(clSocket?.getInputStream())
                    val dataOutputStream = DataOutputStream(clSocket?.getOutputStream())

                    // Use threads for each client to communicate with them simultaneously
                    val t: Thread = TcpClientHandler(
                        this,
                        returnText,
                        dataInputStream,
                        dataOutputStream,
                        clSocket ?: Socket()
                    )
                    t.start()
                }
                srSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    clSocket?.close()
                    srSocket.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }


        val progressBarUpdate = Thread() {

            var i = 0
            while (i < 100) {
                i += 1
                activity?.runOnUiThread {
                    progress.progress = i
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        if (!(killSwitch)) {
            progress.visibility = View.VISIBLE
            progressBarUpdate.start()
            Log.i("Server", "Mew")
            msgLst.clear()
            this.arrayAdapter.notifyItemRemoved(0)
            startListen.start()
            //checkUpdate.start()
            Toast.makeText(binding.root.context, "Started Listening", Toast.LENGTH_SHORT).show()
            Log.i("Server", "Started Listening")
        } else {
            this.killSwitch = true
            progress.visibility = View.INVISIBLE
            startListen.join()
            //checkUpdate.join()
            progressBarUpdate.interrupt()
            Toast.makeText(binding.root.context, "Stopped Listening", Toast.LENGTH_SHORT).show()
            val tmpSize = msgLst.size
            this.msgLst.clear()
            this.tempLst.clear()
            try {
                this.arrayAdapter.notifyItemRangeRemoved(0, tmpSize)
            } catch (e: Exception) {
                Log.i("Server", "Error $e")
            }
        }

        return 0

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}