package com.example.socketexperiment0

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.socketexperiment0.databinding.ActivityMainBinding
import com.example.socketexperiment0.ui.send.SendFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_send, R.id.navigation_receive
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val activeFrg = navHostFragment.childFragmentManager.fragments[0] as SendFragment
        val mButton: FloatingActionButton = findViewById(R.id.mainButton)

        mButton.setOnClickListener() {
            if (activeFrg.isVisible and activeFrg.isAdded) {
                if (activeFrg.isVisible) {
                    activeFrg.startMeow()
                }
            }


        }

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}

