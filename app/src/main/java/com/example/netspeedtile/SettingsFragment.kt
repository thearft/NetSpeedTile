package com.example.netspeedtile

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/** The same screen you had in SettingsActivity, but as a fragment */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_units, rootKey)
    }
}
