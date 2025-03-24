package com.clockwise

//import com.clockwise.app.Company
//import com.plcoding.bookpedia.core.data.safeCall
//import com.plcoding.bookpedia.core.domain.DataError
//import io.ktor.client.HttpClient
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import com.plcoding.bookpedia.core.domain.Result
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//

//private const val BASE_URL = "http://10.0.2.2:8080/v1/auth"
//
//class KtorRemoteAuthDataSource(
//    private val httpClient: HttpClient
//) {
//
//    suspend fun register(
//        username: String,
//        email: String,
//        password: String,
//        restaurantId: String
//    ): Result<RegisterResponseDto, DataError.Remote> {
//        return safeCall<RegisterResponseDto> {
//            httpClient.post("$BASE_URL/register") {
//                contentType(ContentType.Application.Json) // Set JSON content type
//                setBody(RegisterRequestDto(username, email, password, restaurantId))
//            }
//        }
//    }
//    suspend fun login(
//        username: String,
//        password: String
//    ): Result<LoginResponseDto, DataError.Remote> {
//        return safeCall<LoginResponseDto> {
//            httpClient.post("$BASE_URL/login") {
//                contentType(ContentType.Application.Json) // Set JSON content type
//                setBody(LoginRequestDto(username, password))
//            }
//        }
//    }
//
//    suspend fun getCompanies(): Result<List<Company>, DataError.Remote> {
////        return safeCall<List<CompanyResponseDto>> {
////            httpClient.get("$BASE_URL/companies")
////        }
//        return Result.Success(listOf(Company("1", "Company 1"), Company("2", "Company 2")))
//    }
//
//}

