package com.sounekatlogo.sounekatlogo.ui.photo

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekatlogo.databinding.ItemHorizontalBinding

class PhotoHorizontalAdapter : RecyclerView.Adapter<PhotoHorizontalAdapter.ViewHolder>() {

    private val photoList = ArrayList<PhotoHorizontalModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<PhotoHorizontalModel>) {
        photoList.clear()
        photoList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding : ItemHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: PhotoHorizontalModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                cv.setOnClickListener {
                    val photoDetailModel = PhotoDetailModel()
                    photoDetailModel.image = model.image
                    photoDetailModel.imageSize = model.imageSize
                    photoDetailModel.uid = model.uid
                    photoDetailModel.name = model.name
                    photoDetailModel.description = model.description

                    val intent = Intent(itemView.context, PhotoDetailActivity::class.java)
                    intent.putExtra(PhotoDetailActivity.EXTRA_DATA, photoDetailModel)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    override fun getItemCount(): Int = photoList.size
}