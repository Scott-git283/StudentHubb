package com.example.studenth.model

import com.google.firebase.firestore.PropertyName

// Using a dedicated data class for the admin's user list
data class AdminUserData(
    // This property will hold the document ID (which is the user's UID)
    var uid: String = "",

    @get:PropertyName("first_name") @set:PropertyName("first_name") var firstName: String? = "",
    @get:PropertyName("last_name") @set:PropertyName("last_name") var lastName: String? = "",
    var email: String? = "",
    var role: String? = ""
)