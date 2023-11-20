package com.example.breakkyuapp.models

import com.google.firebase.Timestamp

data class Break(
    var name: String,
    var time: Timestamp,
    var userId: String,
    var id: String,
    var note: String,
)