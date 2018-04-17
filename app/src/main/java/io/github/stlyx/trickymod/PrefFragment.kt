package io.github.stlyx.trickymod

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.stlyx.trickymod.Global.ACTION_UPDATE_PREF
import io.github.stlyx.trickymod.Global.FOLDER_SHARED_PREFS
import io.github.stlyx.trickymod.Global.MOD_BASE_DIR
import io.github.stlyx.trickymod.utils.FileUtil
import java.io.File

class PrefFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Move old shared preferences to device protected storage if it exists
            val oldPrefDir = "${context?.applicationInfo?.dataDir}/$FOLDER_SHARED_PREFS"
            val newPrefDir = "${context?.applicationInfo?.deviceProtectedDataDir}/$FOLDER_SHARED_PREFS"
            try {
                File(oldPrefDir).renameTo(File(newPrefDir))
            } catch (t: Throwable) {
                // Ignore this one
            }
            preferenceManager.setStorageDeviceProtected()
        }

        if (arguments != null) {
            val preferencesResId = arguments!!.getInt(ARG_PREF_RES)
            val preferencesName = arguments!!.getString(ARG_PREF_NAME)
            preferenceManager.sharedPreferencesName = preferencesName
            setPreferencesFromResource(preferencesResId, rootKey)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = context ?: return null
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            // setBackgroundColor(ContextCompat.getColor(context, R.color.card_background))
        }
    }

    override fun onStart() {
        super.onStart()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    // Reference: https://github.com/rovo89/XposedBridge/issues/206
    @SuppressLint("SetWorldReadable")
    override fun onPause() {
        // Set shared preferences as world readable.
        val folder = File("$MOD_BASE_DIR/$FOLDER_SHARED_PREFS")
        val file = File(folder, preferenceManager.sharedPreferencesName + ".xml")
        FileUtil.setWorldReadable(file)

        // Notify the backend to reload the preferences
        context?.sendBroadcast(Intent(ACTION_UPDATE_PREF))

        super.onPause()
    }

    override fun onStop() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
    }

    companion object {
        private const val ARG_PREF_RES = "preferencesResId"
        private const val ARG_PREF_NAME = "preferencesFileName"

        fun newInstance(preferencesResId: Int, preferencesName: String): PrefFragment {
            val fragment = PrefFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_PREF_RES, preferencesResId)
                putString(ARG_PREF_NAME, preferencesName)
            }
            return fragment
        }
    }
}