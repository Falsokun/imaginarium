package com.example.olesya.boardgames.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class ImageHolder(@PrimaryKey val imageUrl: String)