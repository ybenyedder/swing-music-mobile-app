package com.android.swingmusic.service

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
class CustomDataSourceFactory(
    private val context: Context,
    private val accessToken: String
) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setDefaultRequestProperties(
                mapOf("Authorization" to "Bearer $accessToken")
            )
            setAllowCrossProtocolRedirects(true)
            setConnectTimeoutMs(10_000)
            setReadTimeoutMs(20_000)
        }
        val upstream = DefaultDataSource.Factory(context, defaultHttpDataSourceFactory)
        val cache = MediaCacheHolder.get(context)
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .createDataSource()
    }
}

@UnstableApi
internal object MediaCacheHolder {
    private const val CACHE_DIR = "media_cache"
    private const val CACHE_BYTES = 256L * 1024L * 1024L

    @Volatile
    private var instance: SimpleCache? = null

    fun get(context: Context): SimpleCache {
        return instance ?: synchronized(this) {
            instance ?: SimpleCache(
                File(context.cacheDir, CACHE_DIR),
                LeastRecentlyUsedCacheEvictor(CACHE_BYTES),
                StandaloneDatabaseProvider(context.applicationContext)
            ).also { instance = it }
        }
    }
}
