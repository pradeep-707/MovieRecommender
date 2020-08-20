package com.example.movierecommender

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
            SearchFragment()).commit()
    }

    private val bottomNavListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            val selectedFragment: Fragment = when (it.itemId) {
                //R.id.nav_alarm -> AlarmFragment()
                //R.id.nav_stopwatch -> StopwatchFragment()
                else -> SearchFragment()
            }

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit()

            true
        }
}