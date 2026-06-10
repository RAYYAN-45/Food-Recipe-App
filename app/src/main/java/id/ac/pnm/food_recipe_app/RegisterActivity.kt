package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)

        mainLayout?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val auth = FirebaseAuth.getInstance()

        val editTextEmail = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val editTextUsername = findViewById<EditText>(R.id.editTextTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val buttonDaftar = findViewById<Button>(R.id.buttonRegister)
        val buttonKembali = findViewById<Button>(R.id.buttonKembali)

        buttonDaftar.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if(email.isEmpty() || username.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Semua Data Harus Diisi", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful){
                                Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                                val back = Intent(this, LoginActivity::class.java)
                                startActivity(back)
                                finish()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Gagal daftar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        buttonKembali.setOnClickListener {
            val back = Intent(this, LoginActivity::class.java)
            startActivity(back)
        }
    }
}