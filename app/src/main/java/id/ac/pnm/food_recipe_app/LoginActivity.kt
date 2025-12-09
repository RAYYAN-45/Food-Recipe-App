package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextTextEmailAddress: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val editTextTextPassword: EditText = findViewById<EditText>(R.id.editTextTextPassword)
        val buttonLogin: Button = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextTextEmailAddress.text.toString()
            val intentLoginToMain = Intent(this, MainActivity::class.java)

            intentLoginToMain.putExtra("Username", email)
            Toast.makeText(this, "Login Berhasl", Toast.LENGTH_LONG).show()
            startActivity(intentLoginToMain)
        }
    }
}