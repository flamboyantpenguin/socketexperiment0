package com.elegantpenguin.socketsimplex.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elegantpenguin.socketsimplex.R
import com.elegantpenguin.socketsimplex.databinding.AboutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AboutFragment : Fragment() {



    private var _binding: AboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //val receiveViewModel = ViewModelProvider(this)[ReceiveViewModel::class.java]



        _binding = AboutBinding.inflate(inflater, container, false)

        activity?.findViewById<FloatingActionButton?>(R.id.mainButton)?.hide()

        binding.followWebsiteButton.setOnClickListener() {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linktr.ee/thelinuxpenguin"))
            startActivity(browserIntent)
        }
        binding.followGitHubButton.setOnClickListener() {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/flamboyantpenguin/socketexperiment0"))
            startActivity(browserIntent)
        }


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}