package com.sounekatlogo.sounekatlogo.ui.photo

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sounekatlogo.sounekatlogo.databinding.ItemPersegiBinding
import com.sounekatlogo.sounekatlogo.databinding.ItemVerticalBinding

class PhotoVerticalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photoList = ArrayList<PhotoVerticalModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<PhotoVerticalModel>) {
        photoList.clear()
        photoList.addAll(items)
        notifyDataSetChanged()
    }


    companion object {
        private const val PHOTO_VERTICAL = 0
        private const val PHOTO_RECTANGLE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == PHOTO_VERTICAL) {
            val binding = ItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VerticalViewHolder(binding)
        } else {
            val binding = ItemPersegiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RectangleViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(photoList[position].imageSize == "Vertikal") {
            (holder as VerticalViewHolder).bind(photoList[position])
        } else{
            (holder as RectangleViewHolder).bind(photoList[position])
        }

    }

    override fun getItemCount(): Int = photoList.size

    override fun getItemViewType(position: Int) : Int{
        //get currently signed user

        return if(photoList[position].imageSize == "Vertikal"){
            PHOTO_VERTICAL
        } else {
            PHOTO_RECTANGLE
        }
    }

    inner class VerticalViewHolder(private val binding: ItemVerticalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: PhotoVerticalModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(list.image)
                    .into(image)

                cv.setOnClickListener {
                    val photoDetailModel = PhotoDetailModel()
                    photoDetailModel.image = list.image
                    photoDetailModel.imageSize = list.imageSize
                    photoDetailModel.uid = list.uid
                    photoDetailModel.name = list.name
                    photoDetailModel.description = list.description

                    val intent = Intent(itemView.context, PhotoDetailActivity::class.java)
                    intent.putExtra(PhotoDetailActivity.EXTRA_DATA, photoDetailModel)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    inner class RectangleViewHolder(private val binding: ItemPersegiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: PhotoVerticalModel) {
            with(binding) {

                Glide.with(itemView.context)
                    .load(list.image)
                    .into(image)

                cv.setOnClickListener {
                    val photoDetailModel = PhotoDetailModel()
                    photoDetailModel.image = list.image
                    photoDetailModel.imageSize = list.imageSize
                    photoDetailModel.uid = list.uid
                    photoDetailModel.name = list.name
                    photoDetailModel.description = list.description

                    val intent = Intent(itemView.context, PhotoDetailActivity::class.java)
                    intent.putExtra(PhotoDetailActivity.EXTRA_DATA, photoDetailModel)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }
}