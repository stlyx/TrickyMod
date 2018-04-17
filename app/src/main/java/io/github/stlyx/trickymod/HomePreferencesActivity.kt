package io.github.stlyx.trickymod

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.github.stlyx.trickymod.Global.PREFERENCE_NAME_SETTINGS
import kotlinx.android.synthetic.main.activity_home_preferences.*
import kotlinx.android.synthetic.main.content_home_preferences.*

class HomePreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_preferences)
        setSupportActionBar(toolbar)

        if (main_container != null && savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, PrefFragment.newInstance(R.xml.pref_settings, PREFERENCE_NAME_SETTINGS))
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home_preferences, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_restart ->
                return true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
