package statusbar.lyric.hook

import statusbar.lyric.BuildConfig
import statusbar.lyric.common.ILyricService

class LyricService : ILyricService.Stub() {

    override fun getRemoteVersion(): Int {
        return BuildConfig.VERSION_CODE + BuildConfig.BUILD_TIME.hashCode()
    }

}