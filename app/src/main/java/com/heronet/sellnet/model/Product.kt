package com.heronet.sellnet.model

data class Product(
    val brand: Any?,
    val buyersCount: Int,
    val category: String,
    val categoryId: String,
    val city: String,
    val createdAt: String,
    val description: String,
    val division: String,
    val id: String,
    val name: String,
    val photos: List<Photo>,
    val price: Int,
    val subCategory: Any?,
    val supplier: Supplier,
    val thumbnail: Thumbnail
) {
    data class Photo(
        val imageUrl: String,
        val publicId: String
    )

    data class Supplier(
        val email: String,
        val id: String,
        val name: String,
        val phone: String,
        val roles: Any?
    )

    data class Thumbnail(
        val imageUrl: String,
        val publicId: String
    )
}