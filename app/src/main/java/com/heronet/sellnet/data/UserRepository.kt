package com.heronet.sellnet.data

import com.heronet.sellnet.model.AuthData
import com.heronet.sellnet.util.Resource
import com.heronet.sellnet.web.Location
import com.heronet.sellnet.web.LoginDto
import com.heronet.sellnet.web.RegisterDto
import com.heronet.sellnet.web.SellnetApi
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(private val sellnetApi: SellnetApi) {
    suspend fun loginUser(loginDto: LoginDto): Resource<AuthData> {
        val data = try {
            sellnetApi.login(loginDto)
        } catch (e: HttpException) {
            return Resource.Error(getError(e.response()!!.errorBody()!!), null)
        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(data)
    }
    suspend fun registerUser(registerDto: RegisterDto): Resource<AuthData> {
        val data = try {
            sellnetApi.register(registerDto)
        } catch (e: HttpException) {
            val resError = getError(e.response()!!.errorBody()!!)
            return if (resError.contains("errors")) {
                val jsonObject = JSONObject(resError)
                val errors = jsonObject.getJSONArray("errors")
                val errorMessages = mutableListOf<String>()
                for (i in 0 until errors.length()) {
                    if(!errors.optJSONObject(i).getString("description").contains("Username")) // Username = Email in the api, so we ignore username errors
                        errorMessages.add(errors.optJSONObject(i).getString("description"))
                }
                Resource.Error("MultipleErrors", errorMessages)
            } else {
                Resource.Error(resError, null)
            }


        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(data)
    }
    suspend fun refreshToken(token: String, authData: AuthData): Resource<AuthData> {
        val data = try {
            sellnetApi.refreshToken("Bearer $token", authData)
        } catch (e: HttpException) {
            return Resource.Error("An error occurred", null)
        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(data)
    }
    suspend fun getLocations(): Resource<Location> {
        val data = try {
            sellnetApi.getLocations()
        } catch (e: HttpException) {
            return Resource.Error(getError(e.response()!!.errorBody()!!), null)
        } catch (e: Exception) {
            return Resource.Error("No Internet Connection", null)
        }
        return Resource.Success(data)
    }
    private fun getError(response: ResponseBody) = response.string()
}