package com.alttabtechnologies.inventorytracker.model

/**
 * Represents the authenticated application operator.
 */

data class User(
    val id: Int = 0,
    val username: String,
    val fullName: String,
    val passwordHash: String
)
