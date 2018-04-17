package io.github.stlyx.trickymod

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import io.github.stlyx.trickymod.Global.PREFERENCE_NAME_SETTINGS
import io.github.stlyx.trickymod.Global.PREF_NAVBAR_HEIGHT
import io.github.stlyx.trickymod.Global.tryWithLog
import io.github.stlyx.trickymod.utils.Preferences

class ModNavbar2 : IXposedHookLoadPackage {

    private val settings = Preferences()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        tryWithLog {
            if (lpparam.packageName == "android" && lpparam.processName == "android") {
                hookExpandedDesktop(lpparam)
            }
        }
    }

    private fun hookExpandedDesktop(lpparam: XC_LoadPackage.LoadPackageParam) {
        settings.init(PREFERENCE_NAME_SETTINGS)

        val factor = settings.getFloat(PREF_NAVBAR_HEIGHT, 100f) / 100f
        if (factor > 0 && factor < 1) {

        }
    }
}