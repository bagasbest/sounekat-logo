package com.sounekatlogo.sounekatlogo.ui.photo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PhotoViewModelHorizontal : ViewModel() {

    private val photoList = MutableLiveData<ArrayList<PhotoHorizontalModel>>()
    private val listItems = ArrayList<PhotoHorizontalModel>()
    private val TAG = PhotoViewModelHorizontal::class.java.simpleName

    fun setHorizontal() {
        listItems.clear()

        try {
            FirebaseFirestore
                .getInstance()
                .collection("image")
                .whereEqualTo("imageSize", "Horizontal")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val model = PhotoHorizontalModel()
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

    fun getHorizontal(): LiveData<ArrayList<PhotoHorizontalModel>> {
        return photoList
    }


}