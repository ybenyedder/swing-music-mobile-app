package com.android.swingmusic.network.data.di

import com.android.swingmusic.network.data.api.service.NetworkApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesNetworkApiService(
        okHttpClient: OkHttpClient
    ): NetworkApiService {
        return Retrofit.Builder()
            .baseUrl("http://placeholder/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(NetworkApiService::class.java)
    }
}
