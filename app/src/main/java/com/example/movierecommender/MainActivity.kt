package com.example.movierecommender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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


            var selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.nav_tickets -> selectedFragment = UserTicketsFragment()
                R.id.nav_logout -> {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> selectedFragment = SearchFragment()
            }

            val bundle = Bundle()
            bundle.putString("username", username)
            selectedFragment!!.arguments = bundle

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit()

            true
        }
}