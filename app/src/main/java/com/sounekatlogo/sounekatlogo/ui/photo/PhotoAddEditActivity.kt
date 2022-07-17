package com.sounekatlogo.sounekatlogo.ui.photo

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sounekatlogo.sounekatlogo.R
import com.sounekatlogo.sounekatlogo.databinding.ActivityPhotoAddEditBinding
import com.sounekatlogo.sounekatlogo.ui.HomepageActivity

class PhotoAddEditActivity : AppCompatActivity() {

    private var _binding: ActivityPhotoAddEditBinding? = null
    private val binding get() = _binding!!

    /// variable untuk menampung gambar dari galeri handphone
    private var image: String? = null

    /// variable untuk permission ke galeri handphone
    private val REQUEST_IMAGE_GALLERY = 1001
    private var option: String? = null
    private var imageSize: String? = null
    private var model : PhotoDetailModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPhotoAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        option = intent.getStringExtra(OPTION)
        showDropdownImageSize()
        if (option == "add") {
            binding.title.text = "Unggah Photo Baru"
        } else {
            binding.title.text = "Edit Photo"
            model = intent.getParcelableExtra(EXTRA_DATA)
            image = model?.image
            imageSize = model?.imageSize

            when (imageSize) {
                "Vertikal" -> {
                    binding.vertical.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(image)
                        .into(binding.vertical)
                }
                "Horizontal" -> {
                    binding.horizontal.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(image)
                        .into(binding.horizontal)

                }
                "Persegi" -> {
                    binding.persegi.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(image)
                        .into(binding.persegi)

                }
            }
            binding.name.setText(model?.name)
            binding.description.setText(model?.description)

        }

        binding.addPhoto.setOnClickListener {
            if (imageSize == null) {
                Toast.makeText(
                    this,
                    "Silahkan pilih bentuk photo terlebih dahulu!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_IMAGE_GALLERY)
            }
        }

        binding.saveBtn.setOnClickListener {
            formValidation()
        }

    }

    private fun formValidation() {
        val name = binding.name.text.toString().trim()
        val description = binding.description.text.toString().trim()

        if (image == null) {
            Toast.makeText(this, "Photo tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else if (name.isEmpty()) {
            Toast.makeText(this, "Nama Photo tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressBar.visibility = View.VISIBLE

            if (intent.getStringExtra(OPTION) == "add") {
                val uid = System.currentTimeMillis().toString()
                val data = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "description" to description,
                    "imageSize" to imageSize,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("image")
                    .document(uid)
                    .set(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.name.setText("")
                            binding.description.setText("")
                            imageSize = null
                            image = null
                            showSuccessDialog()
                        } else {
                            showFailureDialog()
                        }
                    }


            } else {
                val data = mapOf(
                    "name" to name,
                    "description" to description,
                    "imageSize" to imageSize,
                    "image" to image,
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("image")
                    .document(model?.uid!!)
                    .update(data)
                    .addOnCompleteListener {
                        binding.progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            binding.name.setText("")
                            binding.description.setText("")
                            imageSize = null
                            image = null

                            Toast.makeText(this, "Berhasil mengedit foto!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomepageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()

                        } else {
                            showFailureDialog()
                        }
                    }
            }
        }
    }

    private fun showDropdownImageSize() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.image_size, android.R.layout.simple_list_item_1
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        binding.status.setAdapter(adapter)
        binding.status.setOnItemClickListener { _, _, _, _ ->
            imageSize = binding.status.text.toString()
        }
    }

    /// ini adalah program untuk menambahkan gambar kedalalam halaman ini
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                uploadImageToDatabase(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadImageToDatabase(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()


        val imageFileName = "photo/image_" + System.currentTimeMillis() + ".png"
        /// proses upload gambar ke databsae
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri ->

                        /// proses upload selesai, berhasil
                        mProgressDialog.dismiss()
                        image = uri.toString()
                        binding.addPhoto.text = "Ubah Photo"
                        when (imageSize) {
                            "Vertikal" -> {
                                binding.vertical.visibility = View.VISIBLE
                                binding.horizontal.visibility = View.GONE
                                binding.persegi.visibility = View.GONE
                                Glide.with(this)
                                    .load(image)
                                    .into(binding.vertical)
                            }
                            "Horizontal" -> {
                                binding.horizontal.visibility = View.VISIBLE
                                binding.vertical.visibility = View.GONE
                                binding.persegi.visibility = View.GONE
                                Glide.with(this)
                                    .load(image)
                                    .into(binding.horizontal)
                            }
                            "Persegi" -> {
                                binding.persegi.visibility = View.VISIBLE
                                binding.horizontal.visibility = View.GONE
                                binding.vertical.visibility = View.GONE
                                Glide.with(this)
                                    .load(image)
                                    .into(binding.persegi)
                            }
                            else -> {

                            }
                        }

                    }

                    /// proses upload selesai, gagal
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            /// proses upload selesai, gagal
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Mengunggah Photo")
            .setMessage("Ups, sepertinya koneksi internet anda kurang stabil!, silahkan coba beberapa saat lagi")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Mengunggah Photo Baru!")
            .setMessage("Photo akan segera terbit di halaman utama")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val OPTION = "option"
        const val EXTRA_DATA = "data"
    }
}