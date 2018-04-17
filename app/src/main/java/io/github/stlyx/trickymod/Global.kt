package io.github.stlyx.trickymod

import android.annotation.SuppressLint
import android.os.Build
import de.robv.android.xposed.XposedBridge
import kotlin.concurrent.thread

object Global {

    const val XPOSED_PACKAGE_NAME = "de.robv.android.xposed.installer"
    const val XPOSED_FILE_PROVIDER = "de.robv.android.xposed.installer.fileprovider"
    const val MOD_PACKAGE_NAME = "io.github.stlyx.trickymod"

    @SuppressLint("SdCardPath")
    private val DATA_DIR = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) "/data/data/" else "/data/user_de/0/"
    val XPOSED_BASE_DIR = "$DATA_DIR/$XPOSED_PACKAGE_NAME/"
    val MOD_BASE_DIR = "$DATA_DIR/$MOD_PACKAGE_NAME/"

    const val FOLDER_SHARED_PREFS = "shared_prefs"

    const val ACTION_UPDATE_PREF = "$MOD_PACKAGE_NAME.ACTION_UPDATE_PREF"

    const val PREFERENCE_NAME_SETTINGS = "settings"
    const val PREF_SPEC_DEFAULT_NOTIFICATION_CATEGORY = "pref_spec_default_notification_category"
    const val PREF_NAVBAR_HEIGHT = "pref_navbar_height"

    fun tryWithLog(func: () -> Unit) {
        try {
            func()
        } catch (t: Throwable) {
            XposedBridge.log(t)
        }
    }

    fun <T> tryOrNull(func: () -> T): T? =
            try {
                func()
            } catch (t: Throwable) {
                XposedBridge.log(t); null
            }

    fun tryWithThread(func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t -> XposedBridge.log(t) }
        }
    }
}