package com.sounekatlogo.sounekatlogo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.sounekatlogo.sounekatlogo.R
import com.sounekatlogo.sounekatlogo.databinding.ActivityHomepageBinding
import com.sounekatlogo.sounekatlogo.ui.photo.*

class HomepageActivity : AppCompatActivity() {

    private var _binding : ActivityHomepageBinding? = null
    private val binding get() = _binding!!
    private var isAdmin = false
    private var horizontalAdapter : PhotoHorizontalAdapter? = null
    private var verticalAdapter : PhotoVerticalAdapter? = null

    override fun onResume() {
        super.onResume()
        checkIsAdminOrNot()

        initRecyclerViewHorizontal()
        initViewModelHorizontal()

        initRecyclerViewVertical()
        initViewModelVertical()
    }

    private fun initRecyclerViewHorizontal() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        binding.rvHorizontal.layoutManager = linearLayoutManager
        horizontalAdapter = PhotoHorizontalAdapter()
        binding.rvHorizontal.adapter = horizontalAdapter
    }

    private fun initViewModelHorizontal() {
        val viewModel = ViewModelProvider(this)[PhotoViewModelHorizontal::class.java]

        binding.horizontalProgressBar.visibility = View.VISIBLE
        viewModel.setHorizontal()
        viewModel.getHorizontal().observe(this) { photoList ->
            if (photoList.size > 0) {
                binding.noDataHorizontal.visibility = View.GONE
                horizontalAdapter!!.setData(photoList)
            } else {
                binding.noDataHorizontal.visibility = View.VISIBLE
            }
            binding.horizontalProgressBar.visibility = View.GONE
        }
    }

    private fun initRecyclerViewVertical() {
        val linearLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvVertical.layoutManager = linearLayoutManager
        verticalAdapter = PhotoVerticalAdapter()
        binding.rvVertical.adapter = verticalAdapter
    }

    private fun initViewModelVertical() {
        val viewModel = ViewModelProvider(this)[PhotoViewModelVertical::class.java]

        binding.verticalProgressBar.visibility = View.VISIBLE
        viewModel.setVertical()
        viewModel.getVertical().observe(this) { photoList ->
            if (photoList.size > 0) {
                binding.noDataVertical.visibility = View.GONE
                verticalAdapter!!.setData(photoList)
            } else {
                binding.noDataVertical.visibility = View.VISIBLE
            }
            binding.verticalProgressBar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvVertical.isNestedScrollingEnabled = false


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
                binding.fabAddPhotoBtn.visibility = View.GONE
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