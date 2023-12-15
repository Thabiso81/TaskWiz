package com.thabiso81.taskwiz.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.thabiso81.taskwiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val _username = intent.getStringExtra("username")
        val _password = intent.getStringExtra("password")
        val userHasRegistered = intent.getBooleanExtra("hasRegistered", false)

        if(userHasRegistered) {
            binding.edtEmail.setText(_username.toString())
            binding.edtPassword.setText(_password.toString())
        }
        //navigate to register activity
        binding.tvSignUp.setOnClickListener{
            val register = Intent(this, RegisterActivity::class.java)
            startActivity(register)
        }
        //Sign the user into the app
        binding.btnLogin.setOnClickListener(){
            /******************** for testing purposes *********************
            if (inputValid(binding.edtEmail, binding.edtPassword)){
            val email: String = binding.edtEmail.text.toString().trim(){it <= ' '}
            val password: String = binding.edtPassword.text.toString().trim(){it <= ' '}

            auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithEmail:success")
            val user = auth.currentUser
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainActivity)
            finish()
            } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.exception)
            Toast.makeText(
            baseContext,
            "Username or password is incorrect",
            Toast.LENGTH_SHORT,
            ).show()

            }
            }

            }
             ******************** for testing purposes *********************/
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mainActivity)
            finish()

        }
    }

    private fun inputValid(email: EditText, password: EditText): Boolean{
        var isValid = true
        if (email.text.toString().isEmpty()){
            isValid = false
            email.setError("Required")
        }
        if (password.text.toString().isEmpty()){
            isValid = false
            password.setError("Required")
        }



        return isValid
    }
}