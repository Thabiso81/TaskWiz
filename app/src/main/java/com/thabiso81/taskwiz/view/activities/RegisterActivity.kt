package com.thabiso81.taskwiz.view.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.thabiso81.taskwiz.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        //register the user
        btnRegisterSetOnclickListener()

        //take user back to Login
        tvSignInSetOnlickListener()



    }

    private fun btnRegisterSetOnclickListener() {
        binding.btnRegister.setOnClickListener(){
            if (inputValid(binding.edtPersonEmail, binding.edtPassword, binding.edtConfirmPassword)){

                val email: String = binding.edtPersonEmail.text.toString().trim(){it <= ' '}
                val password: String = binding.edtPassword.text.toString().trim(){it <= ' '}
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            val userIsRegistered = true
                            val goToLogin = Intent(this, LoginActivity::class.java)



                            goToLogin.putExtra("username", email)
                            goToLogin.putExtra("password", password)
                            goToLogin.putExtra("hasRegistered", userIsRegistered)
                            startActivity(goToLogin)
                            finish()
                            Toast.makeText(
                                baseContext,
                                "Registration successful",
                                Toast.LENGTH_SHORT,
                            ).show()

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "${task.exception}",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }


            }
        }
    }

    private fun tvSignInSetOnlickListener() {
        binding.tvSignIn.setOnClickListener(){
            val backToLogin = Intent(this, LoginActivity::class.java)

            startActivity(backToLogin)
            finish()
        }
    }

    private fun inputValid(email: EditText, password: EditText, confirmPassword: EditText): Boolean{
        var isValid = true
        if (email.text.toString().isEmpty()){
            isValid = false
            email.error = "Please enter an email"
        }
        if (password.text.toString().isEmpty()){
            isValid = false
            password.error = "Please enter a password"
        }
        if (confirmPassword.text.toString().isEmpty()){
            isValid = false
            confirmPassword.error = "please confirm your password"
        }
        if (confirmPassword.text.toString() != password.text.toString()){
            isValid = false
            confirmPassword.error = "passwords do not match"
        }


        return isValid
    }
}