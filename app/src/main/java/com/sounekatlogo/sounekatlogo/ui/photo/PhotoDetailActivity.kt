package com.sounekatlogo.sounekatlogo.ui.photo

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sounekatlogo.sounekatlogo.R
import com.sounekatlogo.sounekatlogo.databinding.ActivityPhotoDetailBinding
import java.io.File

class PhotoDetailActivity : AppCompatActivity() {

    private var _binding : ActivityPhotoDetailBinding? = null
    private val binding get() = _binding!!
    private var model : PhotoDetailModel? = null
    private var permission = 0
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        permission = if(it) {
            1
        } else {
            0
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhotoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIsAdminOrNot()

        model = intent.getParcelableExtra(EXTRA_DATA)
        when (model?.imageSize) {
            "Horizontal" -> {
                binding.horizontalCl.visibility = View.VISIBLE
                Glide.with(this)
                    .load(model?.image)
                    .into(binding.horizontal)
            }
            "Vertikal" -> {
                binding.verticalCl.visibility = View.VISIBLE
                Glide.with(this)
                    .load(model?.image)
                    .into(binding.vertical)
            }
            else -> {
                binding.persegiCl.visibility = View.VISIBLE
                Glide.with(this)
                    .load(model?.image)
                    .into(binding.persegi)
            }
        }
        binding.title.text = model?.name
        binding.description.text = model?.description

        binding.edit.setOnClickListener {
            val intent = Intent(this, PhotoAddEditActivity::class.java)
            intent.putExtra(PhotoAddEditActivity.EXTRA_DATA, model)
            intent.putExtra(PhotoAddEditActivity.OPTION, "edit")
            startActivity(intent)
        }

        binding.delete.setOnClickListener {
            showAlertDialog()
        }

        binding.constraintLayout2.setOnClickListener {
            val intent = Intent(this, PhotoExpandActivity::class.java)
            intent.putExtra(PhotoExpandActivity.EXTRA_PHOTO, model?.image)
            startActivity(intent)
        }

        binding.downloadBtn.setOnClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            Handler(Looper.getMainLooper()).postDelayed({
                if(permission==1) {
                    downloadImage()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }, 200)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun downloadImage() {
        try {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val imageLink = Uri.parse(model?.image)
            val request = DownloadManager.Request(imageLink)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("image/jpeg")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(model?.name)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+model?.name+".jpg")
            downloadManager.enqueue(request)
            Toast.makeText(this, "Photo berhasil di download", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("tag", e.message.toString())
            Toast.makeText(this, "Photo gagal di download", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Photo Ini")
            .setMessage("Apakah anda yakin ingin menghapus photo ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deletePhoto()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun deletePhoto() {
        FirebaseFirestore
            .getInstance()
            .collection("image")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    showSuccessDialog()
                } else {
                    showFailureDialog()
                }
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Menghapus Photo")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil, silahkan coba beberapa saat lagi!")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Menghapus Photo")
            .setMessage("Berhasil!")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                onBackPressed()
            }
            .show()
    }

    private fun checkIsAdminOrNot() {
        if(FirebaseAuth.getInstance().currentUser != null) {
            binding.edit.visibility = View.VISIBLE
            binding.delete.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}