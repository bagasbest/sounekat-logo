package com.sounekatlogo.sounekatlogo.ui.photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekatlogo.databinding.ActivityPhotoExpandBinding

class PhotoExpandActivity : AppCompatActivity() {

    private var _binding : ActivityPhotoExpandBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhotoExpandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photo = intent.getStringExtra(EXTRA_PHOTO)
        Glide.with(this)
            .load(photo)
            .into(binding.image)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_PHOTO = "photo"
    }
}