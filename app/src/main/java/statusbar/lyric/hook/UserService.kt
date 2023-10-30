package statusbar.lyric.hook

import android.app.ActivityManagerHidden
import android.content.AttributionSource
import android.content.pm.IPackageManager
import android.os.Build
import android.os.Bundle
import android.os.ServiceManager
import rikka.hidden.compat.ActivityManagerApis
import rikka.hidden.compat.adapter.UidObserverAdapter
import rikka.hidden.compat.util.SystemServiceBinder
import statusbar.lyric.BuildConfig
import statusbar.lyric.app.ServiceProvider
import statusbar.lyric.tools.LogTools.log
import kotlin.concurrent.thread

object UserService {

    private var appUid = 0

    private val service: LyricService by lazy { LyricService() }

    private val uidObserver = object: UidObserverAdapter() {
        override fun onUidActive(uid: Int) {
            if (uid != appUid) return
            runCatching {
                "start send binder".log()
                val provider = ActivityManagerApis.getContentProviderExternal(ServiceProvider.authorities, 0, null, null)
                if (provider == null) {
                    "provider is null".log()
                    return
                }
                val extras = Bundle()
                extras.putBinder("binder", service)
                val reply = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val attr = AttributionSource.Builder(1000).setPackageName("android").build()
                    provider.call(attr, ServiceProvider.authorities, "", null, extras)
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    provider.call("android", null, ServiceProvider.authorities, "", null, extras)
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    provider.call("android", ServiceProvider.authorities, "", null, extras)
                } else {
                    provider.call("android", "", null, extras)
                }
                if (reply == null) return
                "binder was sent".log()
            }.onFailure {
                it.log()
            }
        }
    }

    fun register() {
        val pms = SystemServiceBinder("package", IPackageManager.Stub::asInterface).get()
        appUid = getPackageUidCompat(pms, BuildConfig.APPLICATION_ID, 0, 0)
        thread {
            runCatching {
                waitSystemService("activity")
                ActivityManagerApis.registerUidObserver(
                    uidObserver,
                    ActivityManagerHidden.UID_OBSERVER_ACTIVE,
                    ActivityManagerHidden.PROCESS_STATE_UNKNOWN,
                    null
                )
            }.onFailure {
                it.log()
            }
        }
    }

    private fun getPackageUidCompat(pms: IPackageManager, packageName: String, flags: Long, userId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pms.getPackageUid(packageName, flags, userId)
        } else {
            pms.getPackageUid(packageName, flags.toInt(), userId)
        }
    }

    private fun waitSystemService(name: String) {
        while (ServiceManager.getService(name) == null) {
            Thread.sleep(1000)
        }
    }

}