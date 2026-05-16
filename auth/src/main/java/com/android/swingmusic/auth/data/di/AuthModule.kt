package com.android.swingmusic.auth.data.di


import android.content.Context
import com.android.swingmusic.auth.data.api.service.AuthApiService
import com.android.swingmusic.auth.data.datastore.AuthTokensDataStore
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    private const val HTTP_CACHE_DIR = "swing_http_cache"
    private const val HTTP_CACHE_BYTES = 50L * 1024L * 1024L

    @Provides
    @Singleton
    fun providesAuthTokenDataStore(
        @ApplicationContext context: Context
    ): AuthTokensDataStore {
        return AuthTokensDataStore(context = context.applicationContext)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
            redactHeader("Authorization")
            redactHeader("Cookie")
        }
        val cache = Cache(File(context.cacheDir, HTTP_CACHE_DIR), HTTP_CACHE_BYTES)
        return OkHttpClient.Builder()
            .cache(cache)
            .connectionPool(ConnectionPool(10, 10, TimeUnit.MINUTES))
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .addInterceptor(ChuckerInterceptor(context))
            .callTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesAuthApiService(okHttpClient: OkHttpClient): AuthApiService {
        return Retrofit.Builder()
            .baseUrl("http://placeholder/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AuthApiService::class.java)
    }
}
