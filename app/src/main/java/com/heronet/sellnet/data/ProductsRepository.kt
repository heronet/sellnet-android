package com.heronet.sellnet.data

import android.util.Log
import com.heronet.sellnet.model.Product
import com.heronet.sellnet.util.Resource
import com.heronet.sellnet.web.ApiResponse
import com.heronet.sellnet.web.SellnetApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val api: SellnetApi) {
    suspend fun getProducts(
        pageNumber: Int,
        pageSize: Int,
        name: String? = null,
        city: String? = null,
        division: String? = null,
        category: String? = null,
        sortParam: String? = null,
        sellerId: String? = null
    ): Resource<ApiResponse<List<Product>>> {
        val productsResponse = try {
            api.getProducts(
                pageNumber,
                pageSize,
                name,
                city,
                division,
                category,
                sortParam,
                sellerId
            )
        } catch (e: HttpException) {
            return Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            Log.d("ER", e.toString())
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(productsResponse, null)
    }

    suspend fun getProduct(id: String): Resource<Product> {
        val product = try {
            api.getProduct(id)
        } catch (e: HttpException) {
            return Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(product, null)
    }

    suspend fun deleteProduct(id: String, token: String): Resource<Boolean> {
        return try {
            api.deleteProduct(authorization = token, id = id)
            Resource.Success(true)
        } catch (e: HttpException) {
            Log.d("ERR", e.toString())
            Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            Resource.Error("No Internet Connection", null)
        }
    }

    suspend fun addProduct(
        name: RequestBody,
        price: RequestBody,
        description: RequestBody,
        category: RequestBody,
        photos: List<MultipartBody.Part>,
        token: String
    ): Resource<Boolean> {
        return try {
            api.addProduct(name, price, description, category, photos, token)
            Resource.Success(true)
        } catch (e: HttpException) {
            Log.d("ERR", getError(e.response()!!.errorBody()!!))
            Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            Log.d("ERR", e.toString())
            Resource.Error("No Internet Connection", null)
        }
    }

    private fun getError(response: ResponseBody) = response.string()
}