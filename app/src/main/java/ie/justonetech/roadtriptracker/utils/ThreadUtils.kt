package ie.justonetech.roadtriptracker.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

///////////////////////////////////////////////////////////////////////////////////////////////////
// ThreadUtils
// Singleton utility class providing threading helper functions
///////////////////////////////////////////////////////////////////////////////////////////////////

class ThreadUtils internal constructor() {

    val mainThreadExecutor = MainThreadExecutor()
    val networkThreadExecutor = Executors.newSingleThreadExecutor()

    val diskThreadExecutor = Executors.newFixedThreadPool(MAX_DISK_IO_THREADS, object : ThreadFactory {
        private val threadId = AtomicInteger(0)

        override fun newThread(r: Runnable?): Thread {
            return Thread(r).apply {
                name = "_disk_io_thread_${threadId.getAndIncrement()}"
            }
        }
    })

    fun runOnDiskThread(block: () -> Unit) = diskThreadExecutor.execute(block)
    fun runOnNetworkThread(block: () -> Unit) = networkThreadExecutor.execute(block)

    fun runOnMainThread(block: () -> Unit) {
        if(isMainThread())
            block()
        else
            postToMainThread(block)
    }

    fun postToMainThread (block: () -> Unit) {
        mainThreadExecutor.execute(block)
    }

    fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    class MainThreadExecutor internal constructor(): Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(runnable: Runnable) {
            mainThreadHandler.post(runnable)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val TAG = ThreadUtils::class.java.simpleName

        private const val MAX_DISK_IO_THREADS = 5

        @Volatile
        private var instance: ThreadUtils? = null
        private var LOCK = Any()

        operator fun invoke(): ThreadUtils {
            return instance ?: synchronized(LOCK) {
                instance ?: ThreadUtils().also {
                    instance = it
                }
            }
        }
    }
}