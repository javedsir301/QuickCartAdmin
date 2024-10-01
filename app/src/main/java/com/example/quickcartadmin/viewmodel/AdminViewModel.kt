package com.example.quickcartadmin.viewmodel

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickcartadmin.utils.Utils
import com.example.quickcartadmin.models.CartProducts
import com.example.quickcartadmin.models.Notification
import com.example.quickcartadmin.models.NotificationData
import com.example.quickcartadmin.models.Orders
import com.example.quickcartadmin.models.Product
import com.example.quickcartadmin.api.ApiUtilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _isImageUploaded = MutableStateFlow(false)
    var isImageUplaoded: StateFlow<Boolean> = _isImageUploaded

    private val _downloadUris = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadUris: StateFlow<ArrayList<String?>> = _downloadUris

    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved

    fun doesProductExist(
        productTitle: String,
        productCategory: String,
        productType: String
    ): LiveData<Boolean> {
        val existsLiveData = MutableLiveData<Boolean>()
        val dbRef = FirebaseDatabase.getInstance().getReference("Admins/AllProducts")

        dbRef.orderByChild("productTitle").equalTo(productTitle)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var exists = false
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product != null && product.productCategory == productCategory && product.productType == productType) {
                            exists = true
                            break
                        } else {
                            exists = false
                        }
                    }
                    existsLiveData.value = exists
                }

                override fun onCancelled(error: DatabaseError) {
                    existsLiveData.value = false
                }
            })

        return existsLiveData
    }

    fun saveImageInDB(imageUri: ArrayList<Uri>) {
        val downloadUris = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef =
                FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId().toString())
                    .child("images")
                    .child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUris.add(url.toString())

                if (downloadUris.size == imageUri.size) {
                    _isImageUploaded.value = true
                    _downloadUris.value = downloadUris
                }

            }
        }

    }

    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product.productRandomId}").setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins")
                    .child("ProductCategory/${product.productCategory}/${product.productRandomId}")
                    .setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child("ProductType/${product.productType}/${product.productRandomId}")
                            .setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }

    }


//    fun deleteProduct(productId: String) {
//        val db = FirebaseDatabase.getInstance().reference
//        // Delete from Admins/AllProducts
//        db.child("Admins/AllProducts/$productId").removeValue()
//            .addOnSuccessListener {
//                // Delete from Users/Products
//                db.child("Users/Products/$productId").removeValue()
//                    .addOnSuccessListener {
//                        Log.d(TAG, "Product deleted from both admin and user sections")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w(TAG, "Error deleting product from user section", e)
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error deleting product from admin section", e)
//            }
//    }





    fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category == "All" || prod?.productCategory == category) {

                        products.add(prod!!)
                    }
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)

        awaitClose { db.removeEventListener(eventListener) }
    }

    fun savingUpdateProducts(product: Product?) {
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts/${product?.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductCategory/${product?.productCategory}/${product?.productRandomId}")
            .setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins")
            .child("ProductType/${product?.productType}/${product?.productRandomId}")
            .setValue(product)
    }

    fun getOrderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow {
        val db =
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }

    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders")
            .orderByChild("orderStatus")


        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)

//                    if (order?.orderingUserId == Utils.getCurrentUserId()) {
//                        //order?.orderId
//                    orderList.add(order!!)
//                    }
//                    Log.d("checkrList", "orderingId ; " + order?.orderingUserId.toString())
//                    Log.d("checkrList", "orderId ; " + order?.orderId.toString())
//                    Log.d("checkrList", "uId ; " + Utils.getCurrentUserId().toString())
//                    Log.d("checkrList", "" + orderList.toString())
                    orderList.add(order!!)
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    fun updateOrderStatus(orderId: String, status: Int) {

        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
            .child("orderStatus").setValue(status)

    }

    fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
    }

    fun sendNotification(orderId: String, title: String, message: String) {
        Log.d("GGG", "inside sendNotification ")
        val getToken =
            FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
                .child("orderingUserId").get()

        getToken.addOnCompleteListener { task ->

            val userUid = task.result.getValue(String::class.java)
            val userToken = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users")
                .child(userUid!!).child("userToken").get()

            userToken.addOnCompleteListener {
                val notification = Notification(
                    it.result.getValue(String::class.java),
                    NotificationData(title, message)
                )
                ApiUtilities.notificationApi.sendNotification(notification).enqueue(object :
                    Callback<Notification> {
                    override fun onResponse(
                        call: Call<Notification>,
                        response: Response<Notification>
                    ) {
                        Log.d("GGG", "inside on success response ")
                        if (response.isSuccessful) {
                            Log.d("GGG", "sent")
                            Log.d("GGG", it.result.getValue(String::class.java).toString())
                        } else {
                            Log.d("GGG", "failed $response")
                        }
                    }

                    override fun onFailure(call: Call<Notification>, t: Throwable) {
                        Log.d("GGG", "failed " + t.message.toString())
                    }

                })
            }


        }.addOnFailureListener {
            Log.d("GGG", "inside failure listener ")
        }

    }

}