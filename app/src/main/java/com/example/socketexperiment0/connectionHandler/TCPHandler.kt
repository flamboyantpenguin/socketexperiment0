package com.example.socketexperiment0.connectionHandler

import android.util.Log
import android.widget.Toast
import com.example.socketexperiment0.ui.receive.ReceiveFragment
import com.example.socketexperiment0.ui.send.SendFragment
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class TcpClientHandler(private val activeFrg : ReceiveFragment, private var returnText : String, private val dataInputStream: DataInputStream, private val dataOutputStream: DataOutputStream, private val cSocket: Socket) : Thread() {
    override fun run() {
        if (!(activeFrg.killSwitch)) {
            try {
                /*
                if(dataInputStream.available() > 0){
                    Log.i("Server class", "Received: " + dataInputStream.readUTF())
                    dataOutputStream.writeUTF("Hello Client")
                    sleep(2000L)
                }

                 */
                cSocket.setSoLinger(true, 1)
                val msg = dataInputStream.reader().readText()
                Log.i("Server", "Received Message: $msg")
                val tmp = ArrayList<String>(listOf(msg))
                cSocket.inetAddress.hostAddress?.let { tmp.plusAssign(it.toString()) }
                tmp.plusAssign("")
                activeFrg.msgLst.push(tmp)
                activeFrg.tempLst.add(activeFrg.msgLst.indexOf(tmp).toString())
                activeFrg.updateAdapter()
                //returnText += "\n"
                dataOutputStream.writeUTF(returnText)
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

    }

    companion object {
        private val TAG = TcpClientHandler::class.java.simpleName
    }

}


class TCPHandler(private val sendFrg : SendFragment, private val host: String, private val port: String, private var message: String) : Thread() {

    var status = 0
    override fun run() {
        try {
            val clSocket = Socket()
            clSocket.setSoLinger(true, 1)
            clSocket.connect(InetSocketAddress(host, port.toInt()), 500)
            val outputStream = clSocket.getOutputStream()
            outputStream.write(message.toByteArray())
            outputStream.flush()
            outputStream.close()
            val receivedData = DataInputStream(clSocket.getInputStream()).reader().readText()
            sendFrg.history.push(ArrayList(listOf(clSocket.inetAddress.hostAddress, receivedData, message)))
            sendFrg.updateAdapter(sendFrg.history.count())
            Log.i("Client", "Received: $receivedData")
            outputStream.close()
            clSocket.shutdownInput()
            clSocket.shutdownOutput()
            clSocket.close()
        } catch (e: Exception) {
            Log.d("Client Error", e.toString())
            this.status = 1
        }
    }

    companion object {
        private val TAG = TcpClientHandler::class.java.simpleName
    }

}