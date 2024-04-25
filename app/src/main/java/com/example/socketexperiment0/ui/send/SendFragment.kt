package com.example.socketexperiment0.ui.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socketexperiment0.R
import com.example.socketexperiment0.connectionHandler.TCPClient
import com.example.socketexperiment0.databinding.FragmentSendBinding
import com.example.socketexperiment0.ui.CustomAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Stack


class SendFragment : Fragment() {

    var history = Stack<ArrayList<String>>()

    private var arrayAdapter = CustomAdapter(history)

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


        val historyListView: RecyclerView = binding.historyView
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        historyListView.setLayoutManager(layoutManager)
        historyListView.adapter = this.arrayAdapter


        val refreshButton = binding.refreshButton

        refreshButton.setOnClickListener() {
            val tmp = this.history.count()
            this.history.clear()
            this.updateAdapter(tmp)
            Toast.makeText(binding.root.context, "History Cleared", Toast.LENGTH_SHORT).show()
        }


        //activity?.actionBar?.show()
        activity?.findViewById<FloatingActionButton?>(R.id.mainButton)?.show()



        return binding.root
    }

    fun updateAdapter(index : Int) {
        if (this.history.isNotEmpty()) {
            activity?.runOnUiThread {
                this.arrayAdapter.notifyItemInserted(index)
            }
        }
        else {
            activity?.runOnUiThread { this.arrayAdapter.notifyItemRangeRemoved(0, index) }
        }
    }

    fun startMeow() {

        val editHostTextView: TextInputLayout = binding.editSendHost
        val editPortTextView: TextInputLayout = binding.editSendPort
        val editMessageTextView: TextInputLayout = binding.editMessage


        val returnValue = sendData(
            editHostTextView.editText?.text.toString(),
            editPortTextView.editText?.text.toString(),
            editMessageTextView.editText?.text.toString()
        )
        if (returnValue == 0) {
            Toast.makeText(binding.root.context, "Data Sent", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(binding.root.context, "Error in Sending Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendData(host: String, port: String, message: String): Int {

        if (message.length > 1024) {
            Toast.makeText(binding.root.context, "Text Size too Large", Toast.LENGTH_LONG).show()
            return 1
        }

        if (host.isEmpty() || port.isEmpty() || message.isEmpty()) {
            Toast.makeText(binding.root.context, "Fill all fields to continue", Toast.LENGTH_SHORT)
                .show()
            return 1
        }

        val thread = TCPClient(this, host, port, message)
        Toast.makeText(binding.root.context, "Sending Data to $host:$port", Toast.LENGTH_SHORT).show()
        thread.start()

        thread.join()

        return thread.status
    }

}