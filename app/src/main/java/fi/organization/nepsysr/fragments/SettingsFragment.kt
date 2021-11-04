package fi.organization.nepsysr.fragments

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TimePicker
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.utilities.PopTimePicker

class SettingsFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceClickListener, TimePickerDialog.OnTimeSetListener,
    SharedPreferences.OnSharedPreferenceChangeListener
 {

    private var timePref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        // Inflates the given XML resource and replaces the current preference hierarchy (if any)
        // with the preference hierarchy rooted at key.
        setPreferencesFromResource(fi.organization.nepsysr.R.xml.root_preferences, rootKey)

        this.timePref = findPreference("alarmTime")
        loadSettings()
        timePref!!.setOnPreferenceClickListener(this);
    }

    // Loads things using shared preferences
    fun loadSettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val alarm = sp.getString("alarmTime", "")
        timePref!!.setSummary(alarm)
    }

    // Saves things using shared preferences
    fun saveSettings(x : String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = sp.edit()
        edit.putString("alarmTime",x).apply()
    }

    // Called when a preference has been clicked
    override open fun onPreferenceClick(pref: Preference?): Boolean {
        if ((pref!!.getKey() == "alarmTime")) {
            val newFragment = PopTimePicker()
            newFragment.setListener(this)
            newFragment.show(requireFragmentManager(), "timePicker")
        } else {
        }
        return true
    }

    // Receives the user-selected time after the user presses the OK button on the timer
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        var minuteLeadZero = String.format("%02d", minute)
        val x = "$hourOfDay:$minuteLeadZero"

        timePref = findPreference("alarmTime")

        // Sets the new time to the "alarm time" summary
        timePref!!.setSummary(x)
        saveSettings(x)
    }

     override fun onResume() {
         super.onResume()
         preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
     }

     override fun onPause() {
         preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
         super.onPause()
     }

     override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String) {
         if (p1 == "alarmTime") {
             val newTime = p0!!.getString("$p1", "")!!
             val alarm = AlarmHandler(requireContext())
             alarm.updateAlarm(newTime)
         }
     }
}