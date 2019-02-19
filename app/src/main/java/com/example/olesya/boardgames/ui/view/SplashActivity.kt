package com.example.olesya.boardgames.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.olesya.boardgames.database.AppDatabase
import com.example.olesya.boardgames.database.ImageHolderDao
import com.example.olesya.boardgames.entity.ImageHolder
import com.example.olesya.boardgames.ui.main.MainActivity
import com.example.olesya.boardgames.ui.main.MenuViewModel.DB_PERSEPHONE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseDb = FirebaseFirestore.getInstance()
        val dao = AppDatabase.getInstance(this).imagesDao()
        val holders = mutableListOf<ImageHolder>()
        firebaseDb.collection(DB_PERSEPHONE)
                .get()
                .addOnCompleteListener { task ->
                    run {
                        if (task.isSuccessful) {
                            for (document: QueryDocumentSnapshot in task.result!!) {
                                holders.add(ImageHolder(document.data["img"] as String))
                            }
                        } else {
                            Toast.makeText(this, "trouble", Toast.LENGTH_SHORT).show();
                        }

                        insertData(dao, holders)
                        startActivity()
                    }
                }
    }

    private fun insertData(dao: ImageHolderDao, holders: MutableList<ImageHolder>) {
        dao.insertAll(holders)
    }

    private fun startActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}