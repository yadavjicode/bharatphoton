package app.bharatphoton.service.utils

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object CacheManager {
    private var simpleCache: SimpleCache? = null

    fun getCache(context: Context): SimpleCache {
        return simpleCache ?: synchronized(this) {
            if (simpleCache == null) {
                val cacheDir = File(context.cacheDir, "media_cache")
                val databaseProvider = StandaloneDatabaseProvider(context)
                simpleCache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024), databaseProvider)
            }
            simpleCache!!
        }
    }

    fun releaseCache() {
        simpleCache?.release()
        simpleCache = null
    }
}
