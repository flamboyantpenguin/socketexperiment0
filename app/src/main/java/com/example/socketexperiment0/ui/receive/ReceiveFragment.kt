package com.example.socketexperiment0.ui.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val receiveViewModel =
            ViewModelProvider(this).get(ReceiveViewModel::class.java)

        _binding = FragmentReceiveBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textDashboard
        receiveViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
         */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}