package com.example.socketexperiment0.ui.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.socketexperiment0.databinding.FragmentReceiveBinding

class ReceiveFragment : Fragment() {

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
         


        return binding.root
    }

    public fun startMeow() {

    }

    /*
    private fun receiveData() {
        val url = "https://chinmayavidyalaya.zohodesk.in/portal"
        //val intent = CustomTabsIntent.Builder().build()
        //intent.launchUrl(this, Uri.parse(url))
    }

     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}