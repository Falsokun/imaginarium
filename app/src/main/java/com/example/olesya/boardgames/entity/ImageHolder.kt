package com.example.olesya.boardgames.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageHolder(@PrimaryKey val imageUrl: String)