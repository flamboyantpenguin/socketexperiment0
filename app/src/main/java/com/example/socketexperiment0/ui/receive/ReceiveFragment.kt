package com.example.socketexperiment0.ui.receive

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socketexperiment0.R
import com.example.socketexperiment0.databinding.FragmentReceiveBinding
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.LinkedList
import java.util.Stack


class CustomAdapter(private val dataSet: Stack<java.util.ArrayList<String>>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstLine: TextView
        val secondLine: TextView

        init {
            // Define click listener for the ViewHolder's View
            firstLine = view.findViewById(R.id.first_line)
            secondLine = view.findViewById(R.id.second_line)
            //textView = view.findViewById(this.meow.)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_message, viewGroup, false)
        //val view = LayoutInflater.from(viewGroup.context).inflate(this.meow.messageList, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.firstLine.text = dataSet[position][0]
        viewHolder.secondLine.text = dataSet[position][1]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

class ReceiveFragment : Fragment() {

    var killswitch = false
    //var msgLst = arrayOf<String>()
    var tempLst = LinkedList<String>()
    //var msgLst = ArrayList<ArrayList<String>>()
    var msgLst = Stack<ArrayList<String>>()
    private val arrayAdapter = CustomAdapter(this.msgLst)



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
            this.killswitch = !listenToggle.isChecked
        }

        val recyclerView: RecyclerView = binding.msgLstView
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        //val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        //val recyclerView: RecyclerView = binding.root.findViewById(R.id.msgLstView)
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.adapter = this.arrayAdapter
        val tmp = ArrayList<String>()
        tmp.plusAssign("No Data")
        tmp.plusAssign("No Data")
        msgLst.push(tmp)
        this.arrayAdapter.notifyItemInserted(0)

        return binding.root
    }


    class TcpClientHandler(private val temp : ReceiveFragment, private val returnText : String, private val dataInputStream: DataInputStream, private val dataOutputStream: DataOutputStream, private val cSocket: Socket) : Thread() {
        override fun run() {
            if (!(temp.killswitch)) {
                try {
                    /*
                    if(dataInputStream.available() > 0){
                        Log.i("Server class", "Received: " + dataInputStream.readUTF())
                        dataOutputStream.writeUTF("Hello Client")
                        sleep(2000L)
                    }

                     */
                    val msg = dataInputStream.reader().readText()
                    Log.i("Server", "Received: $msg")
                    val tmp = ArrayList<String>()
                    tmp.plusAssign(msg)
                    cSocket.inetAddress.hostAddress?.let { tmp.plusAssign(it.toString()) }
                    temp.msgLst.push(tmp)
                    temp.tempLst.add(temp.msgLst.indexOf(tmp).toString())
                    Log.i("Server", "TMPLST: ${temp.tempLst.size}")
                    dataOutputStream.writeUTF("Hello Client")
                    sleep(2000L)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.i("Server", "Error: $e")
                    try {
                        dataInputStream.close()
                        dataOutputStream.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    try {
                        dataInputStream.close()
                        dataOutputStream.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
                dataInputStream.close()
                dataOutputStream.close()
                cSocket.close()
            }
            else {
                Toast.makeText(temp.binding.root.context, "Meow", Toast.LENGTH_SHORT).show()
            }

        }

        companion object {
            private val TAG = TcpClientHandler::class.java.simpleName
        }

    }


    private fun startMeow() {

        this.killswitch = !(this.binding.listenToggleSwitch.isChecked)
        var srSocket : ServerSocket
        var clSocket : Socket? = null

        val status : TextInputLayout = this.binding.ReceiveStatus

        //val hostname = binding.editReceiveHost.editText?.text.toString()
        val port = binding.editReceivePort.editText?.text.toString()
        val returnText = binding.editReceiveReturn.editText?.text.toString()

        val startListen = Thread {
            try {
                Log.i("Server", "Meow")
                srSocket = ServerSocket(port.toInt())
                while (!(this.killswitch)) {
                    clSocket = srSocket.accept()
                    Log.i("Server", "New client: $clSocket")
                    val dataInputStream = DataInputStream(clSocket?.getInputStream())
                    val dataOutputStream = DataOutputStream(clSocket?.getOutputStream())

                    // Use threads for each client to communicate with them simultaneously
                    val t: Thread = TcpClientHandler(this, returnText, dataInputStream, dataOutputStream, clSocket?: Socket())
                    t.start()
                }
                srSocket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    clSocket?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }

        val checkUpdate = Thread() {
            while (!(killswitch)) {
                if (this.tempLst.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        Log.i("Server", "MSGLIST: ${msgLst.size}")
                        val index= tempLst.poll()
                        if (index != null) {
                            this.arrayAdapter.notifyItemInserted(index.toInt())
                        }
                        //this.arrayAdapter.notifyDataSetChanged()
                    })
                }
            }
        }

        if (!(killswitch)) {
            Log.i("Server", "Mew")
            msgLst.clear()
            this.arrayAdapter.notifyItemRemoved(0)
            startListen.start()
            checkUpdate.start()
            Toast.makeText(binding.root.context, "Started Listening", Toast.LENGTH_SHORT).show()
            Log.i("Server", "Started Listening")
        }
        else {
            startListen.join()
            checkUpdate.join()
            Toast.makeText(binding.root.context, "Stopped Listening", Toast.LENGTH_SHORT).show()
            val tmpSize = msgLst.size
            this.msgLst.clear()
            this.tempLst.clear()
            try {
                this.arrayAdapter.notifyItemRangeRemoved(0, tmpSize)
                val tmp = ArrayList<String>()
                tmp.plusAssign("No Data")
                tmp.plusAssign("No Data")
                this.msgLst.push(tmp)
                this.arrayAdapter.notifyItemInserted(0)
            } catch (e : Exception) {
                Log.i("Server", "Error $e")
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}