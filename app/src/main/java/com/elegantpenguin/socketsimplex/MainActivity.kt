package com.elegantpenguin.socketsimplex


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elegantpenguin.socketsimplex.databinding.ActivityMainBinding
import com.elegantpenguin.socketsimplex.ui.send.SendFragment
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
        /*
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_send, R.id.navigation_receive, R.id.navigation_about
            )
        )

         */
        //setupActionBarWithNavController(navController, appBarConfiguration)

        //var rFrg : ReceiveFragment
        val activeFrg = navHostFragment.childFragmentManager.fragments[0] as SendFragment
        val mButton: FloatingActionButton = findViewById(R.id.mainButton)
        mButton.show()

        mButton.setOnClickListener {
            if (activeFrg.isVisible and activeFrg.isAdded) {
                if (activeFrg.isVisible) {
                    activeFrg.startMeow()
                }
            }
            /*
            else {
                rFrg = navHostFragment.childFragmentManager.fragments[1] as ReceiveFragment
            }

             */
        }

        //supportActionBar?.hide()

    }

    override fun onResume() {
        super.onResume()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}

