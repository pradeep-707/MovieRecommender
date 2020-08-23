package com.example.movierecommender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    lateinit var mLogUsername: TextInputLayout
    lateinit var mLogPassword: TextInputLayout
    lateinit var mLogButton: Button
    lateinit var mCreateAccountButton: Button

    lateinit var mErrorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        mLogUsername = findViewById(R.id.username)
        mLogPassword = findViewById(R.id.password)
        mErrorText = findViewById(R.id.error_message_text_view)

        mCreateAccountButton = findViewById(R.id.create_account_button)
        mCreateAccountButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mLogButton = findViewById(R.id.login_button)
        mLogButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val username = mLogUsername.editText!!.text.toString().trim()
        val password = mLogPassword.editText!!.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users").child(username)



        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mErrorText.text = "user does not exist"
                    return
                }
                //val passwordFromDB = dataSnapshot.child("password").value as String
                val userInfo = dataSnapshot.getValue(UserInfo::class.java)
                if (userInfo!!.password == password) {
                    //mErrorText.text = "correct password"
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("username", userInfo.username)
                    startActivity(intent)
                    finish()
                } else {
                    mErrorText.text = "wrong password"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}