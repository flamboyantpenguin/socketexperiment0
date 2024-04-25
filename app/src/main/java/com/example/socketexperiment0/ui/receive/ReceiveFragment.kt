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
import com.example.socketexperiment0.connectionHandler.TCPListener
import com.example.socketexperiment0.databinding.FragmentReceiveBinding
import com.example.socketexperiment0.ui.CustomAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import java.net.ServerSocket
import java.util.LinkedList
import java.util.Stack


class ReceiveFragment : Fragment() {

    var killSwitch = false
    var tempLst = LinkedList<String>()
    var returnText = ""
    var msgLst = Stack<ArrayList<String>>()
    private val arrayAdapter = CustomAdapter(this.msgLst)
    private var threadTracker  = ArrayList<Thread>()
    var socketTracker  = ArrayList<ServerSocket>()



    private var _binding: FragmentReceiveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        }

        val recyclerView: RecyclerView = binding.msgLstView

        //val recyclerView: RecyclerView = binding.root.findViewById(R.id.msgLstView)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
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


    fun assassin() {
        if (this.killSwitch) {
            for (i in this.threadTracker) {
                Log.d("Server", "Killed ${i.toString()}")
                i.interrupt()
            }
            for (i in this.socketTracker) {
                Log.d("Socket", "Closed ${i.toString()}")
                i.close()
            }
        }
    }


    private fun startMeow() : Int {



        this.killSwitch = !(this.binding.listenToggleSwitch.isChecked)

        val progress = this.binding.progressBar

        val port = binding.editReceivePort.editText?.text.toString()
        this.returnText = binding.editReceiveReturn.editText?.text.toString()

        if (port.isEmpty() or returnText.isEmpty()) {
            Toast.makeText(binding.root.context, "Fill Port and Return Address to continue", Toast.LENGTH_SHORT)
                .show()
            binding.listenToggleSwitch.isChecked = false
            return 1
        }

        if (!(killSwitch)) {
            val t  = TCPListener(this, port.toInt())
            view?.keepScreenOn = true
            progress.visibility = View.VISIBLE
            //progressBarUpdate.start()
            Log.i("Server", "Mew")
            msgLst.clear()
            //this.arrayAdapter.notifyItemRemoved(0)
            t.start()
            this.threadTracker.add(t)
            Toast.makeText(binding.root.context, "Started Listening", Toast.LENGTH_SHORT).show()
            Log.i("Server", "Started Listening")
        } else {
            this.assassin()
            view?.keepScreenOn = false
            //progressBarUpdate.interrupt()
            progress.visibility = View.INVISIBLE
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