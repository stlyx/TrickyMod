package io.github.stlyx.trickymod.utils

import android.content.*
import de.robv.android.xposed.XSharedPreferences
import io.github.stlyx.trickymod.Global.ACTION_UPDATE_PREF
import io.github.stlyx.trickymod.Global.FOLDER_SHARED_PREFS
import io.github.stlyx.trickymod.Global.MOD_BASE_DIR
import io.github.stlyx.trickymod.Global.MOD_PACKAGE_NAME
import io.github.stlyx.trickymod.Global.tryWithLog
import io.github.stlyx.trickymod.Global.tryWithThread
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap

class Preferences : SharedPreferences {

    // loadChannel resumes all the threads waiting for the preference loading.
    private val loadChannel = WaitChannel()

    // listCache caches the string lists in memory to speed up getStringList()
    private val listCache: MutableMap<String, List<String>> = ConcurrentHashMap()

    // content is the preferences generated by the frond end of Wechat Magician.
    private var content: XSharedPreferences? = null

    fun init(preferencesName: String) {
        tryWithThread {
            try {
                // Also load the preferences in the data directories.
                val preferencePath = "$MOD_BASE_DIR/$FOLDER_SHARED_PREFS/$preferencesName.xml"
                content = XSharedPreferences(File(preferencePath))
            } catch (_: FileNotFoundException) {
                // Ignore this one
            } finally {
                loadChannel.done()
                cacheStringList()
            }
        }
    }

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadChannel.wait()

            // Reload and cache the shared preferences
            content?.reload()
            cacheStringList()
        }
    }

    // listen registers a receiver to listen the update events from the frontend.
    fun listen(context: Context) {
        tryWithLog {
            context.registerReceiver(updateReceiver, IntentFilter(ACTION_UPDATE_PREF))
        }
    }

    fun cacheStringList() {
//        PREFERENCE_STRING_LIST_KEYS.forEach { key ->
//            listCache[key] = getString(key, "").split(" ").filter { it.isNotEmpty() }
//        }
    }

    override fun contains(key: String): Boolean {
        return content?.contains(key) ?: false
    }

    override fun getAll(): MutableMap<String, *>? {
        return content?.all
    }

    private fun getValue(key: String): Any? {
        loadChannel.wait()
        return all?.get(key)
    }

    private inline fun <reified T>getValue(key: String, defValue: T): T {
        return getValue(key) as? T ?: defValue
    }

    override fun getInt(key: String, defValue: Int): Int = getValue(key, defValue)

    override fun getLong(key: String, defValue: Long): Long = getValue(key, defValue)

    override fun getFloat(key: String, defValue: Float): Float = getValue(key, defValue)

    override fun getBoolean(key: String, defValue: Boolean): Boolean = getValue(key, defValue)

    override fun getString(key: String, defValue: String): String = getValue(key, defValue)

    override fun getStringSet(key: String, defValue: MutableSet<String>): MutableSet<String> = getValue(key, defValue)

    fun getStringList(key: String, defValue: List<String>): List<String> {
        loadChannel.wait()
        return listCache[key] ?: defValue
    }

    override fun edit(): SharedPreferences.Editor {
        throw UnsupportedOperationException()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw UnsupportedOperationException()
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw UnsupportedOperationException()
    }
}
