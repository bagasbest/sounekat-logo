package com.sounekatlogo.sounekatlogo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sounekatlogo.sounekatlogo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}