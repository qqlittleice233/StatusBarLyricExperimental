package statusbar.lyric.app

import android.os.IBinder
import android.os.IBinder.DeathRecipient
import statusbar.lyric.BuildConfig
import statusbar.lyric.common.ILyricService

object ServiceClient : DeathRecipient, ILyricService {
    override fun asBinder(): IBinder? = service?.asBinder()

    override fun getRemoteVersion(): Int {
        return service?.remoteVersion ?: -1
    }

    enum class STATUS {
        NOT_CONNECTED,
        CONNECTED,
        VERSION_MISMATCH,
    }

    private val localVersion = BuildConfig.VERSION_CODE + BuildConfig.BUILD_TIME.hashCode()

    @Volatile
    private var service : ILyricService? = null

    fun getStatus(): STATUS {
        val service = service ?: return STATUS.NOT_CONNECTED
        val remoteVersion = service.remoteVersion
        return if (remoteVersion == localVersion) STATUS.CONNECTED else STATUS.VERSION_MISMATCH
    }

    fun linkService(binder: IBinder) {
        service = ILyricService.Stub.asInterface(binder)
        binder.linkToDeath(this, 0)
    }

    override fun binderDied() {
        service = null
    }

}