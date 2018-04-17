package io.github.stlyx.trickymod

import android.content.res.XResources
import android.util.TypedValue.COMPLEX_UNIT_DIP
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge.log
import io.github.stlyx.trickymod.Global.PREF_NAVBAR_HEIGHT
import io.github.stlyx.trickymod.Global.tryWithLog
import io.github.stlyx.trickymod.utils.Preferences

class ModNavbar1 : IXposedHookZygoteInit {

    private val settings = Preferences()

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        // settings.init(Global.PREFERENCE_NAME_SETTINGS)

        tryWithLog {
            //val replaceHeight = settings.getFloat(PREF_NAVBAR_HEIGHT, 42f)
            val replaceHeight = 42f
            if (replaceHeight > 0 && replaceHeight <= 720) {
                log("stlyx: setting navbar height to $replaceHeight")
                XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height",
                        XResources.DimensionReplacement(replaceHeight, COMPLEX_UNIT_DIP))
                XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height_landscape",
                        XResources.DimensionReplacement(replaceHeight, COMPLEX_UNIT_DIP))
            }
        }
    }
}