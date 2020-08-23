package com.example.movierecommender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mRegUsername: TextInputLayout
    lateinit var mRegPassword: TextInputLayout
    lateinit var mRegEmail: TextInputLayout
    lateinit var mRegName: TextInputLayout

    lateinit var mErrorTextView: TextView

    lateinit var mRegButton: Button
    lateinit var mLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.hide()

        mRegEmail = findViewById(R.id.reg_email_edit_text)
        mRegName = findViewById(R.id.reg_name_edit_text)
        mRegUsername = findViewById(R.id.reg_username_edit_text)
        mRegPassword = findViewById(R.id.reg_password_edit_text)

        mErrorTextView = findViewById(R.id.error_message_text_view)

        mLoginButton = findViewById(R.id.goto_login_button)
        mLoginButton.setOnClickListener {
            gotoLoginActivity()
        }

        mRegButton = findViewById(R.id.register_button)
        mRegButton.setOnClickListener {
            if (fieldsEmpty()) {
                mErrorTextView.text = "Fill all fields"
            }
            else {
                isUserAlreadyExists()
            }
        }
    }

    private fun isUserAlreadyExists() {
        val reference = FirebaseDatabase.getInstance().reference.child("users").child(mRegUsername.editText!!.text.toString())
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    mErrorTextView.text = "An user with that username already exists"
                } else {
                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val reference = firebaseDatabase.getReference("users")
                    val user = getRegistrationInformation()
                    reference.child(user.username).setValue(user).addOnCompleteListener {
                        Toast.makeText(applicationContext, "Account Created Successfully!", Toast.LENGTH_LONG).show()
                        gotoLoginActivity()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun gotoLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fieldsEmpty(): Boolean {
        if (mRegEmail.editText!!.text.isEmpty() ||
                mRegName.editText!!.text.isEmpty() ||
                mRegPassword.editText!!.text.isEmpty() ||
                mRegUsername.editText!!.text.isEmpty()) {
            return true
        }
        return false
    }

    private fun getRegistrationInformation(): UserInfo {
        return UserInfo(
            mRegUsername.editText!!.text.toString(),
            mRegName.editText!!.text.toString(),
            mRegEmail.editText!!.text.toString(),
            mRegPassword.editText!!.text.toString()
        )
    }


}