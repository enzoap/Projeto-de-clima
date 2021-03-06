package com.example.clima.data.datasource

import com.example.clima.data.api.WeatherService
import com.example.clima.data.exceptions.WeatherErrorResponse
import com.example.clima.data.mapper.toDomain
import com.example.clima.domain.model.WeatherModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class WeatherRemoteDataSource(
    private val weatherService: WeatherService
): WeatherDataSource {

    override fun getInfo(): Flow<WeatherModel> = flow {
        emit(weatherService.getInfo().toDomain())
    }.catch { error -> handleError(error) }

    private fun handleError(error: Throwable) {
        val handleException = runCatching {
            val httpException = error as HttpException
            Gson().fromJson(httpException
                .response()
                ?.errorBody()
                ?.string()
                .orEmpty(), WeatherErrorResponse::class.java)
        }.getOrDefault(error) as Throwable

        throw handleException
    }
}