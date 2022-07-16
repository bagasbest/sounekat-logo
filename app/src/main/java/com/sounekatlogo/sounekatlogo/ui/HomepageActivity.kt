package com.sounekatlogo.sounekatlogo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.sounekatlogo.sounekatlogo.R
import com.sounekatlogo.sounekatlogo.databinding.ActivityHomepageBinding
import com.sounekatlogo.sounekatlogo.ui.photo.PhotoAddEditActivity

class HomepageActivity : AppCompatActivity() {

    private var _binding : ActivityHomepageBinding? = null
    private val binding get() = _binding!!
    private var isAdmin = false

    override fun onResume() {
        super.onResume()
        checkIsAdminOrNot()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener{
            if(!isAdmin) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                showLogoutDialog()
            }
        }

        binding.fabAddPhotoBtn.setOnClickListener {
            val intent = Intent(this, PhotoAddEditActivity::class.java)
            intent.putExtra(PhotoAddEditActivity.OPTION, "add")
            startActivity(intent)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout Admin")
            .setMessage("Apakah anda yakin ingin logout dari Admin ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                FirebaseAuth.getInstance().signOut()
                isAdmin = false
                Toast.makeText(this, "Berhasil Logout!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun checkIsAdminOrNot() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.fabAddPhotoBtn.visibility = View.VISIBLE
            isAdmin = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}