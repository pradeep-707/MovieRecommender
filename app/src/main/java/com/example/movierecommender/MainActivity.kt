package com.example.movierecommender

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.extras!!.getString("username")!!

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener)

        val initialFragment = SearchFragment()
        val bundle = Bundle()
        bundle.putString("username", username)
        initialFragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
            initialFragment).commit()
    }

    private val bottomNavListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {

            val selectedFragment: Fragment = when (it.itemId) {
                R.id.nav_tickets -> UserTicketsFragment()
                else -> SearchFragment()
            }

            val bundle = Bundle()
            bundle.putString("username", username)
            selectedFragment.arguments = bundle

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit()

            true
        }
}