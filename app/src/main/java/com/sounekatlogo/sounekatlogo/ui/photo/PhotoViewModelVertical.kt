package com.sounekatlogo.sounekatlogo.ui.photo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PhotoViewModelVertical : ViewModel() {

    private val photoList = MutableLiveData<ArrayList<PhotoVerticalModel>>()
    private val listItems = ArrayList<PhotoVerticalModel>()
    private val TAG = PhotoViewModelVertical::class.java.simpleName

    fun setVertical() {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("image")
                .whereNotEqualTo("imageSize", "Horizontal")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = PhotoVerticalModel()
                            model.uid = "" + document["uid"]
                            model.description = "" + document["description"]
                            model.name = "" + document["name"]
                            model.imageSize = "" + document["imageSize"]
                            model.image = "" + document["image"]

                            listItems.add(model)
                        }
                        photoList.postValue(listItems)
                    } else {
                        Log.e(TAG, task.toString())
                    }
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getVertical(): LiveData<ArrayList<PhotoVerticalModel>> {
        return photoList
    }

}