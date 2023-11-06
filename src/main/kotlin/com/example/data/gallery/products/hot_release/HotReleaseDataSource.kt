package com.example.data.gallery.products.hot_release

import com.example.models.HotReleaseProduct
import com.example.models.response.ProductResponse

interface HotReleaseDataSource {
    suspend fun getAllHotReleaseProduct(limit: Int): List<ProductResponse>
    suspend fun getHotReleaseProduct(productId: Int): HotReleaseProduct?
    suspend fun addHotReleaseProduct(product: HotReleaseProduct): Int
    suspend fun deleteHotReleaseProduct(productId: Int): Int
}