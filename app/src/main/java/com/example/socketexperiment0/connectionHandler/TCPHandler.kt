package com.example.socketexperiment0.connectionHandler

import android.util.Log
import com.example.socketexperiment0.ui.receive.ReceiveFragment
import com.example.socketexperiment0.ui.send.SendFragment
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.Calendar


class TCPListener(private val recFrg : ReceiveFragment, private val port: Int) : Thread() {
    override fun run() {
        var clSocket: Socket
        val srSocket = ServerSocket(port)
        recFrg.socketTracker.add(srSocket)
        while (!(recFrg.killSwitch)) {
            try {
                clSocket = srSocket.accept()
                Log.i("Server", "New client: $clSocket")
                val dataInputStream = DataInputStream(clSocket?.getInputStream())
                val dataOutputStream = DataOutputStream(clSocket?.getOutputStream())
                // Use threads for each client to communicate with them simultaneously
                val t: Thread = TcpClientHandler(
                    recFrg,
                    recFrg.returnText,
                    dataInputStream,
                    dataOutputStream,
                    clSocket ?: Socket()
                )
                t.start()
            }
            catch (e: SocketException) {
                Log.d("Server", "Socket Closed ($e)")
            }
        }
        Log.i("Server", "Closing Socket")
        srSocket.close()
    }
}


class TcpClientHandler(private val activeFrg : ReceiveFragment, private var returnText : String, private val dataInputStream: DataInputStream, private val dataOutputStream: DataOutputStream, private val cSocket: Socket) : Thread() {
    override fun run() {
        if (!(activeFrg.killSwitch)) {
            try {
                cSocket.setSoLinger(true, 1)
                val msg = BufferedReader(dataInputStream.reader()).readLine()
                Log.i("Server", "Received Message: $msg")
                val tmp = ArrayList<String>(listOf(msg))
                cSocket.inetAddress.hostAddress?.let { tmp.plusAssign(it) }
                val cal = Calendar.getInstance()
                tmp.plusAssign("${cal.get(11)}:"+"${cal.get(12)}:"+"${cal.get(13)}")
                activeFrg.msgLst.push(tmp)
                activeFrg.tempLst.add(activeFrg.msgLst.indexOf(tmp).toString())
                activeFrg.updateAdapter()
                dataOutputStream.writeUTF(returnText)
                cSocket.close()
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
                Log.i("Server", "Error: $e")
                e.printStackTrace()
                try {
                    dataInputStream.close()
                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }

    }

    companion object {
    }

}


class TCPClient(private val sendFrg : SendFragment, private val host: String, private val port: String, private var message: String) : Thread() {

    var status = 0
    override fun run() {
        try {
            val clSocket = Socket()
            clSocket.setSoLinger(true, 1)

            clSocket.connect(InetSocketAddress(host, port.toInt()), 500)
            DataOutputStream(clSocket.getOutputStream()).writeUTF(message)
            val receivedData = BufferedReader(DataInputStream(clSocket.getInputStream()).reader()).readLine()

            clSocket.close()
            val cal = Calendar.getInstance()
            sendFrg.history.push(ArrayList(listOf(clSocket.inetAddress.hostAddress, receivedData, "${cal.get(11)}:"+"${cal.get(12)}:"+"${cal.get(13)}")))
            sendFrg.updateAdapter(sendFrg.history.count())
            Log.i("Client", "Received: $receivedData")
        } catch (e: Exception) {
            Log.d("Client Error", e.toString())
            this.status = 1
        }
    }

    companion object {
    }

}