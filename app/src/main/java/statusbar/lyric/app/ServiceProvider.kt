package statusbar.lyric.app

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import statusbar.lyric.BuildConfig
import statusbar.lyric.tools.LogTools.log

class ServiceProvider: ContentProvider() {

    companion object {
        const val authorities = "statusbar.lyric.app.ServiceProvider"
    }
    override fun onCreate(): Boolean = false

    override fun query(p0: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (callingPackage != "android" || extras == null) return null
        val binder = extras.getBinder("binder") ?: return null
        val service = ServiceClient.linkService(binder)
        ServiceClient.remoteVersion.log()

        // bind service here
        return Bundle()
    }
}