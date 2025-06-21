package com.example.netspeedtile          // ← keep your package

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // NEW — tell the system to pad content by the status-bar height
        WindowCompat.setDecorFitsSystemWindows(window, true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, PrefsFragment())
                .commit()
        }
    }
}


/** Hosts the <PreferenceScreen> we made in res/xml/prefs_units.xml */
class PrefsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_units, rootKey)
    }

    /** Pad the list so the first row isn’t hidden under the status-bar */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            v.setPadding(0, top, 0, 0)           // add status-bar height
            insets                                // keep propagating
        }
    }
}
